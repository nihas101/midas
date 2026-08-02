# Developer Documentation

## 1. Technical Base

* Java, Spring Boot & Vaadin
* **Build Tool**: Maven
* **Database**: SQLite
* **Database Migration**: Liquibase

## 2. Project Structure

The project follows a modular package structure under `de.nihas101.midas`:

* **`bookings`**: Core logic for financial entries
* **`shareholders`**: Management of shareholder data
* **`openingbalance`**: Specialized handling for yearly opening balances
* **`interest`**: Interest rate management and calculation logic
* **`desktop`**: Desktop-specific features like auto-shutdown and browser integration
* **`ui`**: Vaadin views and components
* **`config`**: Spring Boot configuration classes and properties
* ...

## 3. Development Workflow

### 3.1. Building and Running

To build the project and compile the frontend:

```bash
mvn clean package
```

To run the application locally:

```bash
mvn spring-boot:run
```

The app will be available at `http://localhost:8082`

### 3.2. Database Migrations

Database schema changes are managed via Liquibase. Changelogs are located in `midas-core/src/main/resources/db/changelog`

* **Never** modify an existing changelog file
* Always create a new XML file for changes and register it in `db.changelog-master.xml`

### 3.3. Financial Precision

Always use the `MoneyAmount` class for financial values to avoid floating-point errors

## 4. UI & Theme

* **Theme**: Located in `midas-vaadin/src/main/frontend/themes/midas-theme`
* **Styling**: Use `styles.css` for custom CSS

## 5. Desktop Features

* **Auto-Shutdown**: Controlled by `DesktopLifecycleService`. It monitors active browser tabs via
  `VaadinDesktopInitListener`
* **Port Handling**: `MidasApplication.java` contains logic to detect if the port is already in use. If so, it redirects
  the user to the already running instance in their default browser

## 6. Release Process

The release process is fully automated via GitHub Actions

### 6.1. Creating a New Release

1. Ensure all changes are pushed to `main`
2. Navigate to the "Releases" section on GitHub
3. Click **"Draft a new release"**
4. Create a new tag (e.g., `v1.0.0`)
5. Click **"Publish release"**

### 6.2. Automation Details

The `.github/workflows/maven-publish.yml` workflow will:

1. Trigger on the release creation
2. **Synchronize Version**: Extract the version from the tag (e.g., `1.0.0`) and update `pom.xml` automatically
3. **Production Build**: Build the project with the `-Pproduction` profile (optimizes frontend)
4. **Upload Assets**: Attach `midas.jar` (an executable Fat-JAR) to the release

## 7. Stress Testing & Performance

For testing system responsiveness under heavy load, the project includes a dedicated `stress` profile.

### 7.1. Configuration and Database Isolation

Running under the `stress` profile activates the configuration in `application-stress.properties` which:
* Points to a separate database file: `midas-stress.db` (so your development and unit test databases remain untouched).
* Disables automatic browser launching and Vaadin UI auto-shutdown (to prevent JVM termination during headless testing).
* Prevents the test database initializer from clearing database records on startup, allowing you to persist and reuse populated stress data.

### 7.2. Data Populator

The `DataPopulator` is a test utility that populates the database with massive amounts of mock data (~500,000 records) to stress test UI rendering and database queries.
* **Volume**: 100 shareholders, 1,000 bookings per shareholder per year, spanning 5 years.
* **Idempotency**: Skips insertion if the shareholders already exist in the database.
* **How to run**:
  ```bash
  mvn test -Dtest=DataPopulator -Dspring.profiles.active=stress
  ```

### 7.3. Running stress tests

* **How to run against the stress database**:
  ```bash
  mvn verify -Dspring.profiles.active=stress
  ```
