# Youtube Thumbnail Ranking

## 1. Available endpoints

### POST /api/v1/auth/register/

Creates new, disabled user based on provided credentials and sends an email with email verification token.

**Auth required**: NO

**Permissions required**: NONE

**Request body**:

| Name       | Type   | Constraints                                           |
|------------|--------|-------------------------------------------------------|
| `email`    | String | Must follow the email format.                         |
| `username` | String | Length must be at least 8 and at most 255 characters. |
| `password` | String | Length must be at least 8 and at most 255 characters. |


**Success response**:

Code: `201`
```json
{
    "message": "success"
}
```

**Error response**:

(1)
If provided request body is invalid

Code: `400`
```json
{
    "timestamp": "timestamp when error occurred",
    "status": 400,
    "error": "Bad Request",
    "path": "/api/v1/auth/register"
}
```

(2)
If username or email is already taken

Code: `409`
```json
{
    "timestamp": "timestamp when error occurred",
    "status": 409,
    "error": "Conflict",
    "path": "/api/v1/auth/register"
}
```

---

### POST /api/v1/email-verification/{token}

Verifies token sent to user after registration or request new email verification token. If provided token in path is valid then enables user owning the token.

**Auth required**: NO

**Permissions required**: NONE

**Path variables**:

| Name    | Type   | Required |
|---------|--------|----------|
| `token` | String | True     |

**Success response**:

Code: `201`
```json
{
    "message": "success"
}
```

**Error response**:

(1) 
If `token` was not found

Code: `404`
```json
{
    "timestamp": "timestamp when error occurred",
    "status": 404,
    "error": "Not Found",
    "path": "/api/v1/email-verification/{token}"
}
```

(2)
If user is already enabled or `token` has expired:
```json
{
    "timestamp": "timestamp when error occurred",
    "status": 400,
    "error": "Bad request",
    "path": "/api/v1/email-verification/{token}"
}
```
### POST /api/v1/email-verification/resend

Resends an email with email verification token. There is a limit which states how many tokens can user request per hour, you can specify it in `application.yaml` under `email-verification.max-number-of-mails-per-hour` (the default value is 5). 

**Auth required**: NO

**Permissions required**: NONE

**Request body**:

| Name    | Type   | Constraints                   |
|---------|--------|-------------------------------|
| `email` | String | Must follow the email format. |

**Success response**:

Code: `201`
```json
{
    "message": "success"
}
```

**Error response**:

(1)
If user with provided `email` was not found

Code: `404`
```json
{
    "timestamp": "timestamp when error occurred",
    "status": 404,
    "error": "Not Found",
    "path": "/api/v1/email-verification/resend"
}
```

(2)
If user is already enabled or requested too many tokens in one hour:
```json
{
    "timestamp": "timestamp when error occurred",
    "status": 400,
    "error": "Bad request",
    "path": "/api/v1/email-verification/{token}"
}
```
