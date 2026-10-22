package com.littlehorizons.erp.util;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;

/** Writes errors to error_log.txt (appends to the file). */
public class AppLogger {

    public static void error(String message, Exception e) {
        try (PrintWriter out = new PrintWriter(new FileWriter("error_log.txt", true))) {
            out.println(LocalDateTime.now() + " ERROR: " + message);
            e.printStackTrace(out);
        } catch (Exception ignored) {
            System.err.println("Could not write to error_log.txt");
        }
    }
}
