package com.arcanjo.archimage.viewer;

import com.arcanjo.archimage.format.Format;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageView {
    private final File img;

    public ImageView(String img) {
        this.img = new File(img);
    }

    private int getSize(byte high, byte low) {
        return ((high & 0xFF) << 8)
                |  (low & 0xFF);
    }

    /*private int getRgb(int r, int g, int b) {
        return (255 << 24) | (r << 16) | (g << 8) | b;
    }*/

    public BufferedImage image() {

        try (BufferedInputStream bis =
                     new BufferedInputStream(
                             new FileInputStream(this.img))) {

            // =====================
            // HEADER
            // =====================

            byte[] headerBuffer = new byte[Format.HEADER_SIZE];

            int bytesHeader = bis.readNBytes(
                    headerBuffer, 0, 10
            );

            if (bytesHeader != Format.HEADER_SIZE) {
                throw new IOException(
                        "Arquivo menor que o header."
                );
            }

            int width = getSize(
                    headerBuffer[5],
                    headerBuffer[6]
            );

            int height = getSize(
                    headerBuffer[7],
                    headerBuffer[8]
            );

            BufferedImage image =
                    new BufferedImage(
                            width,
                            height,
                            BufferedImage.TYPE_INT_RGB
                    );

            // =====================
            // PIXELS
            // =====================

            byte[] buffer = new byte[3];

            int pixelCount = width * height;

            for (int pixel = 0; pixel < pixelCount; pixel++) {

                int lidos = bis.readNBytes(
                        buffer, 0, 3
                );

                if (lidos != 3) {
                    throw new IOException(
                            "Arquivo terminou antes de todos os pixels. " +
                                    "Pixel: " + pixel
                    );
                }

                int r = buffer[0] & 0xFF;
                int g = buffer[1] & 0xFF;
                int b = buffer[2] & 0xFF;

                int x = pixel % width;
                int y = pixel / width;

                int rgb =
                        (r << 16) |
                                (g << 8) |
                                b;

                image.setRGB(x, y, rgb);
            }

            return image;

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
