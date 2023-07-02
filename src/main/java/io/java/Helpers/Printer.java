package io.java.Helpers;

import java.io.PrintStream;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class Printer {
    private static String appName = "Printer";
    private static String source = null;

    static String accentReplacer(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    static String build() {
        String date = LocalDateTime.now().toString().replace("T", " ");
        String sep = " - ";
        return "[" + appName + sep + date + "]: ";
    }

    static String build(String source) {
        String date = LocalDateTime.now().toString().replace("T", " ");
        String sep = " - ";
        return "[" + appName + sep + source + sep + date + "]: ";
    }

    /**
     * Process multiple printing messages
     * 
     * @param method   the method that needs to be invoked
     * @param contents the content to be printed
     */
    static void printMultipleMessages(
            final PrintStream methodSource,
            final String source,
            final Object[] contents) {
        // Looping all the contents provided
        for (final Object content : contents) {

            if (content == null)
                continue;

            // Checking if it's an Object type
            final Boolean isObject = (content instanceof Object);

            // Checking if it's an Exception Object type
            final Boolean isExceptionObject = (content instanceof Exception);

            if (isObject && !isExceptionObject) {
                methodSource.print(build());
                methodSource.print(content);
                methodSource.print("\n");
                continue;
            }

            // Turning the content to string
            String message = content.toString();

            // If it's an Exception object, get the message inside of it
            if (isExceptionObject) {
                String exceptionMessage = ((Exception) content).getMessage();
                message = exceptionMessage == null ? message : exceptionMessage;
            }

            message = accentReplacer(message); // Helpers

            // If the source wasn't provided, build the without it
            if (source == null)
                methodSource.println(build() + message);
            else
                // Otherwise, apply the source
                methodSource.println(build(source) + message);

            // Print the StackTrace if it's an Exception
            if (isExceptionObject)
                ((Exception) content).printStackTrace();
        }
    }

    /**
     * Prints out as info the content(s) provided
     * 
     * @param contents the content to be printed
     * @return the printer instance
     */
    public static void out(Object... contents) {
        printMultipleMessages(System.out, source, contents);
    }

    /**
     * Prints out as error the content(s) provided
     * 
     * @param contents the content to be printed
     * @return the printer instance
     */
    public static void err(Object... contents) {
        printMultipleMessages(System.err, source, contents);
    }

    /**
     * Sets a header to the printer
     * 
     * @param source the source/header that need to be presented in the print
     */
    public static void of(String source) {
        Printer.source = source;
    }

    /**
     * Dispose all using fields
     */
    public static void dispose() {
        Printer.source = null;
    }
}
