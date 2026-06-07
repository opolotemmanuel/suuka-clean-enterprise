# CI/CD with GitHub Actions

Use GitHub Actions to automate build, test, IaC checks, and deployment. This uses free tiers available for many teams.

Recommended workflows

- `ci.yml` — Build and test Java (Maven/Gradle) on PRs.
- `iac-plan.yml` — Run `terraform fmt` and `terraform plan` on PRs.
- `deploy.yml` — Deploy application and apply approved Terraform changes on merge to `main` (protected branch).

Secrets and identity

- Store Azure service principal credentials or use GitHub OIDC to request short-lived tokens.
- Store other secrets (e.g., payment gateway keys) in GitHub Secrets and/or reference Azure Key Vault at runtime.

Example quick commands

```bash
# Run locally
mvn -B -DskipTests=false test
# Terraform
cd infra/terraform && terraform init && terraform plan
```
