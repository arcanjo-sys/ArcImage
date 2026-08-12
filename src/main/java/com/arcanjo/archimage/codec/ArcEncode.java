package com.arcanjo.archimage.codec;

import com.arcanjo.archimage.format.Format;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.zip.Deflater;

/**
 * ARC 2.0
 *
 * Pipeline:
 *
 * RGB
 *  ↓
 * Filtro adaptativo
 *  ↓
 * RLE
 *  ↓
 * DEFLATE (LZ77 + Huffman)
 *  ↓
 * ARC
 */
public class ArcEncode {

    /*
     * Quantidade de filtros disponíveis.
     */
    private static final int FILTER_COUNT = 5;

    private final BufferedImage img;
    private final File fileOutput;

    public ArcEncode(String input, String output) throws IOException {

        this.img = ImageIO.read(new File(input));

        if (this.img == null) {
            throw new IOException(
                    "Não foi possível carregar a imagem: " + input
            );
        }

        this.fileOutput = new File(output);
    }

    // ================================================================
    // UTILITÁRIOS
    // ================================================================

    private static byte[] intToUInt16Bytes(int value) {

        if (value < 0 || value > 65535) {
            throw new IllegalArgumentException(
                    "Valor fora de UInt16: " + value
            );
        }

        return new byte[]{
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF)
        };
    }

    private static void writeUInt32(
            OutputStream out,
            int value
    ) throws IOException {

        out.write((value >>> 24) & 0xFF);
        out.write((value >>> 16) & 0xFF);
        out.write((value >>> 8) & 0xFF);
        out.write(value & 0xFF);
    }

    /**
     * Escreve:
     *
     * TAG (4 bytes)
     * SIZE (2 bytes)
     * DATA
     */
    private static void writeMetadata(
            OutputStream out,
            byte[] tag,
            byte[] data
    ) throws IOException {

        if (tag.length != 4) {
            throw new IOException(
                    "Tag precisa possuir exatamente 4 bytes."
            );
        }

        if (data.length > 65535) {
            throw new IOException(
                    "Metadado excede 65535 bytes."
            );
        }

        out.write(tag);
        out.write(intToUInt16Bytes(data.length));
        out.write(data);
    }

    // ================================================================
    // PAETH
    // ================================================================

    private static int paethPredictor(
            int left,
            int up,
            int upperLeft
    ) {

        int p = left + up - upperLeft;

        int pa = Math.abs(p - left);
        int pb = Math.abs(p - up);
        int pc = Math.abs(p - upperLeft);

        if (pa <= pb && pa <= pc) {
            return left;
        }

        if (pb <= pc) {
            return up;
        }

        return upperLeft;
    }

    // ================================================================
    // FILTRO
    // ================================================================

    /**
     * Cria uma linha filtrada.
     *
     * Filtros:
     *
     * 0 = None
     * 1 = Sub
     * 2 = Up
     * 3 = Average
     * 4 = Paeth
     */
    private static byte[] filterRow(
            byte[] current,
            byte[] previous,
            int filter
    ) {

        byte[] result =
                new byte[current.length];

        for (int i = 0; i < current.length; i++) {

            int raw =
                    current[i] & 0xFF;

            int left =
                    i >= 3
                            ? current[i - 3] & 0xFF
                            : 0;

            int up =
                    previous == null
                            ? 0
                            : previous[i] & 0xFF;

            int upperLeft =
                    previous == null || i < 3
                            ? 0
                            : previous[i - 3] & 0xFF;

            int prediction;

            switch (filter) {

                case 0:
                    prediction = 0;
                    break;

                case 1:
                    prediction = left;
                    break;

                case 2:
                    prediction = up;
                    break;

                case 3:
                    prediction =
                            (left + up) / 2;
                    break;

                case 4:
                    prediction =
                            paethPredictor(
                                    left,
                                    up,
                                    upperLeft
                            );
                    break;

                default:
                    throw new IllegalArgumentException(
                            "Filtro inválido: " + filter
                    );
            }

            result[i] =
                    (byte) (
                            (raw - prediction)
                                    & 0xFF
                    );
        }

        return result;
    }

    /**
     * Score utilizado para escolher o melhor filtro.
     */
    private static long filterScore(
            byte[] data
    ) {

        long score = 0;

        for (byte value : data) {

            int signed =
                    value;

            score += Math.abs(signed);
        }

        return score;
    }

    // ================================================================
    // RLE
    // ================================================================

    /**
     * RLE binário.
     *
     * Formato:
     *
     * 0xxxxxxx = literal
     * 1xxxxxxx = repetição
     *
     * Literal:
     *
     * quantidade = (control & 0x7F) + 1
     * depois vêm N bytes.
     *
     * Repetição:
     *
     * quantidade = (control & 0x7F) + 1
     * depois vem 1 byte que será repetido.
     */
    private static byte[] rleEncode(
            byte[] input
    ) throws IOException {

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        int pos = 0;

        while (pos < input.length) {

            // ========================================================
            // TENTA ENCONTRAR REPETIÇÃO
            // ========================================================

            int runLength = 1;

            while (
                    pos + runLength < input.length &&
                            runLength < 128 &&
                            input[pos + runLength]
                                    == input[pos]
            ) {

                runLength++;
            }

            /*
             * RLE só compensa a partir de 3 bytes:
             *
             * literal de 3 = 4 bytes
             * RLE de 3     = 2 bytes
             */
            if (runLength >= 3) {

                out.write(
                        0x80 | (runLength - 1)
                );

                out.write(
                        input[pos] & 0xFF
                );

                pos += runLength;

                continue;
            }

            // ========================================================
            // LITERAL
            // ========================================================

            int literalStart = pos;
            int literalLength = 0;

            while (
                    pos < input.length &&
                            literalLength < 128
            ) {

                int run = 1;

                while (
                        pos + run < input.length &&
                                run < 3 &&
                                input[pos + run]
                                        == input[pos]
                ) {

                    run++;
                }

                /*
                 * Se encontramos uma repetição >= 3,
                 * paramos o literal para que a próxima
                 * iteração use RLE.
                 */
                if (run >= 3) {
                    break;
                }

                pos++;
                literalLength++;
            }

            if (literalLength == 0) {
                /*
                 * Segurança para evitar loop infinito.
                 */
                out.write(Format.PADDING);
                out.write(input[pos] & 0xFF);
                pos++;
            } else {

                out.write(
                        literalLength - 1
                );

                out.write(
                        input,
                        literalStart,
                        literalLength
                );
            }
        }

        return out.toByteArray();
    }

    // ================================================================
    // DEFLATE
    // ================================================================

    private static byte[] deflate(
            byte[] input
    ) throws IOException {

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        Deflater deflater =
                new Deflater(
                        Deflater.BEST_COMPRESSION
                );

        try {

            deflater.setInput(input);
            deflater.finish();

            byte[] buffer =
                    new byte[8192];

            while (!deflater.finished()) {

                int count =
                        deflater.deflate(buffer);

                if (count == 0 &&
                        !deflater.finished()) {

                    throw new IOException(
                            "Deflater não conseguiu produzir dados."
                    );
                }

                out.write(
                        buffer,
                        0,
                        count
                );
            }

        } finally {
            deflater.end();
        }

        return out.toByteArray();
    }

    // ================================================================
    // CONSTRUÇÃO DOS DADOS
    // ================================================================

    /**
     * Converte a imagem para:
     *
     * [filter][filtered RGB][filter][filtered RGB]...
     */
    private byte[] buildFilteredData(
            int width,
            int height
    ) {

        ByteArrayOutputStream filtered =
                new ByteArrayOutputStream(
                        width * height * 3 + height
                );

        byte[] previous = null;

        for (int y = 0; y < height; y++) {

            byte[] current =
                    new byte[width * 3];

            int index = 0;

            for (int x = 0; x < width; x++) {

                int rgb =
                        img.getRGB(x, y);

                current[index++] =
                        (byte) ((rgb >> 16) & 0xFF);

                current[index++] =
                        (byte) ((rgb >> 8) & 0xFF);

                current[index++] =
                        (byte) (rgb & 0xFF);
            }

            // ========================================================
            // TESTA TODOS OS FILTROS
            // ========================================================

            byte[] best = null;
            int bestFilter = 0;
            long bestScore = Long.MAX_VALUE;

            for (int filter = 0;
                 filter < FILTER_COUNT;
                 filter++) {

                byte[] candidate =
                        filterRow(
                                current,
                                previous,
                                filter
                        );

                long score =
                        filterScore(candidate);

                if (score < bestScore) {

                    bestScore = score;
                    bestFilter = filter;
                    best = candidate;
                }
            }

            /*
             * O primeiro byte de cada linha indica
             * qual filtro foi utilizado.
             */
            filtered.write(
                    bestFilter
            );

            filtered.write(
                    best,
                    0,
                    best.length
            );

            previous = current;
        }

        return filtered.toByteArray();
    }

    // ================================================================
    // ENCODE
    // ================================================================

    public void encode() throws IOException {

        int width =
                img.getWidth();

        int height =
                img.getHeight();

        if (width <= 0 || height <= 0) {
            throw new IOException(
                    "Dimensões inválidas."
            );
        }

        if (width > 65535 ||
                height > 65535) {

            throw new IOException(
                    "A imagem excede o limite de 65535 pixels."
            );
        }

        // ============================================================
        // AUTOR
        // ============================================================

        String author =
                System.getProperty(
                        "user.name",
                        "unknown"
                );

        byte[] authorBytes =
                author.getBytes(
                        StandardCharsets.UTF_8
                );

        // ============================================================
        // TIMESTAMP
        // ============================================================

        long timestamp =
                Instant.now().toEpochMilli();

        byte[] timestampBytes =
                new byte[8];

        for (int i = 7; i >= 0; i--) {

            timestampBytes[7 - i] =
                    (byte) (
                            (timestamp >> (i * 8))
                                    & 0xFF
                    );
        }

        // ============================================================
        // FILTRO
        // ============================================================

        System.out.println(
                "[ARC] Gerando filtros..."
        );

        byte[] filtered =
                buildFilteredData(
                        width,
                        height
                );

        System.out.println(
                "[ARC] Dados filtrados: "
                        + filtered.length
                        + " bytes"
        );

        // ============================================================
        // RLE
        // ============================================================

        System.out.println(
                "[ARC] Executando RLE..."
        );

        byte[] rle =
                rleEncode(filtered);

        System.out.println(
                "[ARC] Depois do RLE: "
                        + rle.length
                        + " bytes"
        );

        // ============================================================
        // DEFLATE
        // ============================================================

        System.out.println(
                "[ARC] Executando DEFLATE..."
        );

        byte[] compressed =
                deflate(rle);

        System.out.println(
                "[ARC] Depois do DEFLATE: "
                        + compressed.length
                        + " bytes"
        );

        // ============================================================
        // ARQUIVO ARC
        // ============================================================

        try (
                BufferedOutputStream bos =
                        new BufferedOutputStream(
                                new FileOutputStream(
                                        fileOutput
                                )
                        )
        ) {

            // ========================================================
            // HEADER - 12 BYTES
            // ========================================================

            bos.write(
                    Format.SIGNATURE
            );

            bos.write(
                    Format.VERSION_MAJOR
            );

            bos.write(
                    Format.VERSION_MINOR
            );

            bos.write(
                    (width >> 8) & 0xFF
            );

            bos.write(
                    width & 0xFF
            );

            bos.write(
                    (height >> 8) & 0xFF
            );

            bos.write(
                    height & 0xFF
            );

            bos.write(
                    Format.COLOR_RGB
            );

            bos.write(
                    Format.PADDING
            );

            // ========================================================
            // METADADOS
            // ========================================================

            writeMetadata(
                    bos,
                    Format.AUTH,
                    authorBytes
            );

            writeMetadata(
                    bos,
                    Format.SOFT,
                    Format.SOFTWARE
            );

            writeMetadata(
                    bos,
                    Format.TIMS,
                    timestampBytes
            );

            // ========================================================
            // DATA
            // ========================================================

            bos.write(
                    Format.DATA
            );

            /*
             * Identificador do codec ARC 2.0.
             */
            bos.write(
                    Format.CODEC_VERSION
            );

            /*
             * Tamanho dos dados comprimidos.
             */
            writeUInt32(
                    bos,
                    compressed.length
            );

            /*
             * Dados:
             *
             * filtro
             * RLE
             * DEFLATE
             */
            bos.write(
                    compressed
            );

            bos.flush();
        }

        // ============================================================
        // ESTATÍSTICAS
        // ============================================================

        long rawRgbSize =
                (long) width *
                        height *
                        3;

        long finalSize =
                fileOutput.length();

        double ratio =
                rawRgbSize == 0
                        ? 0
                        : (100.0 * finalSize)
                          / rawRgbSize;

        System.out.println();
        System.out.println(
                "========== ARC 2.0 =========="
        );

        System.out.println(
                "Imagem: "
                        + width
                        + "x"
                        + height
        );

        System.out.println(
                "RGB bruto: "
                        + rawRgbSize
                        + " bytes"
        );

        System.out.println(
                "Filtrado: "
                        + filtered.length
                        + " bytes"
        );

        System.out.println(
                "RLE: "
                        + rle.length
                        + " bytes"
        );

        System.out.println(
                "DEFLATE: "
                        + compressed.length
                        + " bytes"
        );

        System.out.println(
                "ARC final: "
                        + finalSize
                        + " bytes"
        );

        System.out.printf(
                "Tamanho relativo ao RGB bruto: %.2f%%%n",
                ratio
        );

        System.out.println(
                "============================="
        );
    }
}