# Midas

Midas is a planned tool designed to replace an older Clipper application for creating financial reports. It will gather financial data over a year and enable users to generate various reports (text or PDF). The primary goals are future-proofing, long-term compatibility, and a modern, interactive browser-based user interface.

## Project Goals

*   **Future-Proofing:** Designed for long-term compatibility and stability.
*   **Browser Interface:** Modern, interactive web-based user interface.
*   **Financial Reporting:** Gather annual financial data and generate reports (text/PDF).
*   **Clipper Replacement:** Serve as a modern replacement for an older Clipper application.

## Technology Stack

*   **Language:** Java
*   **Build Tool:** Maven
*   **Application Framework:** Spring Boot (for backend structure)
*   **User Interface:** Vaadin (for interactive browser UI)
*   **Database:** SQLite
*   **Testing:** JUnit (for unit/integration testing)

## Usage

To run Midas, execute the standalone JAR file. The application will launch a local web server, and the user interface will be accessible in your web browser.

    $ java -jar midas-0.1.0-standalone.jar [args]

## Configuration

Midas uses Spring Boot configuration files (e.g., `application.properties` or `application.yml`) for configuration. Configuration files will typically be located within the application or in a specified configuration directory.

## Deployment & Distribution

The primary deployment target is a standalone JVM JAR, built using Maven.

## Error Handling & Logging

Robust logging is implemented using a standard JVM logging framework, with log files rotated based on configured policies to assist with debugging and auditing.

## License

Copyright © 2026 nihas101

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
https://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
