# AI Access Tokens and Scopes

This document defines access token scopes and recommended JWT claims for controlling AI visibility and actions across the Suuka Clean platform. The backend must enforce scopes for every API that returns AI data or executes AI-driven workflows. The frontend must check scopes before showing UI affordances such as `Approve` buttons.

## Scope Definitions

- `ai.read` — Read-only access to AI-generated insights and recommendations. (Clients, Cleaners, Supervisors, Admins with read-only needs)
- `ai.recommend` — Ability to generate or request new AI recommendations (server-side eligibility). Usually used by services rather than UI users.
- `ai.queue.read` — View AI recommendations queue (Admins, Supervisors with elevated access)
- `ai.queue.manage` — Update status of queue items (approve, schedule, ignore). Admins only.
- `ai.audit.read` — Read audit logs for AI recommendations and actions. Admins and auditors.
- `ai.audit.write` — Write audit entries for programmatic workflows (system use).
- `ai.approve` — Explicit permission to approve AI recommendations that trigger critical actions. Admins only.
- `ai.config` — Permission to change AI configuration and thresholds. System administrators only.

## Role → Scope Mapping (recommended)

- Client: `ai.read`
- Cleaner: `ai.read`
- Supervisor: `ai.read`, `ai.queue.read`
- Admin: `ai.read`, `ai.queue.read`, `ai.queue.manage`, `ai.audit.read`, `ai.approve`, `ai.config`

Adjust mappings to fit your RBAC system; prefer granting scopes to roles rather than individual users where possible.

## Sample JWT Claims

A minimal JWT payload example (signed by your auth server):

{
  "sub": "user:12345",
  "name": "Emma Admin",
  "role": "admin",
  "scopes": ["ai.read","ai.queue.read","ai.queue.manage","ai.audit.read","ai.approve"],
  "iss": "https://auth.suuka.example",
  "iat": 1620000000,
  "exp": 1620003600
}

Backend enforcement should validate `iss`, `exp`, and the presence of required scopes.

## Frontend Guidance

- To determine if a user can see AI cards: check `scopes` includes `ai.read`.
- To render `Approve` buttons: check `scopes` includes `ai.approve` or `ai.queue.manage`.
- Avoid showing UI controls that the user cannot use — hide rather than disable where appropriate to reduce confusion.

Example (pseudo):

if (user.scopes.includes('ai.read')) {
  showAIInsightCard()
}

if (user.scopes.includes('ai.approve')) {
  showApproveButton()
}

## Backend Enforcement

- All endpoints that return AI recommendations must verify `ai.read` scope.
- Endpoints that change AI queue state must verify `ai.queue.manage` and log the action to the audit log with `ai.audit.write`.
- Approve endpoints must validate `ai.approve` and record decision-maker identity.

## Audit Requirements

Every AI recommendation lifecycle change must be logged with:

- `aiModule` (string)
- `recommendationId` (uuid)
- `userId`
- `scopes` used
- `action` (approve|ignore|schedule)
- `timestamp`
- `notes`

Store logs in an append-only audit store and surface them in the Admin `Audit Logs` view.

## Notes

- Scopes should be minimal and strictly audited.
- Use short-lived tokens for UI sessions; refresh via secure refresh tokens.
- Consider integrating with your existing RBAC/ACL system and API gateway for scope enforcement.
