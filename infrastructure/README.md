# Infrastructure

## Setup

Install CDKTF

```
brew install cdktf
```

Install Terraform https://developer.hashicorp.com/terraform/tutorials/aws-get-started/install-cli

Set the following environment variables

```
export GOOGLE_CLOUD_PROJECT=<projectId>
export REGION=<region name>
export REPO_NAME=composeflow-server
```

e.g.
```
export GOOGLE_CLOUD_PROJECT=composeflow
export REGION=us-central1
export REPO_NAME=composeflow-server
```

```
gcloud storage buckets create gs://${GOOGLE_CLOUD_PROJECT}-terraform --location=us-central1
```

## Deploy

```bash
cd infrastructure

gcloud auth login --update-adc --project ${GOOGLE_CLOUD_PROJECT}
cdktf deploy main
cdktf deploy storage
```
