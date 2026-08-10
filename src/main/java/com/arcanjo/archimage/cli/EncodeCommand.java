package com.arcanjo.archimage.cli;

import com.arcanjo.archimage.codec.ArcEncode;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.IOException;

@Command(
        name = "encode",
        description = "Encode an image into ArcImage format."
)
public class EncodeCommand implements Runnable {

    @Parameters(
            index = "0",
            description = "Input image"
    )
    private String input;

    @Parameters(
            index = "1",
            description = "Output ArcImage file"
    )
    private String output;

    @Override
    public void run() {

        try {

            ArcEncode encoder =
                    new ArcEncode(input, output);

            encoder.encode();

            System.out.println(
                    "Image encoded successfully."
            );

        } catch (IOException e) {

            System.err.println(
                    "Error: " + e.getMessage()
            );
        }
    }
}