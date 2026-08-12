package com.arcanjo.archimage.format;

public final class Format {

    private Format() {}

    // Identificação do formato
    public static final byte[] SIGNATURE = {
            'A', 'R', 'C', 'X'
    };

    // Versão do formato
    public static final byte VERSION_MAJOR = 2;
    public static final byte VERSION_MINOR = 0;

    /*
     * Identificador da versão do codec armazenado depois de DATA.
     */
    public static final int CODEC_VERSION = 0x20;

    // Formato de cor
    public static final byte COLOR_RGB = 3;

    // Tamanho do header
    public static final int HEADER_SIZE = 12;

    public static final byte PADDING = 0x00;

    // Metadatas

    public static final byte[] SOFTWARE = {
            'A', 'r', 'c', 'I', 'm', 'a', 'g', 'e', ' ', 'v', '2', '.', '0'
    };

    public static final byte[] AUTH = {
            'A', 'U', 'T', 'H'
    };

    public static final byte[] TIMS = {
            'T', 'I', 'M', 'S'
    };

    public static final byte[] SOFT = {
            'S', 'O', 'F', 'T'
    };

    public static final byte[] DATA = {
            'D', 'A', 'T', 'A'
    };

}
