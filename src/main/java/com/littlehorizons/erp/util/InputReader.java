package com.littlehorizons.erp.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Handles console inputs with validation loops.
 */
public class InputReader {

    private static final Scanner SCANNER = new Scanner(System.in);

    public static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SCANNER.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Este campo no puede estar vacío. Intente de nuevo.");
        }
    }

    public static String readOptional(String prompt) {
        System.out.print(prompt + " (Enter para omitir): ");
        String value = SCANNER.nextLine().trim();
        return value.isEmpty() ? null : value;
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(SCANNER.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Por favor ingrese un número entre " + min + " y " + max + ".");
        }
    }

    public static BigDecimal readBigDecimal(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = SCANNER.nextLine().trim().replace(',', '.');
                BigDecimal value = new BigDecimal(input);
                if (value.compareTo(BigDecimal.valueOf(min)) >= 0 && value.compareTo(BigDecimal.valueOf(max)) <= 0) {
                    return value;
                }
            } catch (Exception ignored) {
            }
            System.out.println("Ingrese un valor numérico válido entre " + min + " y " + max + ".");
        }
    }

    public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (AAAA-MM-DD): ");
            try {
                return LocalDate.parse(SCANNER.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("Formato de fecha inválido. Utilice AAAA-MM-DD (ej: 2026-03-15).");
            }
        }
    }

    public static LocalTime readTime(String prompt) {
        while (true) {
            System.out.print(prompt + " (HH:mm, ej: 08:00): ");
            try {
                String input = SCANNER.nextLine().trim();
                if (input.length() == 5) {
                    return LocalTime.parse(input);
                } else if (input.length() == 8) {
                    return LocalTime.parse(input);
                }
            } catch (DateTimeParseException ignored) {
            }
            System.out.println("Formato de hora inválido. Utilice HH:mm (ej: 07:30 o 14:00).");
        }
    }

    public static boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(prompt + " (s/n): ");
            String input = SCANNER.nextLine().trim().toLowerCase();
            if (input.equals("s") || input.equals("si") || input.equals("sí") || input.equals("y") || input.equals("yes")) {
                return true;
            }
            if (input.equals("n") || input.equals("no")) {
                return false;
            }
            System.out.println("Responda 's' para Sí o 'n' para No.");
        }
    }

    public static String readChoice(String title, List<String> options) {
        System.out.println(title);
        for (int i = 0; i < options.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + options.get(i));
        }
        return options.get(readInt("Seleccione una opción: ", 1, options.size()) - 1);
    }

    public static void pause() {
        System.out.print("\nPresione Enter para continuar...");
        SCANNER.nextLine();
    }
}
