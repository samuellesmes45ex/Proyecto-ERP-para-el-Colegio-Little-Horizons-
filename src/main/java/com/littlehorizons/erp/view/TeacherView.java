package com.littlehorizons.erp.view;

import com.littlehorizons.erp.controller.TeacherController;
import com.littlehorizons.erp.model.Teacher;
import com.littlehorizons.erp.model.TeacherAssignment;
import com.littlehorizons.erp.util.AppLogger;
import com.littlehorizons.erp.util.InputReader;
import com.littlehorizons.erp.util.Validator;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;

/**
 * Interactive console interface for Teacher Management and Subject Schedule Assignments.
 */
public class TeacherView {

    private final TeacherController controller = new TeacherController();

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║         MÓDULO: GESTIÓN DE PROFESORES        ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.println("1. Registrar nuevo profesor");
            System.out.println("2. Listar todos los profesores");
            System.out.println("3. Buscar profesor (nombre, documento o especialidad)");
            System.out.println("4. Asignar materia y horario (con control de cruces)");
            System.out.println("5. Consultar horario semanal de un profesor");
            System.out.println("6. Consultar profesores asignados a una materia");
            System.out.println("0. Volver al Menú Principal");

            int option = InputReader.readInt("Seleccione una opción: ", 0, 6);
            try {
                switch (option) {
                    case 1 -> registerTeacher();
                    case 2 -> listTeachers();
                    case 3 -> searchTeachers();
                    case 4 -> assignSubject();
                    case 5 -> viewTeacherSchedule();
                    case 6 -> viewSubjectAssignments();
                    default -> back = true;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("\n[ERROR DE VALIDACIÓN] " + e.getMessage());
            } catch (SQLException e) {
                AppLogger.error("TeacherView database failure", e);
                System.out.println("\n[ERROR DE BASE DE DATOS] Operación no completada. Consulte error_log.txt.");
            }
        }
    }

    private void registerTeacher() throws SQLException {
        System.out.println("\n--- REGISTRO DE PROFESOR ---");
        Teacher t = new Teacher();
        t.setDocumentType(InputReader.readChoice("Tipo de documento:", Validator.GUARDIAN_DOCUMENT_TYPES));
        t.setDocumentNumber(InputReader.readText("Número de documento: "));
        t.setFirstName(InputReader.readText("Primer nombre: "));
        t.setMiddleName(InputReader.readOptional("Segundo nombre"));
        t.setLastName(InputReader.readText("Primer apellido: "));
        t.setSecondLastName(InputReader.readOptional("Segundo apellido"));
        t.setEmail(InputReader.readText("Correo electrónico institucional: "));
        t.setPhone(InputReader.readOptional("Teléfono de contacto"));
        t.setAddress(InputReader.readOptional("Dirección"));
        t.setAcademicDegree(InputReader.readText("Título profesional / académico: "));
        t.setSpecialty(InputReader.readText("Especialidad (ej: Matemáticas, Ciencias Naturales): "));

        int id = controller.registerTeacher(t);
        System.out.println("\n✔ Profesor registrado exitosamente con ID: " + id);
    }

    private void listTeachers() throws SQLException {
        List<Teacher> list = controller.listAllTeachers();
        printTeacherTable(list);
    }

    private void searchTeachers() throws SQLException {
        String text = InputReader.readText("Ingrese término a buscar: ");
        List<Teacher> list = controller.searchTeachers(text);
        printTeacherTable(list);
    }

    private void assignSubject() throws SQLException {
        System.out.println("\n--- ASIGNACIÓN DE CARGA ACADÉMICA Y HORARIO ---");
        int teacherId = InputReader.readInt("ID del profesor: ", 1, Integer.MAX_VALUE);
        int subjectId = InputReader.readInt("ID de la materia: ", 1, Integer.MAX_VALUE);
        int year = InputReader.readInt("Año académico: ", 2020, 2035);
        String section = InputReader.readOptional("Sección / Salón (ej: A, B, 101)");
        String day = InputReader.readChoice("Día de la clase:", Validator.DAYS_OF_WEEK);
        LocalTime startTime = InputReader.readTime("Hora de inicio de la clase");
        LocalTime endTime = InputReader.readTime("Hora de finalización de la clase");

        int id = controller.assignSubject(teacherId, subjectId, year, section, day, startTime, endTime);
        System.out.println("\n✔ Materia asignada correctamente (Registro #" + id + "). Horario validado sin conflictos.");
    }

    private void viewTeacherSchedule() throws SQLException {
        int teacherId = InputReader.readInt("ID del profesor: ", 1, Integer.MAX_VALUE);
        int year = InputReader.readInt("Año académico: ", 2020, 2035);

        List<TeacherAssignment> list = controller.getAssignmentsByTeacher(teacherId, year);
        System.out.println("\nHorario de clases asignado para el año " + year + ":");
        if (list.isEmpty()) {
            System.out.println("  (No hay clases programadas para este profesor)");
        } else {
            for (TeacherAssignment a : list) {
                System.out.println("  • " + a.getDayOfWeek() + " " + a.getStartTime() + " - " + a.getEndTime() +
                        " | " + a.getSubjectName() + " (Sección: " + a.getSection() + ")");
            }
        }
        InputReader.pause();
    }

    private void viewSubjectAssignments() throws SQLException {
        int subjectId = InputReader.readInt("ID de la materia: ", 1, Integer.MAX_VALUE);
        int year = InputReader.readInt("Año académico: ", 2020, 2035);

        List<TeacherAssignment> list = controller.getAssignmentsBySubject(subjectId, year);
        System.out.println("\nProfesores asignados a la materia:");
        if (list.isEmpty()) {
            System.out.println("  (No hay profesores asignados actualmente)");
        } else {
            for (TeacherAssignment a : list) {
                System.out.println("  • Prof: " + a.getTeacherName() + " | Sec: " + a.getSection() + " | " + a.getDayOfWeek() + " " + a.getStartTime() + " - " + a.getEndTime());
            }
        }
        InputReader.pause();
    }

    private void printTeacherTable(List<Teacher> teachers) {
        System.out.println("\n" + String.format("%-5s | %-12s | %-30s | %-22s | %-10s", "ID", "DOCUMENTO", "NOMBRE COMPLETO", "ESPECIALIDAD", "ESTADO"));
        System.out.println("-----------------------------------------------------------------------------------------");
        for (Teacher t : teachers) {
            System.out.println(String.format("%-5d | %-12s | %-30s | %-22s | %-10s",
                    t.getId(),
                    t.getDocumentType() + " " + t.getDocumentNumber(),
                    t.getFullName(),
                    t.getSpecialty() != null ? t.getSpecialty() : "-",
                    t.getStatus()));
        }
        System.out.println("(" + teachers.size() + " profesores listados)");
        InputReader.pause();
    }
}
