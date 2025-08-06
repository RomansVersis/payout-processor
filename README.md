# Payout Processor

## Overview
Payout Processor is a Spring Boot application that processes daily payout files from government agencies (starting with Wakanda) and sends payout data to a debt collection system via REST API.  
It is designed with a focus on **reliability**, **extensibility**, and **guaranteed delivery**.

---

## Architecture
- **Controller**: `TriggerController` – manual trigger via `/api/trigger`.
- **Service**: `WakandaPayoutProcessor` (processing orchestration), `WakandaFileParser` (CSV parsing), `PayoutFileParser` (interface for future formats).
- **Client**: `PayoutApiClient` – REST calls with retries.
- **Infrastructure**: `SchedulerConfig` (daily job at 03:40), `RestTemplateConfig` (configurable HTTP client).
- **Domain**: `Payout`, `PayoutRequestDto`.

---

## Features
- ⏱ **Scheduled Processing** (03:40 daily) with SLA compliance.
- 📄 **Robust CSV Parsing** using Apache Commons CSV.
- 🔄 **Reliable API Calls** with Spring Retry.
- 🛠 **Error Handling** – failed files moved to `failed` directory.
- ➕ **Extensible** – easy to add new file parsers.
- 📝 **Centralized Logging** with `@Slf4j` and dedicated invalid record logs.

---

## Technologies
Java 17 • Spring Boot • Apache Commons CSV • Spring Retry • Lombok • JUnit 5 • Mockito • Maven

---

## Setup & Run
### Prerequisites
- Java 17+, Maven 3.x
- API endpoint: `https://intrum.wiremockapi.cloud/payout`

### Build & Start
```bash
mvn clean package
mvn spring-boot:run
