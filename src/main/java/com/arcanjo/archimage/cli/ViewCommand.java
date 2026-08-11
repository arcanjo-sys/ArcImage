package com.arcanjo.archimage.cli;

import com.arcanjo.archimage.viewer.ImageView;
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
    @Parameters(
            index = "0",
            description = "Input ArcImage file"
    )
    private String input;

    @Override
    public void run() {

        ImageView image = new ImageView(input);

        BufferedImage img = image.image();

        int maxWidth = 800;
        int maxHeight = 600;

        double scale = Math.min(
                (double) maxWidth / img.getWidth(),
                (double) maxHeight / img.getHeight()
        );

        int newWidth = (int) (img.getWidth() * scale);
        int newHeight = (int) (img.getHeight() * scale);

        Image scaled = img.getScaledInstance(
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
