# Midas

**Languages**: [English](README.md) | [Deutsch](README_de.md)

Midas is a tool for creating financial reports for shareholders. It allows the entry of financial data and enables users
to generate various reports.

The primary goals are future-proofing, long-term compatibility, and a browser-based user interface.

## Usage

To run Midas, you can use the standalone JAR or the launcher scripts provided in the releases.

### Prerequisites

- **Java 21 or newer** must be installed on your system. [Temurin](https://adoptium.net/temurin/releases) comes
  recommended.

### Standalone Execution

1. Download the latest `midas.jar`.
2. Run the jar by:
    - Double-clicking on `midas.jar`.
    - or running `java -jar midas.jar` in a terminal.

The application will launch a local web server, and the user interface will be accessible in your web browser at
`http://localhost:8082` (or the configured port).

### Data Persistence

The application stores its data in `midas.db`.

## Configuration

Midas uses Spring Boot configuration files (e.g., `application.properties` or `application.yml`) for configuration.
Configuration files will typically be located within the application or in a specified configuration directory.

## Application Properties

The application can be configured using `application.properties`. Below are some of the key properties:

* `midas.i18n`
    * `default-locale`: Sets the default locale for the application (e.g., `en`, `de`).
    * `force-default-language`: If `true`, forces the application to use the `default-locale` regardless of
      browser settings.
* `midas.theme.default-theme`: Sets the default theme for the application (e.g., `dark`).
* `midas.ui`
    * `hide-theme-toggle`: If `true`, hides the theme toggle button in the UI.
    * `hide-language-selector`: If `true`, hides the language selector in the UI.
* `midas.desktop`
    * `.auto-shutdown-enabled`: If `true`, the application will automatically shut down when no browser
      windows (and sessions) are active (default: `true`).
    * `grace-period-seconds`: The amount of time (in seconds) the application will wait after the last browser
      window is closed before shutting down (default: `60`).
* `midas.export.pdf.template-path`: The path to HTML templates to be rendered
  via [Thymeleaf](https://www.thymeleaf.org/) in the PDF export
    * See `src/main/resources/templates/export` for the default templates
    * See `de.nihas101.midas.export.pdf.PdfViewData` for the data structure used as input
* `spring`
    * `datasource.url`: The JDBC URL for the SQLite database (e.g., `jdbc:sqlite:midas.db`).
    * `jpa.show-sql`: If `true`, Hibernate will log all SQL statements to the console.
* `server.port`: The port on which the application will run (e.g., `8082`).
* `vaadin.launch-browser`: If set to `true`, a browser window will automatically open to the application URL upon
  startup.