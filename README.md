# ERP Colegio Little Horizons - Sistema de Gestión Educativa Integral

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Architecture](https://img.shields.io/badge/Architecture-MVC%20%7C%20DAO%20%7C%20SOLID-success?style=for-the-badge)](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](LICENSE)

---

## 2. Descripción

### Resumen del propósito del sistema
El **ERP Colegio Little Horizons** es una solución empresarial educativa desarrollada en Java estándar con persistencia en MySQL 8.0+. Su propósito principal es centralizar, automatizar y sincronizar las operaciones académicas, disciplinarias, financieras, extracurriculares y de inventario del Colegio Internacional "Little Horizons", sustituyendo los antiguos registros dispersos en papel y hojas de cálculo por un flujo de datos transaccional, consistente y seguro.

### Problemas que resuelve
1. **Pérdida y desorden en notas académicas:** Centraliza las calificaciones en una escala estandarizada colombiana (`0.0` a `5.0`), calculando promedios por materia, periodos y cursos automáticamente mediante *Java Streams*.
2. **Desarticulación en actividades extracurriculares:** Administra clubes escolares validando en tiempo real los aforos máximos para evitar sobrecupos y gestiona la participación en eventos deportivos/académicos con premiación y puestos de honor.
3. **Inconsistencias y retrasos en facturación:** Automatiza el cobro de conceptos (matrículas, pensiones, becas) y genera en tiempo real **Facturas Electrónicas en formato PDF** que cumplen con la reglamentación colombiana DIAN (incluyendo CUFE SHA-256 y código QR renderizado).
4. **Pérdida de inventario en laboratorios y biblioteca:** Lleva un control estricto de préstamos a estudiantes y profesores, alertando de manera oportuna sobre devoluciones vencidas.
5. **Falta de visibilidad sobre asistencia y deserción:** Registra la asistencia diaria por materia y genera alertas automáticas para estudiantes con patrones críticos de inasistencia (>3 ausencias).

### Funcionalidades principales
* **Gestión de Estudiantes y Matrículas:** Registro personal, vinculación de acudientes/tutores legales, historial de matrículas por grado y asignación de materias por período.
* **Gestión de Profesores y Horarios:** Directorio docente, asignación de materias y detección automática de colisiones o cruces de horario.
* **Evaluación y Calificaciones:** Registro de notas con escala `DECIMAL(2,1)`, promedios automáticos y consulta de historial acumulado.
* **Control de Asistencia:** Registro diario con estados (*Presente, Ausente, Tardanza, Excusa Médica*) y reportes de riesgo por inasistencias.
* **Clubes y Eventos Extracurriculares:** Control de aforo máximo en inscripciones y seguimiento a competiciones escolares.
* **Facturación Electrónica DIAN:** Emisión de comprobantes y facturas PDF oficiales con CUFE generado por hashing criptográfico y código QR integrado.
* **Biblioteca y Laboratorios:** Catálogo clasificado, control de préstamos a docentes/alumnos y monitoreo de recursos vencidos.
* **Reportes y Analítica:** Cuadro de honor académico, análisis de cartera vencida (>30 días de mora) y métricas de inventario.

---

## 3. Autores

* **Desarrollador / Ingeniero de Software:** Samuel & Equipo de Desarrollo
* **Institución:** Colegio Internacional Little Horizons
* **Correo electrónico:** contacto@littlehorizons.edu.co / developer@littlehorizons.edu.co
* **Repositorio oficial:** [GitHub - Little Horizons ERP](https://github.com/usuario/Proyecto-ERP-para-el-Colegio-Little-Horizons-)

---

## 4. Tecnologías Utilizadas

* **Lenguaje de Programación:** Java SE 17 (LTS)
* **IDE Recomendado:** IntelliJ IDEA, Eclipse IDE o Visual Studio Code (con Extension Pack for Java)
* **Gestor de Dependencias:** Apache Maven 3.8+
* **Motor de Base de Datos:** MySQL Server 8.0 o superior (indispensable para la ejecución e integridad de los `CHECK constraints` de notas y horarios).
* **Librerías Externas (Dependencies):**
  * `com.mysql:mysql-connector-j:8.4.0`: Controlador oficial JDBC para comunicación de alto rendimiento con MySQL.
  * `com.github.librepdf:openpdf:1.3.40`: Generación programática de documentos PDF estilizados y profesionales (Facturación DIAN).
  * `com.google.zxing:core:3.5.3` y `javase:3.5.3`: Generación y renderizado de códigos QR bidimensionales para las facturas electrónicas.

---

## 5. Requerimientos Previos

Antes de ejecutar el sistema, asegúrese de contar con las siguientes herramientas instaladas y configuradas en su variable de entorno `PATH`:

1. **Java Development Kit (JDK):** Versión 17 o superior.
   * Verificación: `java -version` y `javac -version`
2. **Apache Maven:** Versión 3.8 o superior.
   * Verificación: `mvn -version`
3. **MySQL Server:** Versión **8.0 o superior** (Requisito estricto).
   * Verificación: `mysql --version`

> [!IMPORTANT]
> **MySQL 8.0+ Requerido:** El esquema de base de datos hace uso intensivo de validaciones nativas `CHECK (valor_nota >= 0.0 AND valor_nota <= 5.0)` y `CHECK (hora_fin > hora_inicio)`. Versiones anteriores a MySQL 8.0 ignoran los `CHECK constraints` en tiempo de ejecución.

---

## 6. Configuración de la Base de Datos

### 6.1. Creación de la base de datos
Abra su terminal o cliente MySQL (Workbench, DBeaver, línea de comandos) y ejecute el script maestro ubicado en `docs/database.sql`:

```bash
# Desde la raíz del proyecto:
mysql -u root -p < docs/database.sql
```

El script se encargará automáticamente de:
1. Crear la base de datos `erp_little_horizons` con codificación `utf8mb4`.
2. Crear todas las tablas con sus llaves primarias, foráneas, índices y restricciones `CHECK`.
3. Poblar la base de datos con **más de 45 registros de prueba coherentes y realistas** en cada una de las tablas principales (estudiantes, profesores, acudientes, materias, notas, pagos, clubes, recursos).

### 6.2. Configuración de credenciales
En la raíz del proyecto encontrará el archivo `config.properties.example`. Copie este archivo con el nombre `config.properties`:

```bash
cp config.properties.example config.properties
```

Edite `config.properties` con los datos de su servidor local de MySQL:

```properties
db.url=jdbc:mysql://localhost:3306/erp_little_horizons?useSSL=false&serverTimezone=America/Bogota&allowPublicKeyRetrieval=true
db.user=root
db.password=TU_PASSWORD_AQUI
```

> [!CAUTION]
> El archivo `config.properties` contiene credenciales sensibles y está incluido en `.gitignore`. Nunca lo publique ni lo suba a repositorios públicos.

### 6.3. Instalación del driver JDBC
El driver `mysql-connector-j` se descarga e instala automáticamente a través de Apache Maven al compilar el proyecto.

---

## 7. Instalación y Ejecución

### 7.1. Clonar el repositorio
```bash
git clone https://github.com/usuario/Proyecto-ERP-para-el-Colegio-Little-Horizons-.git
cd Proyecto-ERP-para-el-Colegio-Little-Horizons-
```

### 7.2. Abrir el proyecto en el IDE
* **IntelliJ IDEA:** `File` -> `Open` -> Seleccionar la carpeta raíz (donde se encuentra `pom.xml`). El IDE detectará el proyecto Maven automáticamente.
* **VS Code:** `File` -> `Open Folder` -> Seleccionar la carpeta raíz. Asegúrese de tener activa la extensión *Extension Pack for Java*.

### 7.3. Compilar
Para compilar el proyecto y descargar las dependencias ejecute:
```bash
mvn clean compile
```

### 7.4. Ejecutar
Para iniciar la aplicación de consola directamente con Maven:
```bash
mvn exec:java -Dexec.mainClass="com.littlehorizons.erp.Main"
```
O empaquete y ejecute el archivo `.jar`:
```bash
mvn clean package
java -jar target/erp-1.0.0.jar
```

---

## 8. Estructura del Proyecto

El proyecto está diseñado bajo una estricta arquitectura en capas **Modelo-Vista-Controlador (MVC)**, complementada con el patrón **DAO (Data Access Object)**:

```
Proyecto-ERP-para-el-Colegio-Little-Horizons-/
├── pom.xml                        # Configuración de dependencias Maven
├── config.properties.example      # Plantilla de credenciales de base de datos
├── .gitignore                     # Exclusión de binarios, credenciales y facturas
├── README.md                      # Documentación técnica exhaustiva
├── docs/
│   └── database.sql               # Script SQL maestro (DDL completo + DML con +45 datos/tabla)
└── src/
    └── main/
        └── java/
            └── com/
                └── littlehorizons/
                    └── erp/
                        ├── Main.java               # Punto de entrada de la aplicación
                        ├── config/
                        │   └── DatabaseConnection.java # Conexión JDBC Singleton hilo-segura
                        ├── model/                  # Entidades del dominio (POJOs puros)
                        │   ├── Student.java
                        │   ├── Guardian.java
                        │   ├── GradeLevel.java
                        │   ├── Enrollment.java
                        │   ├── Subject.java
                        │   ├── Teacher.java
                        │   ├── TeacherAssignment.java
                        │   ├── AcademicRecord.java
                        │   ├── Attendance.java
                        │   ├── Club.java
                        │   ├── ClubMembership.java
                        │   ├── SchoolEvent.java
                        │   ├── EventParticipant.java
                        │   ├── PaymentConcept.java
                        │   ├── Payment.java
                        │   ├── Invoice.java
                        │   ├── ResourceCategory.java
                        │   ├── Resource.java
                        │   └── ResourceLoan.java
                        ├── dao/                    # Acceso a datos con PreparedStatement
                        │   ├── StudentDao.java
                        │   ├── GuardianDao.java
                        │   ├── EnrollmentDao.java
                        │   ├── SubjectDao.java
                        │   ├── TeacherDao.java
                        │   ├── TeacherAssignmentDao.java
                        │   ├── AcademicRecordDao.java
                        │   ├── AttendanceDao.java
                        │   ├── ClubDao.java
                        │   ├── EventDao.java
                        │   ├── PaymentDao.java
                        │   └── ResourceDao.java
                        ├── controller/             # Lógica de negocio, validaciones y Streams
                        │   ├── StudentController.java
                        │   ├── TeacherController.java
                        │   ├── AcademicController.java
                        │   ├── ExtracurricularController.java
                        │   ├── FinancialController.java
                        │   ├── ResourceController.java
                        │   └── ReportController.java
                        ├── view/                   # Vistas interactivas de consola
                        │   ├── StudentView.java
                        │   ├── TeacherView.java
                        │   ├── AcademicView.java
                        │   ├── ExtracurricularView.java
                        │   ├── FinancialView.java
                        │   ├── ResourceView.java
                        │   └── ReportView.java
                        └── util/                   # Utilidades transversales
                            ├── AppLogger.java          # Sistema de logs a error_log.txt
                            ├── InputReader.java        # Lectura y validación de tipos por consola
                            ├── Validator.java          # Expresiones regulares y reglas de dominio
                            ├── QrCodeGenerator.java    # Generador ZXing en memoria de códigos QR
                            └── PdfInvoiceGenerator.java# Motor de facturas electrónicas en PDF
```

---

## 9. Principios y Patrones de Diseño Aplicados

### 9.1. Principios SOLID

1. **Single Responsibility Principle (SRP):**
   * *Explicación:* Cada clase posee una única responsabilidad bien delimitada.
   * *Ejemplo:* `PdfInvoiceGenerator` únicamente sabe cómo estructurar y renderizar un PDF de factura DIAN; no interactúa con la base de datos ni solicita inputs por consola. Por su parte, `AcademicRecordDao` solo gestiona persistencia SQL de calificaciones, delegando los cálculos de promedios al `AcademicController`.

2. **Open/Closed Principle (OCP):**
   * *Explicación:* El software está abierto a la extensión pero cerrado a la modificación.
   * *Ejemplo:* La entidad `ResourceLoan` maneja préstamos tanto a estudiantes como a profesores de manera polimórfica sin alterar la estructura base del recurso `Resource`.

3. **Liskov Substitution Principle (LSP):**
   * *Explicación:* Las clases del modelo y colecciones respetan los contratos esperados por los controladores sin alterar el comportamiento esperado del sistema.

4. **Interface Segregation Principle (ISP):**
   * *Explicación:* Los DAOs exponen interfaces e interfaces de métodos específicos para cada entidad, evitando que una vista o controlador dependa de operaciones ajenas a su contexto.

5. **Dependency Inversion Principle (DIP):**
   * *Explicación:* Los módulos de alto nivel (como los Controladores) no dependen de detalles de infraestructura de bajo nivel directamente cableados, sino que consumen la abstracción de base de datos a través del `DatabaseConnection` y los métodos estándar de los DAOs.

### 9.2. Patrones de Diseño

1. **Singleton Pattern (Creacional):**
   * *Implementación:* [`DatabaseConnection`](src/main/java/com/littlehorizons/erp/config/DatabaseConnection.java).
   * *Justificación:* Garantiza que exista una única instancia global responsable de leer las propiedades de configuración y gestionar las conexiones JDBC, evitando sobrecarga de memoria y lecturas redundantes en disco. Se implementó con *Double-Checked Locking* volátil para asegurar soporte multi-hilo seguro (*Thread-Safe*).

2. **Data Access Object (DAO) Pattern (Estructural):**
   * *Implementación:* Todos los componentes del paquete `com.littlehorizons.erp.dao`.
   * *Justificación:* Separa completamente la lógica de negocio de las sentencias SQL. Si en el futuro se migra el motor a PostgreSQL o se utiliza JPA/Hibernate, los controladores y vistas permanecen 100% inalterados.

3. **Model-View-Controller (MVC) Pattern (Arquitectónico):**
   * *Implementación:* Separación en paquetes `model/`, `view/` y `controller/`.
   * *Justificación:* Permite modificar la interfaz de usuario (por ejemplo, reemplazar las vistas de consola por una interfaz gráfica Swing o JavaFX, o una API REST) sin tocar la capa de negocio ni el modelo de datos.

---

## 10. Funcionalidades Principales por Módulo

### Módulo 1: Gestión de Estudiantes y Académica
* Registro de estudiantes con datos biomédicos (tipo de sangre, alergias, condiciones).
* Asociación de acudientes con control de parentesco y responsabilidad financiera.
* Matrículas por grado y año escolar.
* Inscripción en asignaturas por períodos académicos (I, II, III, IV).
* Directorio de profesores y asignación de materias con validación algorítmica para evitar conflictos o cruces de horario.
* Registro de notas de `0.0` a `5.0`.
* Cálculo automático de promedio por materia, GPA por período y promedio general de curso mediante **Java Streams**.
* Control diario de asistencia y detección automática de estudiantes con inasistencias críticas.

### Módulo 2: Actividades Extracurriculares
* Catálogo de clubes activos y supervisores a cargo.
* Inscripción de estudiantes con **validación estricta de cupo máximo** (*Aforo*).
* Seguimiento de membresías (*Activa, Retirada*).
* Calendario de eventos institucionales clasificados por tipo.
* Registro de participantes y premiación de competencias (*1er Lugar, 2do Lugar, 3er Lugar, Participante*).

### Módulo 3: Gestión Financiera y Facturación DIAN
* Catálogo de conceptos de pago (matrículas, mensualidades, transporte, uniformes).
* Registro detallado de cobros con número de recibo consecutivo y método de pago.
* Historial transaccional por alumno o por acudiente.
* Reporte dinámico de morosos mediante *Java Streams* que identifica quiénes superan el umbral de 30 días sin pagar.
* **Emisión de Factura Electrónica en PDF:**
  * Cabecera institucional del Colegio Little Horizons (Razón social, NIT, resolución DIAN).
  * Datos del cliente/acudiente y beneficiario.
  * Desglose de conceptos, cantidades, valores unitarios y totales.
  * Cálculo de CUFE simulado (Hash SHA-256).
  * Código QR simulado con la información tributaria insertado en el PDF.
  * Almacenamiento organizado en la carpeta `invoices/`.

### Módulo 4: Gestión de Recursos (Biblioteca y Laboratorios)
* Inventario de recursos catalogados (libros, microscopios, material deportivo, dispositivos).
* Préstamos independientes para estudiantes y docentes (respetando tablas dedicadas).
* Control de devoluciones con reactivación automática de estado a `Disponible`.
* Reporte inmediato de préstamos vencidos no entregados a la fecha.

### Módulo 5: Reportes y Analítica
* Cuadro de honor con los estudiantes con mayor promedio general del colegio.
* Alerta temprana de ausentismo escolar.
* Consolidado financiero de cartera en mora.
* Estado de inventario físico y préstamos pendientes.

---

## 11. Manejo de Errores y Logs

### Sistema de Logging Implementado
El sistema cuenta con una clase centralizada [`AppLogger`](src/main/java/com/littlehorizons/erp/util/AppLogger.java). Cada vez que una operación SQL o del sistema arroja una excepción, el error es capturado y escrito con marca de tiempo precisa (`LocalDateTime.now()`) y su *Stack Trace* completo en el archivo:
```
error_log.txt
```

### Estrategia de Manejo de Excepciones
1. **Try-with-resources:** Todas las conexiones (`Connection`), sentencias preparadas (`PreparedStatement`) y resultados (`ResultSet`) se abren dentro de bloques *try-with-resources* para garantizar el cierre seguro e inmediato de los cursores en la base de datos, previniendo fugas de memoria (*Resource Leaks*).
2. **Consultas 100% parametrizadas:** Ninguna sentencia SQL concatena strings de entrada del usuario, eliminando cualquier vulnerabilidad a ataques de Inyección SQL (*SQL Injection*).
3. **Manejo en la capa de Vista:** Las vistas capturan `IllegalArgumentException` para notificar al usuario de forma amigable sobre errores en sus datos de entrada y `SQLException` para informar problemas de persistencia sin detener intempestivamente la ejecución del programa.

---

## 12. Capturas de Pantalla (Estructura de Menú)

```
╔══════════════════════════════════════════════╗
║   ERP Colegio Little Horizons                ║
║   Sistema de Gestión Educativa Integral      ║
╚══════════════════════════════════════════════╝
1. Gestión de Estudiantes
2. Gestión Académica
3. Gestión de Profesores
4. Actividades Extracurriculares
5. Gestión Financiera
6. Gestión de Recursos
7. Reportes y Estadísticas
8. Salir
Seleccione una opción: _
```

---

## 13. Problemas Conocidos y Limitaciones

* La aplicación actual funciona a través de una interfaz de consola de comandos interactiva (CLI).
* La resolución DIAN y el CUFE son simulaciones criptográficas y visuales basadas en la especificación técnica nacional de facturación electrónica.

---

## 14. Futuras Mejoras

* Implementación de una interfaz gráfica de usuario en JavaFX o desarrollo de un frontend web moderno en React consumiendo una API Spring Boot.
* Integración directa con la pasarela de la DIAN mediante Web Services SOAP/REST para validación previa en tiempo real.
* Envío automatizado de la factura en PDF y boletines de notas al correo electrónico del acudiente mediante JavaMail.

---

## 15. Licencia

Este proyecto está bajo la Licencia Educativa MIT - Consulte el archivo de licencia para obtener más detalles.

---

## 16. Contacto

* **Colegio Internacional Little Horizons S.A.S.**
* **Dirección:** Carrera 45 # 120-35, Bogotá D.C., Colombia
* **Teléfono:** +57 (601) 789-0123
* **Correo Institucional:** facturacion@littlehorizons.edu.co / soporte@littlehorizons.edu.co
