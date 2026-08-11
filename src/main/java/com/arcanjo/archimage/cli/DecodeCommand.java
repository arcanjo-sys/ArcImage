package com.arcanjo.archimage.cli;

import com.arcanjo.archimage.codec.ArcDecode;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
        name = "decode",
        description = "Decode an ArcImage file.",
        mixinStandardHelpOptions = true
)
public class DecodeCommand implements Runnable {

    @Parameters(
            index = "0",
            description = "Input ArcImage file"
    )
    private String input;

    @Parameters(
            index = "1",
            description = "Output image file"
    )
    private String output;

    @Override
    public void run() {

        ArcDecode decoder = new ArcDecode(input, output);

        decoder.decode();

        System.out.println(
                "Image decoded successfully."
        );

    }
}