## 1. Projects

### 1.1 List All Projects

```
GET /v1/projects
```

**Response:**

```json
{
  "projects": [
    {
      "id": 1,
      "name": "Example App",
      "description": "Demo project for testing",
      "slug": "example-app"
    },
    {
      "id": 2,
      "name": "Mobile API",
      "description": "Backend for mobile clients",
      "slug": "mobile-api"
    }
  ],
  "message": "Projects listed.",
  "type": "success"
}
```

### 1.2 Create Project

```
POST /v1/projects
```

**Body:**

```json
{
  "slug": "my-blog",
  "name": "My Blog Backend",
  "description": "a backend app for blogs"
}
```

**Response:**

```json
{
  "message": "Project Created.",
  "type": "success"
}
```

### 1.3 Update Project

```
PATCH /v1/projects/{projectSlug}
```

**Body:**
> Either `name` or `slug` or `description` or all can be updated.

```json
{
  "slug": "my-blog",
  "name": "My Blog Backend",
  "description": "an entire app for blogs"
}
```

**Response:**

```json
{
  "message": "Project Updated.",
  "type": "success"
}
```

### 1.4 Project Summary

```
GET /v1/projects/{projectSlug}/summary
```

**Response:**

```json
{
  "id": 1,
  "slug": "my-blog",
  "name": "My Blog Backend",
  "description": "an entire app for blogs",
  "envs": [
    {
      "name": "production",
      "secrets": ["DB_URL", "DB_PASSWORD"]
    },
    {
      "name": "staging",
      "secrets": ["TEST_DB_URL", "TEST_DB_PASSWORD"]
    }
  ],
  "type": "success",
  "message": "Project summary received."
}
```

## 2. Environment

### 2.1 List Environments

```
GET /v1/projects/{projectSlug}/envs
```

**Response:**

```json
{
  "envs": [
    {
      "id": 1,
      "name": "production"
    },
    {
      "id": 2,
      "name": "staging"
    }
  ],
  "type": "success",
  "message": "Environments listed."
}
```

### 2.2 Create Environment

```
POST /v1/projects/{projectSlug}/envs
```

**Body:**

```json
{
  "name": "production"
}
```

**Response:**

```json
{
  "message": "Environment Created.",
  "type": "success"
}
```

### 2.3 Update Environment

```
PATCH /v1/projects/{projectSlug}/envs/{env}
```

**Body:**

```json
{
  "name": "production"
}
```

**Response:**

```json
{
  "message": "Environment Updated.",
  "type": "success"
}
```

### 2.4 Delete Environment

```
DELETE /v1/projects/{projectSlug}/envs/{env}
```

**Response:**

```json
{
  "type": "success",
  "message": "Environment deleted."
}
```

## 3. Secrets

### 3.1 List Secret Keys

```
GET /v1/projects/{projectSlug}/envs/{env}/secrets
```

**Response:**

```json
{
  "keys": [
    "DB_USER",
    "DB_PASS",
    "API_KEY"
  ],
  "type": "success",
  "message": "Secret keys listed."
}

```

### 3.2 Add Secret

```
POST /v1/projects/{projectSlug}/envs/{env}/secrets
```

**Body:**

```json
{
  "key": "DB_PASS",
  "value": "hunter2"
}
```

**Response:**

```json
{
  "type": "success",
  "message": "Secret added."
}
```

### 3.3 View Secret Value

```
GET /v1/projects/{projectSlug}/envs/{env}/secrets/{key}
```

**Response:**

```json
{
  "key": "DB_PASS",
  "value": "hunter2",
  "type": "success",
  "message": "Fetched secret value."
}
```

### 3.4 Update Secret

```
PUT /v1/projects/{projectSlug}/envs/{env}/secrets/{key}
```

> Only `value` can be updated.  
> To change the name, create a new secret and delete old one.

**Body:**

```json
{
  "value": "hunter2"
}
```

**Response:**

```json
{
  "type": "success",
  "message": "Secret Updated."
}
```

### 3.5 Delete Secret

```
DELETE /v1/projects/{projectSlug}/envs/{env}/secrets/{key}
```

**Response:**

```json
{
  "type": "success",
  "message": "Secret deleted."
}
```

## 4. API Key

### 4.1 Create API Key**

```
POST /v1/projects/{projectSlug}/apiKeys
```

**Body:**

```json
{
  "label": "ci-deploy"
}
```

**Response:**

```json
{
  "key_plaintext": "AbCdEfGh12345",
  "type": "success",
  "message": "API key created. Save the plaintext now; it will not be shown again."
}
```

> The plaintext key is shown **only once**. After that, only its hash remains stored.

### 4.2 List API Keys**

```
GET /v1/projects/{projectSlug}/apiKeys
```

**Response:**

```json
{
  "apiKeys": [
    {
      "id": 3,
      "label": "ci-deploy"
    }
  ],
  "type": "success",
  "message": "API keys listed."
}
```

### 4.3 Revoke API Key**

```
DELETE /v1/projects/{projectSlug}/apiKeys/{keyId}
```

**Response:**

```json
{
  "type": "success",
  "message": "API key revoked."
}
```
