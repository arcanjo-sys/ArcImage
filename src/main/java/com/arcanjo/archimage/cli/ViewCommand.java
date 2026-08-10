package com.arcanjo.archimage.cli;

import com.arcanjo.archimage.codec.ArcDecode;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

@Command(
        name = "view",
        description = "View an ArcImage file.",
        mixinStandardHelpOptions = true
)
public class ViewCommand implements Runnable{
    BufferedImage image;

    @Parameters(
            index = "0",
            description = "Input ArcImage file"
    )
    private String input;

    @Override
    public void run() {

        ArcDecode decoder = new ArcDecode(input);

        BufferedImage image = decoder.decode();

        int maxWidth = 800;
        int maxHeight = 600;

        double scale = Math.min(
                (double) maxWidth / image.getWidth(),
                (double) maxHeight / image.getHeight()
        );

        int newWidth = (int) (image.getWidth() * scale);
        int newHeight = (int) (image.getHeight() * scale);

        Image scaled = image.getScaledInstance(
                newWidth,
                newHeight,
                Image.SCALE_SMOOTH
        );

        JLabel label = new JLabel(new ImageIcon(scaled));

        JFrame frame = new JFrame("ArcImage");

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.add(label);

        frame.pack();

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }
}
