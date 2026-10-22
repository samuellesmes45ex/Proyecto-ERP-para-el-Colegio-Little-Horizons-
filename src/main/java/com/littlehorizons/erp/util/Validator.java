package com.littlehorizons.erp.util;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

/**
 * Common validation rules, regular expressions, and fixed domain choice lists.
 */
public class Validator {

    public static final List<String> DOCUMENT_TYPES = List.of("TI", "CC", "CE", "Pasaporte");
    public static final List<String> GUARDIAN_DOCUMENT_TYPES = List.of("CC", "CE", "Pasaporte");
    public static final List<String> GENDERS = List.of("M", "F", "Otro");
    public static final List<String> STUDENT_STATUSES = List.of("Activo", "Graduado", "Retirado", "Suspendido");
    public static final List<String> TEACHER_STATUSES = List.of("Activo", "Inactivo", "Licencia");
    public static final List<String> ACADEMIC_PERIODS = List.of("I", "II", "III", "IV");
    public static final List<String> GRADE_PERIODS = List.of("I", "II", "III", "IV", "Final");
    public static final List<String> ATTENDANCE_STATUSES = List.of("Presente", "Ausente", "Tardanza", "Excusa Médica");
    public static final List<String> DAYS_OF_WEEK = List.of("Lunes", "Martes", "Miércoles", "Jueves", "Viernes");
    public static final List<String> EVENT_TYPES = List.of("Académico", "Cultural", "Deportivo", "Social", "Científico");
    public static final List<String> EVENT_ROLES = List.of("Participante", "Competidor", "Organizador", "Asistente");
    public static final List<String> EVENT_POSITIONS = List.of("1er Lugar", "2do Lugar", "3er Lugar", "Participante");
    public static final List<String> PAYMENT_METHODS = List.of("Efectivo", "Transferencia", "Tarjeta Débito", "Tarjeta Crédito", "Cheque");
    public static final List<String> RESOURCE_STATUSES = List.of("Disponible", "Prestado", "En Mantenimiento", "Dado de Baja");
    public static final List<String> LOAN_STATUSES = List.of("Activo", "Devuelto", "Vencido");

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.trim().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return phone.trim().matches("^\\d{7,15}$");
    }

    public static boolean isValidDocument(String document) {
        if (document == null) return false;
        return document.trim().matches("^[A-Za-z0-9]{5,20}$");
    }

    public static boolean isValidScore(BigDecimal score) {
        if (score == null) return false;
        return score.compareTo(BigDecimal.ZERO) >= 0 && score.compareTo(new BigDecimal("5.0")) <= 0;
    }

    public static boolean isValidTimeRange(LocalTime start, LocalTime end) {
        if (start == null || end == null) return false;
        return end.isAfter(start);
    }
}
