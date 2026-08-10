package com.arcanjo.archimage.cli;

import com.arcanjo.archimage.codec.ArcDecode;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.awt.image.BufferedImage;
import java.io.IOException;

@Command(
        name = "decode",
        description = "Decode an ArcImage file.",
        mixinStandardHelpOptions = true
)
public class DecodeCommand implements Runnable {
    BufferedImage image;

    @Parameters(
            index = "0",
            description = "Input ArcImage file"
    )
    private String input;

    @Override
    public void run() {

        ArcDecode decoder = new ArcDecode(input);

        image = decoder.decode();

        System.out.println(
                "Image decoded successfully."
        );

    }
}