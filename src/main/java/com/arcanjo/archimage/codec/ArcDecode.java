package com.arcanjo.archimage.codec;

import com.arcanjo.archimage.format.Format;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ArcDecode {
    private final File fileInput;
    private final File fileOutput;

    public ArcDecode(String fileInput, String fileOutput) {
        this.fileInput = new File(fileInput);
        this.fileOutput = new File(fileOutput);
    }

    public void decode() {

        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(new FileInputStream(fileInput)))) {

            // =========================
            // HEADER
            // =========================

            byte[] signature = new byte[3];
            in.readFully(signature);

            if (signature[0] != 'A' ||
                    signature[1] != 'R' ||
                    signature[2] != 'C') {

                throw new IOException("Arquivo ARC inválido: assinatura incorreta.");
            }

            int versionMajor = in.readUnsignedByte();
            int versionMinor = in.readUnsignedByte();

            if (versionMajor != Format.VERSION_MAJOR || versionMinor != Format.VERSION_MINOR) {
                throw new IOException(
                        "Versão ARC não suportada: "
                                + versionMajor + "." + versionMinor
                );
            }

            int width = in.readUnsignedShort();
            int height = in.readUnsignedShort();

            int pixelType = in.readUnsignedByte();

            if (pixelType != 3) {
                throw new IOException(
                        "Tipo de pixel não suportado: " + pixelType
                );
            }

            // =========================
            // CRIA A IMAGEM
            // =========================

            BufferedImage image = new BufferedImage(
                    width,
                    height,
                    BufferedImage.TYPE_INT_RGB
            );

            // =========================
            // PIXELS
            // =========================

            for (int y = 0; y < height; y++) {

                for (int x = 0; x < width; x++) {

                    int r = in.readUnsignedByte();
                    int g = in.readUnsignedByte();
                    int b = in.readUnsignedByte();

                    int rgb = (r << 16) |
                            (g << 8)  |
                            b;

                    image.setRGB(x, y, rgb);
                }
            }

            // =========================
            // PNG
            // =========================

            if (!ImageIO.write(image, "png", fileOutput)) {
                throw new IOException("Não foi possível escrever o PNG.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

