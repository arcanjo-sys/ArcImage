package com.arcanjo.archimage.cli;
import com.arcanjo.archimage.format.Format;
import com.arcanjo.archimage.viewer.ImageView;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Command(
        name = "info",
        description = "Displays the image metadata.",
        mixinStandardHelpOptions = true
)
public class InfoCommand implements Runnable{
    @Parameters(
            index = "0",
            description = "Input ArcImage file"
    )
    private String input;

    @Override
    public void run() {
        try {
            ImageView image = new ImageView(input);
            BufferedImage img = image.image();

            File file = new File(input);

            long fileSize = file.length();
            long pixels = (long) img.getWidth() * img.getHeight();

            System.out.println("# ArcImage Information");
            System.out.println();

            System.out.println("## File");
            System.out.println();
            System.out.println(fileSize);

            System.out.printf("Name:       %s%n", file.getName());
            System.out.println("Format:     ARC");
            System.out.printf("Version:    %d%s%d\n", Format.VERSION_MAJOR, ".", Format.VERSION_MINOR);
            System.out.printf("File Size:  %.2f MB%n", fileSize / (1024.0 * 1024.0));

            System.out.println();

            System.out.println("## Image");
            System.out.println();

            System.out.printf("Width:      %d%n", img.getWidth());
            System.out.printf("Height:     %d%n", img.getHeight());
            System.out.println("Color:      RGB"); // Alterações futuras
            System.out.println("Bit Depth:  24"); // Alterações futuras
            System.out.printf("Pixels:     %,d%n", pixels);

            System.out.println();

            System.out.println("## Compression");
            System.out.println();

            System.out.printf("Codec:      ARC v%x\n", Format.CODEC_VERSION);
            System.out.println("Filter:     Adaptive");
            System.out.println("RLE:        Enabled");
            System.out.println("DEFLATE:    Enabled");

            System.out.println();

            System.out.println("## Metadata");
            System.out.println();

            System.out.printf("Author:     %s\n", image.getAuthor());
            System.out.printf("Software:   %s\n", image.getSoftware());
            System.out.printf("Created:    %s\n", image.getCreated());

        } catch (IOException e) {
            throw new RuntimeException("Failed to read ARC image", e);
        }
    }
}
