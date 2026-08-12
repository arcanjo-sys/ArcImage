package com.arcanjo.archimage.viewer;

import com.arcanjo.archimage.format.Format;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.zip.Inflater;

/**
 * Decoder ARC 2.0.
 *
 * Pipeline inverso:
 *
 * ARC
 *  ↓
 * DEFLATE
 *  ↓
 * RLE
 *  ↓
 * filtros reversos
 *  ↓
 * RGB
 */
public class ImageView {
    private String author;
    private String software;
    private String created;

    private static final int CODEC_VERSION = 0x20;

    private final File img;

    public ImageView(String img) {
        this.img = new File(img);
    }

    // ================================================================
    // LEITURA
    // ================================================================

    private int getSize(
            byte high,
            byte low
    ) {

        return ((high & 0xFF) << 8)
                | (low & 0xFF);
    }

    private int readUInt32(
            InputStream in
    ) throws IOException {

        int b1 = in.read();
        int b2 = in.read();
        int b3 = in.read();
        int b4 = in.read();

        if (
                b1 == -1 ||
                        b2 == -1 ||
                        b3 == -1 ||
                        b4 == -1
        ) {

            throw new IOException(
                    "Fim inesperado lendo UInt32."
            );
        }

        return (
                ((b1 & 0xFF) << 24)
                        |
                        ((b2 & 0xFF) << 16)
                        |
                        ((b3 & 0xFF) << 8)
                        |
                        (b4 & 0xFF)
        );
    }

    private void readFully(
            InputStream in,
            byte[] buffer
    ) throws IOException {

        int offset = 0;

        while (offset < buffer.length) {

            int count =
                    in.read(
                            buffer,
                            offset,
                            buffer.length - offset
                    );

            if (count == -1) {

                throw new IOException(
                        "Fim inesperado do arquivo."
                );
            }

            offset += count;
        }
    }

    // ================================================================
    // PAETH
    // ================================================================

    private int paethPredictor(
            int left,
            int up,
            int upperLeft
    ) {

        int p =
                left +
                        up -
                        upperLeft;

        int pa =
                Math.abs(
                        p - left
                );

        int pb =
                Math.abs(
                        p - up
                );

        int pc =
                Math.abs(
                        p - upperLeft
                );

        if (pa <= pb && pa <= pc) {
            return left;
        }

        if (pb <= pc) {
            return up;
        }

        return upperLeft;
    }

    // ================================================================
    // INFLATE
    // ================================================================

    private byte[] inflate(
            byte[] compressed
    ) throws IOException {

        Inflater inflater =
                new Inflater();

        inflater.setInput(
                compressed
        );

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        byte[] buffer =
                new byte[8192];

        try {

            while (!inflater.finished()) {

                int count =
                        inflater.inflate(
                                buffer
                        );

                if (count > 0) {

                    out.write(
                            buffer,
                            0,
                            count
                    );

                    continue;
                }

                if (inflater.needsDictionary()) {

                    throw new IOException(
                            "DEFLATE requer um dicionário."
                    );
                }

                if (inflater.needsInput()) {

                    throw new IOException(
                            "DEFLATE terminou antes dos dados."
                    );
                }

                throw new IOException(
                        "DEFLATE não conseguiu avançar."
                );
            }

        } catch (Exception e) {

            if (e instanceof IOException) {
                throw (IOException) e;
            }

            throw new IOException(
                    "Erro ao descompactar DEFLATE.",
                    e
            );

        } finally {

            inflater.end();
        }

        return out.toByteArray();
    }

    // ================================================================
    // RLE
    // ================================================================

    private byte[] rleDecode(
            byte[] input
    ) throws IOException {

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        int pos = 0;

        while (pos < input.length) {

            int control =
                    input[pos++] & 0xFF;

            boolean run =
                    (control & 0x80) != 0;

            int length =
                    (control & 0x7F) + 1;

            // ========================================================
            // RUN
            // ========================================================

            if (run) {

                if (pos >= input.length) {

                    throw new IOException(
                            "RLE truncado: faltou byte da repetição."
                    );
                }

                byte value =
                        input[pos++];

                for (int i = 0;
                     i < length;
                     i++) {

                    out.write(
                            value & 0xFF
                    );
                }

            }

            // ========================================================
            // LITERAL
            // ========================================================

            else {

                if (
                        pos + length >
                                input.length
                ) {

                    throw new IOException(
                            "RLE truncado: literal excede o buffer."
                    );
                }

                out.write(
                        input,
                        pos,
                        length
                );

                pos += length;
            }
        }

        return out.toByteArray();
    }

    // ================================================================
    // FILTRO INVERSO
    // ================================================================

    private void unfilterRow(
            byte[] filtered,
            byte[] previous,
            byte[] current,
            int filter
    ) throws IOException {

        if (
                filter < 0 ||
                        filter > 4
        ) {

            throw new IOException(
                    "Tipo de filtro inválido: "
                            + filter
            );
        }

        for (int i = 0;
             i < current.length;
             i++) {

            int value =
                    filtered[i] & 0xFF;

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

                    throw new IOException(
                            "Filtro desconhecido."
                    );
            }

            current[i] =
                    (byte) (
                            (value + prediction)
                                    & 0xFF
                    );
        }
    }

    // ================================================================
    // DECODIFICAÇÃO DOS PIXELS
    // ================================================================

    private BufferedImage decodePixels(
            byte[] filteredData,
            int width,
            int height
    ) throws IOException {

        int rowSize =
                width * 3;

        int expectedSize =
                height * (rowSize + 1);

        if (
                filteredData.length !=
                        expectedSize
        ) {

            throw new IOException(
                    "Tamanho dos dados filtrados inválido. "
                            + "Esperado="
                            + expectedSize
                            + ", recebido="
                            + filteredData.length
            );
        }

        BufferedImage image =
                new BufferedImage(
                        width,
                        height,
                        BufferedImage.TYPE_INT_RGB
                );

        byte[] previous = null;

        int offset = 0;

        for (int y = 0;
             y < height;
             y++) {

            int filter =
                    filteredData[offset++]
                            & 0xFF;

            byte[] filteredRow =
                    new byte[rowSize];

            System.arraycopy(
                    filteredData,
                    offset,
                    filteredRow,
                    0,
                    rowSize
            );

            offset += rowSize;

            byte[] current =
                    new byte[rowSize];

            unfilterRow(
                    filteredRow,
                    previous,
                    current,
                    filter
            );

            // ========================================================
            // RGB
            // ========================================================

            int index = 0;

            for (int x = 0;
                 x < width;
                 x++) {

                int r =
                        current[index++]
                                & 0xFF;

                int g =
                        current[index++]
                                & 0xFF;

                int b =
                        current[index++]
                                & 0xFF;

                int rgb =
                        (r << 16)
                                |
                                (g << 8)
                                |
                                b;

                image.setRGB(
                        x,
                        y,
                        rgb
                );
            }

            previous = current;
        }

        return image;
    }

    // ================================================================
    // Get Author
    // ================================================================

    public String getAuthor() {
        return this.author;
    }

    // ================================================================
    // Get Software
    // ================================================================

    public String getSoftware() {
        return this.software;
    }

    // ================================================================
    // Get Created
    // ================================================================

    public String getCreated() {
        return this.created;
    }

    // ================================================================
    // IMAGE
    // ================================================================

    public BufferedImage image()
            throws IOException {

        try (
                BufferedInputStream bis =
                        new BufferedInputStream(
                                new FileInputStream(
                                        this.img
                                )
                        )
        ) {

            // ========================================================
            // HEADER
            // ========================================================

            byte[] header =
                    new byte[12];

            readFully(
                    bis,
                    header
            );

            if (
                    header[0] != 'A' ||
                            header[1] != 'R' ||
                            header[2] != 'C' ||
                            header[3] != 'X'
            ) {

                throw new IOException(
                        "Assinatura ARCX não encontrada."
                );
            }

            int width =
                    getSize(
                            header[6],
                            header[7]
                    );

            int height =
                    getSize(
                            header[8],
                            header[9]
                    );

            if (
                    width <= 0 ||
                            height <= 0
            ) {

                throw new IOException(
                        "Dimensões inválidas: "
                                + width
                                + "x"
                                + height
                );
            }

            System.out.println(
                    "[ARC] Imagem: "
                            + width
                            + "x"
                            + height
            );

            // ========================================================
            // METADADOS
            // ========================================================

            byte[] tag = new byte[4];

            while (true) {

                readFully(
                        bis,
                        tag
                );

                String tagName =
                        new String(
                                tag,
                                StandardCharsets.US_ASCII
                        );

                System.out.println(
                        "[ARC] Tag: "
                                + tagName
                );

                if (tagName.equals("DATA")) {
                    break;
                }

                int size =
                        (
                                (bis.read() & 0xFF)
                                        << 8
                        )
                                |
                                (
                                        bis.read() & 0xFF
                                );

                byte[] metadata =
                        new byte[size];

                readFully(
                        bis,
                        metadata
                );

                String value =
                        new String(
                                metadata,
                                StandardCharsets.UTF_8
                        );

                switch (tagName) {

                    case "AUTH":
                        author = value;
                        break;

                    case "SOFT":
                        software = value;
                        break;

                    case "TIMS":
                        long timestamp =
                                ByteBuffer
                                        .wrap(metadata)
                                        .getLong();

                        Instant instant = Instant.ofEpochMilli(timestamp);

                        String created =
                                DateTimeFormatter
                                        .ISO_INSTANT
                                        .format(instant);


                        this.created = created;
                        break;

                    default:
                        System.out.println(
                            "[ARC] Unknown metadata: "
                                    + tagName
                        );
                }
            }

            // ========================================================
            // CODEC
            // ========================================================

            int codecVersion =
                    bis.read();

            if (
                    codecVersion !=
                            CODEC_VERSION
            ) {

                throw new IOException(
                        String.format(
                                "Codec ARC incompatível: 0x%02X",
                                codecVersion
                        )
                );
            }

            // ========================================================
            // TAMANHO DO BLOCO COMPRIMIDO
            // ========================================================

            int compressedSize =
                    readUInt32(
                            bis
                    );

            if (
                    compressedSize <= 0
            ) {

                throw new IOException(
                        "Tamanho comprimido inválido: "
                                + compressedSize
                );
            }

            byte[] compressed =
                    new byte[
                            compressedSize
                            ];

            readFully(
                    bis,
                    compressed
            );

            System.out.println(
                    "[ARC] Dados comprimidos: "
                            + compressedSize
                            + " bytes"
            );

            // ========================================================
            // DEFLATE
            // ========================================================

            System.out.println(
                    "[ARC] Inflando DEFLATE..."
            );

            byte[] rle =
                    inflate(
                            compressed
                    );

            System.out.println(
                    "[ARC] Depois do DEFLATE: "
                            + rle.length
                            + " bytes"
            );

            // ========================================================
            // RLE
            // ========================================================

            System.out.println(
                    "[ARC] Decodificando RLE..."
            );

            byte[] filtered =
                    rleDecode(
                            rle
                    );

            System.out.println(
                    "[ARC] Depois do RLE: "
                            + filtered.length
                            + " bytes"
            );

            // ========================================================
            // FILTROS
            // ========================================================

            System.out.println(
                    "[ARC] Restaurando filtros..."
            );

            BufferedImage image =
                    decodePixels(
                            filtered,
                            width,
                            height
                    );

            System.out.println(
                    "[ARC] Imagem reconstruída com sucesso."
            );

            return image;
        }
    }
}