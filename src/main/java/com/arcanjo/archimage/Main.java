package com.arcanjo.archimage;

import com.arcanjo.archimage.cli.ArcCommand;
import picocli.CommandLine;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        int exitCode = new CommandLine(new ArcCommand())
                .execute(args);

        //System.exit(exitCode);
    }
}