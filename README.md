# Midas

**Languages**: [English](README.md) | [Deutsch](README_de.md)

Midas is a planned tool designed to replace an older Clipper application for creating financial reports. It will gather
financial data over a year and enable users to generate various reports (text or PDF). The primary goals are
future-proofing, long-term compatibility, and a modern, interactive browser-based user interface.

## Project Goals

* **Future-Proofing:** Designed for long-term compatibility and stability.
* **Browser Interface:** Modern, interactive web-based user interface.
* **Financial Reporting:** Gather annual financial data and generate reports (text/PDF).
* **Clipper Replacement:** Serve as a modern replacement for an older Clipper application.

## Usage

To run Midas, you can use the standalone JAR or the launcher scripts provided in the releases.

### Prerequisites

- **Java 21 or newer** must be installed on your system. [Temurin](https://adoptium.net/temurin/releases) comes recommended.

### Standalone Execution

1. Download the latest `midas.jar` and the launcher script for your OS (`midas.bat` for Windows, `midas.sh` for
   Linux/macOS).
2. Place them in the same folder.
3. Run the launcher script:
    - **Windows**: Double-click `midas.bat`.
    - **Linux/macOS**: Run `./midas.sh` in a terminal.

Alternatively, run the JAR directly from the command line:

    $ java -jar midas.jar

The application will launch a local web server, and the user interface will be accessible in your web browser at
`http://localhost:8082` (or the configured port).

### Data Persistence

The application stores its data in `${user.home}/.midas/midas.db` to ensure your data is preserved between runs and
across different versions of the application.

## Configuration

Midas uses Spring Boot configuration files (e.g., `application.properties` or `application.yml`) for configuration.
Configuration files will typically be located within the application or in a specified configuration directory.

## Deployment & Distribution

The primary deployment target is a standalone JVM JAR, built using Maven.

## Error Handling & Logging

Robust logging is implemented using a standard JVM logging framework, with log files rotated based on configured
policies to assist with debugging and auditing.

## Application Properties

The application can be configured using `application.properties`. Below are some of the key properties:

* `midas.i18n.default-locale`: Sets the default locale for the application (e.g., `en`, `de`).
* `midas.i18n.force-default-language`: If `true`, forces the application to use the `default-locale` regardless of
  browser settings.
* `midas.theme.default-theme`: Sets the default theme for the application (e.g., `dark`).
* `midas.ui.hide-theme-toggle`: If `true`, hides the theme toggle button in the UI.
* `midas.ui.hide-language-selector`: If `true`, hides the language selector in the UI.
* `midas.desktop.auto-shutdown-enabled`: If `true`, the application will automatically shut down when no browser
  windows (and sessions) are active (default: `true`).
* `midas.desktop.grace-period-seconds`: The amount of time (in seconds) the application will wait after the last browser
  window is closed before shutting down (default: `60`).
* `server.port`: The port on which the application will run (e.g., `8082`).
* `vaadin.launch-browser`: If set to `true`, a browser window will automatically open to the application URL upon
  startup.
* `spring.datasource.url`: The JDBC URL for the SQLite database (e.g., `jdbc:sqlite:midas.db`).
* `spring.jpa.show-sql`: If `true`, Hibernate will log all SQL statements to the console.
