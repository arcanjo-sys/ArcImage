package com.arcanjo.javaremote.codec;

import java.awt.image.BufferedImage;
import java.io.*;

public class ArcDecode {
    private File img;

    public ArcDecode(String img) {
        this.img = new File(img);
    }

    private int getSize(int sU, int sL) {
        return (sU << 8) | sL;
    }

    private int getRgb(int r, int g, int b) {
        return (255 << 24) | (r << 16) | (g << 8) | b;
    }

    public BufferedImage decode() {
        BufferedImage image;

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(this.img))) {

            byte[] headerBuffer = new byte[9];
            int bytesHeader = bis.read(headerBuffer);

            if (bytesHeader < 9) {
                System.out.println("Error: File sent is smaller than the header.");
            }

            int width = getSize(headerBuffer[4], headerBuffer[5]);
            int height = getSize(headerBuffer[6], headerBuffer[7]);

            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);

            byte[] buffer = new byte[4096]; // Lê de 4KB em 4KB
            int readsBytes;
            int pixelCount = 0;

            while ((readsBytes = bis.read(buffer)) != -1) {
                for (int i = 0; i < readsBytes; i += 3) {

                    int x = pixelCount % width;
                    int y = pixelCount / width;

                    int r = buffer[i] & 0xFF;
                    int g = buffer[i + 1] & 0xFF;
                    int b = buffer[i + 2] & 0xFF;

                    System.out.printf("Pixel %d -> [%d, %d, %d]\n", pixelCount, r, g, b);
                    System.out.printf("Rgb: %x\n", getRgb(r, g, b));

                    System.out.printf(
                            "pixel=%d x=%d y=%d width=%d height=%d%n",
                            pixelCount,
                            x,
                            y,
                            width,
                            height
                    );

                    if (pixelCount >= width * height) {
                        break;
                    }

                    image.setRGB(x, y, getRgb(r, g, b));
                    pixelCount++;
                }
            }
            } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return image;
    }
}
