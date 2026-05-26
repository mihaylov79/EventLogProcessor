# Event Log Processor

Small Spring Boot service that processes an uploaded event log file (one JSON object per line), validates events, aggregates statistics, and returns results via a REST API.

## Requirements
- Java 21
- Maven (or use the Maven Wrapper included in the repo)

## How to Run

### 1) Start the application

```bash
./mvnw spring-boot:run
```

### 2) Call the API

Upload a file with one JSON object per line:

```bash
curl -X POST \
  -H "Content-Type: multipart/form-data" \
  -F "file=@/path/to/events.log" \
  http://localhost:8080/events/process
```

**Windows PowerShell:**

```bash
curl.exe -X POST `
  -F "file=@C:\path\to\events.log" `
  http://localhost:8080/events/process
```

**Postman:**
- Method: `POST`
- URL: `http://localhost:8080/events/process`
- Body -> `form-data`
- Key: `file` (type: File), Value: select your log file
- Click `Send`

The response is JSON with aggregated statistics.

### 3) Run tests

```bash
./mvnw test
```

## Design Decisions
- **Pipeline structure**: parse -> validate -> aggregate. Each stage has a single responsibility and keeps the code testable.
- **Jackson tree parsing**: `readTree` + safe extraction allows partial parsing without failing the whole event on a single bad field.
- **Validation separated from parsing**: parser collects raw fields; validator enforces business rules.
- **Aggregation on valid events only**: invalid lines are counted but do not affect statistics.
- **REST API with multipart upload**: matches the requirement of reading a file and is easy to test with curl/Postman.

## Assumptions
- Timestamps are ISO-8601 and validated with `Instant.parse`.
- UUID fields must be valid UUID strings.
- Blank strings are treated as missing/invalid.
- Purchase amount must be a valid number and greater than 0.
- For ties in "most active user", the top-3 sorting breaks ties by UUID string order.

## Tradeoffs
- **Memory vs simplicity**: streaming is used for file processing; no full file load.
- **Strict validation**: invalid fields result in the entire event being invalid to keep logic simple and deterministic.
- **No persistence**: results are returned immediately; no database or storage.

## Project Structure (high-level)
- `parser`: safe JSON parsing
- `validation`: field and action validation rules
- `aggregation`: statistics accumulation
- `processing`: pipeline orchestration
- `controller`: REST endpoint
