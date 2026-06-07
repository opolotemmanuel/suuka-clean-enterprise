# Infrastructure as Code (IaC)

This project uses Terraform to provision Azure resources reproducibly and with minimal cost.

Principles

- Default to a logical multi-tenant deployment (shared infrastructure, tenant-scoped data) to minimize costs.
- Use VNets and Private Endpoints to provide network isolation with low operational cost.
- Keep Terraform code modular: `platform` module for shared infra, `tenant` module for tenant overrides.
- Use remote state in Azure Storage with state locking.

Suggested layout

```
/infra/terraform/
  main.tf
  variables.tf
  outputs.tf
  modules/
    platform/
    tenant/
  environments/
    prod.tfvars
    staging.tfvars
```

Quick commands (local)

```bash
cd infra/terraform
terraform fmt
terraform init
terraform plan -var-file=environments/staging.tfvars
terraform apply -var-file=environments/staging.tfvars
```

Remote runs

- Use GitHub Actions to run `terraform plan` on PRs and `terraform apply` on protected `main` merges with approvals.

Security

- Keep secrets out of code; store sensitive values in Azure Key Vault and pass references into Terraform.
- Use service principals or OIDC tokens for CI to authenticate to Azure.
