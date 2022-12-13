# Youtube Thumbnail Ranking

## 1. Available endpoints

### POST /api/v1/auth/register/

Creates new, disabled user based on provided credentials and sends an email with email verification token.

**Auth required**: NO

**Permissions required**: NONE

**Request body**:

| Name     | Type   | Constraints                                           |
|----------|--------|-------------------------------------------------------|
| email    | String | Must follow the email format.                         |
| username | String | Length must be at least 8 and at most 255 characters. |
| password | String | Length must be at least 8 and at most 255 characters. |


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
    "error": "Conflict",
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