package com.arcanjo.archimage.viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {

    private final BufferedImage image;

    public ImagePanel(BufferedImage image) {
        this.image = image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int escale = 1;

        g.drawImage(image, 0, 0,image.getWidth() * escale, image.getHeight() * escale,  this);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(
                image.getWidth(),
                image.getHeight()
        );
    }
}
