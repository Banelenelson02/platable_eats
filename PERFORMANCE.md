# Spring Boot startup optimisation

This is a **single-container tuning change**. It does not move the frontend, change routes, or modify the Plateable Eats intro GIF.

## Baseline (Vercel production logs, 19 November 2026)

Production Spring Boot instances have reported roughly 18.5 to 25 seconds from application startup to ready. Example: `Started PlateableEatsApplication in 18.544 seconds` for the 00:27:49Z startup.

One observed timeline:
- 00:27:52 Java application logs first Spring Boot startup event.
- 00:27:57 embedded Tomcat initialises.
- 00:27:59 Hikari starts, connecting by 00:28:00.
- 00:28:05 JPA EntityManagerFactory initialisation completes.
- 00:28:09 application reports ready.

These are **server startup timings**, not a measurement of browser Largest Contentful Paint, page load or response times. Many parallel asset requests can start different cold container instances.

## Changes tested in this branch

1. `-XX:TieredStopAtLevel=1` prioritises JVM startup over maximum long-lived throughput. Roll back if sustained performance suffers.
2. Hibernate's schema mode is environment configurable. **After confirming the existing database schema**, set `HIBERNATE_DDL_AUTO=none` in Vercel production to stop schema modification/checks on every cold start. Retain the default `update` for local developer setup.
3. Bound the local Hikari connection pool to maximum 3, minimum 1 per application instance. This helps avoid competing database connections during bursts of serverless startup.

No application code, HTML, CSS, JavaScript or database table is changed.

## Verification / rollback

- Maven CI must pass.
- For a fair comparison, use several **cold container launches** on the same Vercel environment and compare each `Started PlateableEatsApplication in X seconds` log line before and after. Do not use the 9-minute GitHub Playwright browser installation time as a page-performance metric.
- Check GET `/actuator/health` = 200, GET `/api/menu` = 200, GET `/` = 200; confirm production checkout using only dummy information and clean up the test.
- Observe whether the old `could not connect to $PORT` timeouts recur.
- If the startup flag worsens warm throughput, remove `-XX:TieredStopAtLevel=1` and rebuild.
- If the schema is not prepared for `none`, revert `HIBERNATE_DDL_AUTO` in Vercel (or set `update`) before redeploying. Never run automatic schema migrations against real production data without review.

Important: This does **not** fix the underlying behaviour that Vercel routes the first cache miss for CSS/images through Spring Boot. If slow cold starts persist, compare a static-asset hosting change separately, instead of combining it with these JVM and Hibernate changes.
