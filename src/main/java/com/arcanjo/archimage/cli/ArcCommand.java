package com.arcanjo.archimage.cli;

import picocli.CommandLine.Command;

@Command(
        name = "arcimage",
        description = "Encode and decode images using the ArcImage format.",
        subcommands = {
                EncodeCommand.class,
                DecodeCommand.class,
                ViewCommand.class
        }
)

public class ArcCommand {
}
