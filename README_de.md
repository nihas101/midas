# Midas

**Sprachen**: [English](README.md) | [Deutsch](README_de.md)

Midas ist ein Programm zur Erstellung von Finanzberichten für Gesellschafter. Es ermöglicht die Eingabe von Buchungen
und das Erzeugen verschiedener Berichte.
Die Hauptziele sind Zukunftssicherheit, langfristige Kompatibilität und eine browserbasierte Benutzeroberfläche.

## Verwendung

Um Midas auszuführen, können Sie die JAR-Datei verwenden.

### Voraussetzungen

- **Java 21 oder neuer** muss auf Ihrem System installiert sein. [Temurin](https://adoptium.net/temurin/releases) wird
  dafür empfohlen.

### Eigenständige Ausführung

1. Laden Sie die neueste `midas.jar` herunter.
2. Starten Sie das Programm via:
    - Doppelklick auf `midas.jar`.
    - oder ausführen von `java -jar midas.jar` im Terminal.

Die Anwendung startet einen lokalen Webserver, und die Benutzeroberfläche ist in Ihrem Webbrowser unter
`http://localhost:8082` (oder dem konfigurierten Port) zugänglich.

### Datenspeicherung

Die Anwendung speichert ihre Daten in `midas.db`.

## Konfiguration

Midas verwendet Spring Boot Konfigurationsdateien (z.B. `application.properties` oder `application.yml`) zur
Konfiguration. Konfigurationsdateien befinden sich typischerweise innerhalb der Anwendung oder in einem angegebenen
Konfigurationsverzeichnis.

## Anwendungseigenschaften

Die Anwendung kann über `application.properties` konfiguriert werden. Nachfolgend sind einige der wichtigsten
Eigenschaften aufgeführt:

### Midas

* `midas.i18n`
    * `default-locale`: Legt das Standard-Gebietsschema für die Anwendung fest (z.B. `en`, `de`).
    * `force-default-language`: Wenn `true`, erzwingt die Anwendung die Verwendung des `default-locale` unabhängig von
      den Browsereinstellungen.
* `midas.theme.default-theme`: Legt das Standard-Theme für die Anwendung fest (z.B. `dark`).
* `midas.ui`
    * `hide-theme-toggle`: Wenn `true`, wird der Theme-Umschaltknopf in der Benutzeroberfläche ausgeblendet.
    * `hide-language-selector`: Wenn `true`, wird die Sprachauswahl in der Benutzeroberfläche ausgeblendet.
* `midas.desktop`
    * `auto-shutdown-enabled`: Wenn `true`, wird die Anwendung automatisch heruntergefahren, sobald keine
      Browserfenster (und Sessions) mehr aktiv sind (Standard: `true`).
    * `grace-period-seconds`: Die Zeitspanne (in Sekunden), die die Anwendung wartet, nachdem das letzte Browserfenster
      geschlossen wurde, bevor sie herunterfahren wird (Standard: `60`).
* `midas.export.pdf.template-path`: Pfad zu den HTML‑Templates, die über [Thymeleaf](https://www.thymeleaf.org/) für den
  PDF‑Export gerendert werden.
    * Siehe `src/main/resources/templates/export` für die Standard‑Templates.
    * Siehe `de.nihas101.midas.export.pdf.PdfViewData` für die Eingabedatenstruktur.
* `spring`
    * `datasource.url`: Die JDBC‑URL für die SQLite‑Datenbank (z.B. `jdbc:sqlite:midas.db`).
    * `jpa.show-sql`: Wenn `true`, protokolliert Hibernate alle SQL‑Queries in der Konsole.
* `server.port`: Der Port, auf dem die Anwendung ausgeführt wird (z.B. `8082`).
* `vaadin.launch-browser`: Wenn auf `true` gesetzt, öffnet sich automatisch ein Browserfenster zur Anwendungs‑URL beim
  Start.
