# Midas

Midas ist ein geplantes Tool, das eine ältere Clipper-Anwendung zur Erstellung von Finanzberichten ersetzen soll. Es wird Finanzdaten über ein Jahr sammeln und Benutzern ermöglichen, verschiedene Berichte (Text oder PDF) zu erstellen. Die Hauptziele sind Zukunftssicherheit, langfristige Kompatibilität und eine moderne, interaktive browserbasierte Benutzeroberfläche.

## Projektziele

*   **Zukunftssicherheit:** Entwickelt für langfristige Kompatibilität und Stabilität.
*   **Browser-Oberfläche:** Moderne, interaktive Web-basierte Benutzeroberfläche.
*   **Finanzberichterstattung:** Sammeln jährlicher Finanzdaten und Erstellen von Berichten (Text/PDF).
*   **Clipper-Ersatz:** Dient als moderner Ersatz für eine ältere Clipper-Anwendung.

## Technologie-Stack

*   **Sprache:** Java
*   **Build-Tool:** Maven
*   **Anwendungs-Framework:** Spring Boot (für die Backend-Struktur)
*   **Benutzeroberfläche:** Vaadin (für interaktive Browser-UI)
*   **Datenbank:** SQLite
*   **Testen:** JUnit (für Unit-/Integrationstests)

## Verwendung

Um Midas auszuführen, führen Sie die JAR-Datei aus. Die Anwendung startet einen lokalen Webserver, und die Benutzeroberfläche ist in Ihrem Webbrowser zugänglich.

    $ java -jar midas-0.1.0-standalone.jar [args]

## Konfiguration

Midas verwendet Spring Boot Konfigurationsdateien (z.B. `application.properties` oder `application.yml`) zur Konfiguration. Konfigurationsdateien befinden sich typischerweise innerhalb der Anwendung oder in einem angegebenen Konfigurationsverzeichnis.

## Bereitstellung & Verteilung

Das primäre Bereitstellungsziel ist ein eigenständiges JVM JAR, das mit Maven erstellt wird.

## Fehlerbehandlung & Protokollierung

Eine robuste Protokollierung wird mit einem Standard-JVM-Protokollierungs-Framework implementiert, wobei die Protokolldateien basierend auf konfigurierten Richtlinien rotiert werden, um bei der Fehlerbehebung und Prüfung zu helfen.

## Anwendungseigenschaften

Die Anwendung kann über `application.properties` konfiguriert werden. Nachfolgend sind einige der wichtigsten Eigenschaften aufgeführt:

### Midas
*   `midas.i18n.default-locale`: Legt das Standard-Gebietsschema für die Anwendung fest (z.B. `en`, `de`).
*   `midas.i18n.force-default-language`: Wenn `true`, erzwingt die Anwendung die Verwendung des `default-locale` unabhängig von den Browsereinstellungen.
*   `midas.theme.default-theme`: Legt das Standard-Theme für die Anwendung fest (z.B. `dark`).
*   `midas.ui.hide-theme-toggle`: Wenn `true`, wird die Theme-Umschalttaste in der Benutzeroberfläche ausgeblendet.
*   `midas.ui.hide-language-selector`: Wenn `true`, wird die Sprachauswahl in der Benutzeroberfläche ausgeblendet.
*   `midas.desktop.auto-shutdown-enabled`: Wenn `true`, wird die Anwendung automatisch heruntergefahren, sobald keine Browserfenster (und Sessions) mehr aktiv sind (Standard: `true`).
*   `midas.desktop.grace-period-seconds`: Die Zeitspanne (in Sekunden), die die Anwendung wartet, nachdem das letzte Browserfenster geschlossen wurde, bevor sie heruntergefahren wird (Standard: `60`).
*   `server.port`: Der Port, auf dem die Anwendung ausgeführt wird (z.B. `8080`).
*   `vaadin.launch-browser`: Wenn auf `true` gesetzt, öffnet sich automatisch ein Browserfenster zur Anwendungs-URL beim Start.
*   `spring.datasource.url`: Die JDBC-URL für die SQLite-Datenbank (z.B. `jdbc:sqlite:midas.db`).
*   `spring.jpa.show-sql`: Wenn `true`, protokolliert Hibernate alle SQL-Queries in der Konsole.