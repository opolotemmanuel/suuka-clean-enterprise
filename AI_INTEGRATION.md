# AI Integration Guide

This document explains how to integrate Azure OpenAI into the platform responsibly.

## Goals

- Automate low-risk tasks to reduce operator load and speed up responses.
- Provide intelligent suggestions for dispatch and product recommendations.
- Ensure humans approve critical actions (enterprise bookings, escrow exceptions).

## Recommended Models & Libraries

- Use **Azure OpenAI Service** with `gpt-4o-mini` or `gpt-4o` for high-quality responses.
- Java clients: **Spring AI** (Spring integration) or **LangChain4j** for chains and prompt management.

## Integration Patterns

- Centralize all model calls in an `AIService` that handles:
  - Prompt templating and versioning
  - Rate-limiting / batching
  - Retry & backoff
  - Response validation and confidence scoring
  - PII redaction/tokenization before sending

- Implement a short-term caching layer for repeated queries (e.g., product summary) to reduce cost.

## Human-in-the-loop

- Categorize AI actions as `low-risk` or `critical`.
- `low-risk` actions: auto-executed (chat replies, summaries).
- `critical` actions: create an approval record with AI suggestion and metadata; push to operator queue; do not execute until an operator approves.

## Security

- Store all model keys in **Azure Key Vault** and access via managed identity.
- Do not persist raw prompts containing PII. Log metadata, hashes, and redacted content for auditing.

## Auditing & Monitoring

- Log AI inputs, outputs, and decision metadata to Application Insights or a secure audit store.
- Emit metrics for model usage and cost to Azure Monitor and configure cost alerts.

## Cost Controls

- Use smaller models for high-volume low-risk tasks (e.g., `gpt-4o-mini`) and reserve larger models for complex flows.
- Rate-limit and cache responses, and batch requests where possible.

## Example: Dispatch Recommendation Flow

1. Booking arrives -> `AIService.recommendCleaners(context)`
2. AI returns ranked cleaner list with confidence scores.
3. If `confidence > threshold` and low-risk, optionally auto-assign; else create approval ticket.
4. Operator approves -> system assigns cleaner and notifies them.

## Quick Implementation Checklist

- [ ] Add `AIService` abstraction in backend code.
- [ ] Add `ai/chat`, `ai/recommendations`, and approval endpoints.
- [ ] Configure Key Vault and store OpenAI key.
- [ ] Add logging and audit for AI actions.
- [ ] Add cost monitoring and alerts.
