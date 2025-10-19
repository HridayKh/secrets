# 🗝️ **Secrets Service API Design (v1)**

* **Secrets App Base URL:** `https://api.hridaykh.in/secrets`



# Backend / Other Projects API

* These endpoints are meant to be used by your other applications (e.g., blog backend, CI, etc.).
* They **only fetch** secrets and authenticate via an API key in the header.

## Authentication

### **Header**:

```http
Authorization: ApiKey <api_key>
```

## **Endpoints**

### **1. Get all secrets for an environment**
```
GET /v1/secrets/{projectSlug}/{env}
```
**Description:**
Fetches all decrypted secrets for a project environment.

**Response:**
```json
{
	"project": "my-blog",
	"env": "production",
	"secrets": {
		"DB_USER": "root",
		"DB_PASS": "hunter2",
		"API_KEY": "abcd1234"
	},
	"status": "ok",
	"msg": "Fetched all secrets for environment.",
	"timestamp": "2025-10-19T12:00:00Z"
}
```

### **2. Get a single secret**

```
GET /v1/secrets/{projectSlug}/{env}/{secretKey}
```
**Description:**
Fetches one specific secret by key.
**Response:**
```json
{
	"project": "my-blog",
	"env": "production",
	"key": "DB_PASS",
	"value": "hunter2",
	"status": "ok",
	"msg": "Fetched secret value.",
	"timestamp": "2025-10-19T12:00:00Z"
}
```

# **Admin / Frontend API**

* These endpoints are used by the **management UI** (which authenticates using your custom auth app’s cookie).
* They support creating, editing, and deleting projects, environments, secrets, and API keys.

## Authentication
> Note: Refer to the [Auth App Github](https://github.com/HridayKh/auth) for more info about my auth system.

* Frontend has an auth cookie from your custom auth app, if not, redirect to auth app for login.
* Each admin endpoint checks for this auth cookie.
* Admin endpoints performs a server-to-server request to auth app to get the `metadata`/`permissons` of the acc in the cookie.
* check if the `metadata`/`permissons` contain say that they an admin/main acc or are verified to use this secrets manager 

> Note: The `metadata`/`permissons` of a account in the auth app are just custom json to store extra values about an account and managed manually or by other apps such as this (secrets manager) one.  



## 1. Projects

### 1.1 List All Projects

```
GET /v1/admin/projects
```

**Response:**
```json
{
	"projects": [
		{
			"slug": "my-blog",
			"name": "My Blog Backend",
			"timestamp": "2025-10-19T12:00:00Z"
		},
		{
			"slug": "auth-system",
			"name": "Auth Service",
			"timestamp": "2025-09-10T10:00:00Z"
		}
	],
	"status": "ok",
	"msg": "Projects listed."
}
```

### 1.2 Create Project

```
POST /v1/admin/projects
```

**Body:**

```json
{
	"slug": "my-blog",
	"name": "My Blog Backend"
}
```

**Response:**

```json
{
	"id": 1,
	"slug": "my-blog",
	"name": "My Blog Backend",
	"status": "ok",
	"msg": "Project created.",
	"timestamp": "2025-10-19T12:00:00Z"
}
```

### 1.3 Update Project

```
PATCH /v1/admin/projects/{projectSlug}
```

**Body:**
> Either `name` or `slug` or both `name` and `slug` can be updated.
```json
{
	"slug": "my-blog",
	"name": "My Blog Backend"
}
```

**Response:**

```json
{
	"id": 1,
	"slug": "my-blog",
	"name": "My Blog Backend",
	"status": "ok",
	"msg": "Project updated.",
	"created_at": "2025-10-19T12:00:00Z"
}
```

### 1.4 Project SUmmary
```
GET /v1/admin/projects/{projectSlug}/summary
```

**Response:**

```json
{
  "id": 1,
  "slug": "my-blog",
  "name": "My Blog Backend",
  "environments": [
    {
      "name": "production",
      "secrets": ["DB_URL", "DB_PASSWORD"]
    },
    {
      "name": "staging",
      "secrets": ["TEST_DB_URL", "TEST_DB_PASSWORD"]
    }
  ],
  "status": "ok",
  "msg": "Project summary received.",
  "created_at": "2025-10-19T12:00:00Z"
}
```

### 1.5 Delete Project
> projects cant be deleted for now to avoid accidental data loss.

## 2. Environment

### 2.1 List Environments

```
GET /v1/admin/projects/{projectSlug}/envs
```

**Response:**
```json
{
	"envs": [
		{
			"id": 1,
			"name": "production",
			"timestamp": "2025-10-19T12:01:00Z"
		},
		{
			"id": 2,
			"name": "staging",
			"timestamp": "2025-10-19T12:01:00Z"
		}
	],
	"status": "ok",
	"msg": "Environments listed."
}
```

### 2.2 Create Environment

```
POST /v1/admin/projects/{projectSlug}/envs
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
	"id": 1,
	"name": "production",
	"status": "ok",
	"msg": "Environment created.",
	"timestamp": "2025-10-19T12:01:00Z"
}
```

### 2.3 Update Environment

```
PATCH /v1/admin/projects/{projectSlug}/envs/{env}
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
	"result": "updated",
	"status": "ok",
	"msg": "Environment updated.",
	"timestamp": "2025-10-19T12:01:00Z"
}
```

## 3. Secret

### 3.1 Add Secret

```
POST /v1/admin/projects/{projectSlug}/envs/{env}/secrets
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
	"id": 1,
	"key": "DB_PASS",
	"status": "ok",
	"msg": "Secret added.",
	"timestamp": "2025-10-19T12:01:00Z"
}
```

### 3.2 Update Secret

```
PUT /v1/admin/projects/{projectSlug}/envs/{env}/secrets/{key}
```

**Body:**

```json
{
	"value": "hunter2"
}
```

**Response:**

```json
{
	"result": "updated",
	"status": "ok",
	"msg": "Secret updated.",
	"timestamp": "2025-10-19T12:01:00Z"
}
```

### 3.3 List Secret Keys (no values)

```
GET /v1/admin/projects/{projectSlug}/envs/{env}/secrets
```

**Response:**

```json
{
	"keys": [
		"DB_USER",
		"DB_PASS",
		"API_KEY"
	],
	"status": "ok",
	"msg": "Secret keys listed.",
	"timestamp": "2025-10-19T12:01:00Z"
}

```

### 3.4 View Secret Value

```
GET /v1/admin/projects/{projectSlug}/envs/{env}/secrets/{key}
```

**Response:**

```json
{
	"key": "DB_PASS",
	"value": "hunter2",
	"status": "ok",
	"msg": "Fetched secret value.",
	"timestamp": "2025-10-19T12:01:00Z"
}
```

### 3.5 Delete Secret

```
DELETE /v1/admin/projects/{projectSlug}/envs/{env}/secrets/{key}
```

**Response:**

```json
{
	"result": "deleted",
	"status": "ok",
	"msg": "Secret deleted.",
	"timestamp": "2025-10-19T12:01:00Z"
}
```

## 4. **API Key**

### 4.1 Create API Key**

```
POST /v1/admin/projects/{projectSlug}/apikeys
```

**Body:**

```json
{
	"name": "ci-deploy"
}
```

**Response:**

```json
{
	"id": 3,
	"name": "ci-deploy",
	"key_plaintext": "AbCdEfGh12345",
	"status": "ok",
	"msg": "API key created. Save the plaintext now; it will not be shown again.",
	"timestamp": "2025-10-19T12:05:00Z"
}
```

> The plaintext key is shown **only once**. After that, only its hash remains stored.



### 4.2 List API Keys**

```
GET /v1/admin/projects/{projectSlug}/apikeys
```

**Response:**
```json
{
	"apikeys": [
		{
			"id": 3,
			"name": "ci-deploy",
			"revoked": false,
			"timestamp": "2025-10-19T12:05:00Z"
		}
	],
	"status": "ok",
	"msg": "API keys listed."
}
```

### 4.3 Revoke API Key**

```
DELETE /v1/admin/projects/{projectSlug}/apikeys/{keyId}
```

**Response:**

```json
{
	"result": "revoked",
	"status": "ok",
	"msg": "API key revoked.",
	"timestamp": "2025-10-19T12:05:00Z"
}
```


# Health Check

* Can be accessed by anyone without authentication.

```
GET /v1/health
```
**Response:**

```json
{
	"status": "ok",
	"msg": "Secrets app is up and running.",
	"timestamp": "2025-10-19T12:00:00Z"
}
```
