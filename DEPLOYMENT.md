# Deployment

This document describes the deployment strategy for the cleaning services platform on Azure.

## Azure Infrastructure (multi-tenant, cost-aware)

- **Azure App Service** (recommended) for hosting tenant-aware backend services. AKS is not recommended for the initial platform due to higher cost and operational overhead.
- **Azure Database for PostgreSQL** for shared multi-tenant data (schema-per-tenant or row-discriminator patterns), with optional tenant-scoped databases when isolation is required.
- **Azure Maps API** for geolocation, routing, and distance computation.
- **Azure Key Vault** for storing payment gateway keys, API secrets, and credentials.
- **Azure Web PubSub / Event Grid** for real-time notifications and dispatch events.
- **Azure Active Directory** for enterprise identity and role-based access.
- **Azure Virtual Network + Private Endpoints** to provide low-cost network isolation between platform components and databases.
- **Azure Monitor / Application Insights** for usage tracking, alerts, and performance metrics.
- **Azure OpenAI Service** for AI assistants and recommendation agents (use GPT-4o-mini/GPT-4o models via Spring AI or LangChain4j). Store model keys in Azure Key Vault and restrict access via managed identities.

## Enterprise Tenant Deployment (optional)

For enterprise customers that require dedicated deployments, offer an optional tenant-isolated deployment into the customer's Azure tenant. This increases cost but provides strict separation for regulated requirements.

- Deploy tenant-isolated resources using service principals or managed identities.
- Configure tenant-specific Azure Key Vaults and PostgreSQL instances when strict data separation is required.
- Prefer logical multi-tenancy (shared infrastructure) by default to reduce costs; provide tenant-isolated deployments only on contractual request.

## Environment Setup

- `AZURE_CLIENT_ID`
- `AZURE_CLIENT_SECRET`
- `AZURE_TENANT_ID`
- `AZURE_MAPS_KEY`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `PAYMENT_GATEWAY_API_KEY`

## Deployment Steps

1. Provision Azure resources.
2. Create PostgreSQL database and schema.
3. Deploy Spring Boot app to Azure App Service.
4. Configure Azure Key Vault and link it to App Service.
5. Set application settings and secrets.
6. Verify API health and database connectivity.

## Security Best Practices

- Never store secrets in code or source control.
- Use managed identities where possible.
- Restrict database access to App Service and use private endpoints rather than public IPs.
- Enable HTTPS and enforce TLS.
- Audit app and database logs for unusual activity.
- Enforce enterprise role-based access control for corporate accounts.
- Use Azure Policy and resource groups to separate production, staging, and tenant resources.

## Infrastructure as Code & CI/CD (automate, free tooling)

Automate provisioning and deployments using Infrastructure as Code and free CI/CD tooling:

- Use Terraform (open-source) to provision Azure resources: VNets, private endpoints, App Service, PostgreSQL, Key Vaults, and resource groups. (AKS can be added later if the platform requires container orchestration at scale, but avoid it initially to reduce cost.)
- Keep Terraform configuration in `/infra/terraform` and use remote state (Azure Storage) for team collaboration.
- Use GitHub Actions to run `terraform fmt`, `terraform init`, `terraform plan` on PRs and `terraform apply` via a protected workflow for production.
- Use GitHub Actions to build and test the Spring Boot application (Maven/Gradle), produce artifacts, and deploy to App Service or AKS.
- Store required secrets in GitHub Secrets and reference them securely in workflows; prefer service principals or OIDC tokens for short-lived credentials.

## AI & Responsible AI Guidance

- **Model hosting**: Use Azure OpenAI Service for hosted models (GPT-4o-mini / GPT-4o) and call models from backend services via secure Key Vault-stored credentials.
- **Secrets**: Never store OpenAI keys in code or GH PRs; reference them from Azure Key Vault. Use GitHub OIDC or service principals with minimal scopes for CI.
- **Human-in-the-loop**: Automate low-risk tasks (chat responses, product summarization) and require human approval for critical actions (e.g., auto-assigning high-value enterprise jobs, releasing escrow exceptions). Implement an `approvals` queue and notification flow for operators.
- **Auditing & logging**: Log AI inputs, model responses, and decision metadata to Application Insights or a secure audit store for traceability and compliance. Redact PII before logging where required.
- **Rate limiting & cost control**: Throttle calls to the OpenAI API, cache repeated responses, and monitor cost with alerts in Azure Monitor.

This approach minimizes manual steps, enforces reproducible infrastructure, and uses free/open tooling to reduce operational costs.
