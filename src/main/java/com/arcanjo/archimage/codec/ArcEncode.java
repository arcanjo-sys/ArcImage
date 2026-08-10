package com.arcanjo.archimage.codec;

import com.arcanjo.archimage.format.Format;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ArcEncode {
    private BufferedImage img;
    private File fileOutput;

    public ArcEncode(String input, String output) throws IOException {
        this.img = ImageIO.read(new File(input));
        this.fileOutput = new File(output);
    }

    public void encode() {
        try (BufferedOutputStream bos =
                     new BufferedOutputStream(
                             new FileOutputStream(fileOutput))) {

            bos.write(Format.SIGNATURE);

            // Version

            bos.write(Format.VERSION_MAJOR);
            bos.write(Format.VERSION_MINOR);

            /*

            * SIZE

            * */

            int width = this.img.getWidth();
            int height = this.img.getHeight();

            // Width - 2 bytes
            bos.write((width >> 8) & 0xFF);
            bos.write(width & 0xFF);

            // Height - 2 bytes
            bos.write((height >> 8) & 0xFF);
            bos.write(height & 0xFF);

            // Color RGB
            bos.write(Format.COLOR_RGB);

            System.out.printf(
                    "HEADER: %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X%n",
                    Format.SIGNATURE[0],
                    Format.SIGNATURE[1],
                    Format.SIGNATURE[2],
                    Format.VERSION_MAJOR,
                    Format.VERSION_MINOR,
                    (width >> 8) & 0xFF,
                    width & 0xFF,
                    (height >> 8) & 0xFF,
                    height & 0xFF,
                    Format.COLOR_RGB
            );

            for (int y = 0; y < height; y++) {

                for (int x = 0; x < width; x++) {

                    int rgb = this.img.getRGB(x, y);

                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;

                    bos.write(r);
                    bos.write(g);
                    bos.write(b);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
