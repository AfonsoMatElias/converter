package io.github.afonsomatelias.Helpers;

// Hidden printer to controller the what to print in the converter 
public class $$ {
    public static boolean isSilent = false;

    public static void out(Object... contents) {
        if (isSilent) return;
        Printer.out(contents);
    }

    public static void err(Object... contents) {
        Printer.err(contents);
    }
}