# Plateable Eats: safe Vercel container deployment

This branch prepares the full Spring Boot application and its existing frontend to run on Vercel's Container runtime (currently beta).

## 1. Security actions before any deployment

The public repository previously included a live-looking database password in `src/main/resources/application.properties` and a hardcoded staff password.

- Rotate the affected Supabase database password **immediately**. Assume it was exposed, including in Git history.
- Set a new, strong `STAFF_PASSWORD`. Do not reuse the old staff password.
- Review database logs and access permissions for suspicious activity.
- Remove historical secrets with a coordinated Git history rewrite if needed, but **rotation is necessary regardless**. The code changes here do not purge older commits.
- Do not use real customer information in this demonstration until the security and privacy setup has been reviewed.

## 2. Environment variables

Configure these as Vercel project environment variables. None belong in Git.

| Name | Purpose |
|---|---|
| `DATABASE_URL` | JDBC PostgreSQL URL for the rotated Supabase database (use the correct pooler URL and JDBC options) |
| `DB_USERNAME` | Supabase PostgreSQL username |
| `DB_PASSWORD` | Newly rotated database password |
| `STAFF_USERNAME` | Optional staff username, defaults to `staff` |
| `STAFF_PASSWORD` | Strong staff password; staff login is disabled when absent |
| `PORT` | Set to `8080` in Vercel because the container listens on port 8080 |

For local development, use a local PostgreSQL database or a separately configured development database.

## 3. Build locally

Java 17 and Maven are sufficient for local development:

```bash
mvn clean verify
mvn spring-boot:run
```

The test suite runs against an isolated H2 database through `application-test.properties`. It must never use your production Supabase database.

Docker is used for the Vercel deployment:

```bash
docker build -f Dockerfile.vercel -t plateable-eats .
docker run --rm -p 8080:8080 -e DATABASE_URL=jdbc:postgresql://host:5432/db -e DB_USERNAME=your_username -e DB_PASSWORD=your_new_password -e STAFF_PASSWORD=your_new_staff_password plateable-eats
```

Do not paste secrets into shared terminal screenshots or shell history.

## 4. Deploy on Vercel

1. In Vercel, import `Banelenelson02/platable_eats` from GitHub.
2. Set the project's root directory to the repository root.
3. Use the Container framework preset. `Dockerfile.vercel` and `vercel.json` are already included.
4. Configure environment variables for the appropriate Preview and Production environments, including `PORT=8080`.
5. Trigger a preview deployment and verify `/actuator/health`, `/api/menu`, and a complete checkout using **dummy test data only**.
6. Confirm staff routes reject requests without authentication.
7. Only when the preview is verified, promote/merge to production and retire the Railway deployment.

Vercel Container runtime is in beta and uses Vercel Functions billing. Check plan limits and function duration before sending real traffic.

## 5. Scope and future improvements

The demo is **not** a payment processor. Anonymous checkout creates orders and does not collect payments. Public checkout needs abuse protection (rate limiting/bot prevention) before real-world use. Consider a dedicated staff dashboard, structured migrations (Flyway), reservation availability rules, and BigDecimal for money values.
