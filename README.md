# PulseDesk - AI Comment-to-Ticket Triage System

PulseDesk collects user comments and uses AI to decide whether they should become support tickets — generating a title, category, priority, and summary.

If the AI fails or returns invalid output, the system flags the comment for manual review instead of crashing.

## Tech Stack

- Java 21 + Spring Boot 3.5
- H2 in-memory database
- Hugging Face Inference API (Qwen2.5-72B)

## Design Decisions

- Single AI call per comment instead of multiple — one request returns all ticket fields as JSON
- Fallback logic if AI response fails to parse — app continues without crashing, if AI response is invalid or cannot be parsed we set manualReview = true and show it in UI
- H2 in-memory database — no setup needed
- DTO-based request handling — cleaner than raw maps, makes the API contract explicit

## Setup

Requirements: Java 21+ and Maven 3.6+

1. Clone the repo and open the project
2. Replace `hf_your_token_here` with your real token from huggingface.co in `src/main/resources/application.properties`
3. Run with `mvn spring-boot:run`
4. Open http://localhost:8080

## Live Demo

https://pulsedesk-production-58c3.up.railway.app

## Usage

### UI

Open http://localhost:8080 — type a comment, select a channel, and click Submit. The AI will analyze it and create a ticket if needed. Comments and tickets appear in the tables below.

### API Endpoints

| Method | Endpoint      | Description      |
| ------ | ------------- | ---------------- |
| POST   | /comments     | Submit a comment |
| GET    | /comments     | Get all comments |
| GET    | /tickets      | Get all tickets  |
| GET    | /tickets/{id} | Get ticket by ID |

## Notes

- Data resets when the app restarts (in-memory H2 database)
- Hugging Face token required — get one free at huggingface.co

## Possible Improvements

- Persistent database (PostgreSQL)
- Async processing so the API responds faster
- Retry logic for API failures
- More validation
