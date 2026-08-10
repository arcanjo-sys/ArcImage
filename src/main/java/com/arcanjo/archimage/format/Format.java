package com.arcanjo.archimage.format;

public final class Format {

    private Format() {}

    // Identificação do formato
    public static final byte[] SIGNATURE = {
            'A', 'R', 'C'
    };

    // Versão do formato
    public static final byte VERSION_MAJOR = 1;
    public static final byte VERSION_MINOR = 0;

    // Formato de cor
    public static final byte COLOR_RGB = 3;

    // Tamanho do header
    public static final int HEADER_SIZE = 10;
}
