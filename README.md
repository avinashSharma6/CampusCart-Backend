# CampusCart Backend

CampusCart's backend is a Java 21 / Spring Boot application using Spring MVC, Thymeleaf, Spring Security, Spring Data JPA, Hibernate, and MySQL.

## Contents

- `src/main/java/com/campuscart/` — controllers, services, entities, repositories, security, configuration, and image storage.
- `src/main/resources/application.properties` — Spring Boot and database configuration.
- `database/setup.sql` — creates the MySQL database.
- `database/clean_demo_listings.sql` — optional cleanup script that removes listings and their dependent reports and wishlist entries while preserving users and categories.
- `pom.xml` — Maven dependencies and packaging configuration.

## Configuration

The root `.env` file supplies `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `PORT`, and `UPLOAD_DIR`. Keep `.env` private and use `.env.example` as a template.

## Build and run

From the project root:

```bash
mvn -f backend/pom.xml clean package -DskipTests
./run.sh
```

The Maven build packages templates and static assets from `../frontend/` into the Spring Boot application.

## Database safety

The application uses `spring.jpa.hibernate.ddl-auto=update`; it does not automatically clear listing data. Back up the database before running any cleanup script. The cleanup script is manual and is not run when the application starts.
