package com.arcanjo.javaremote;

import com.arcanjo.javaremote.codec.ArcDecode;
import com.arcanjo.javaremote.viewer.ImagePanel;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {
        BufferedImage image;
        ArcDecode decode = new ArcDecode("./src/main/java/com/arcanjo/javaremote/image.arc");

        image = decode.decode();

        ImagePanel paint = new ImagePanel(image);
        JFrame frame = new JFrame("Arc image");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(paint);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}