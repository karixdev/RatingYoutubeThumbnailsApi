# YouTube Thumbnail Ranking

## 1. Available endpoints

### POST /api/v1/auth/register

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

### POST /api/v1/auth/sign-in

Signs in enabled user. Response includes `JWT` and user details such as: `email`, `username`, `user_role`, `is_enabled`. 

**Auth required**: NO

**Permissions required**: NONE

**Request body**:

| Name       | Type   | Constraints                                           |
|------------|--------|-------------------------------------------------------|
| `email`    | String | Must follow the email format.                         |
| `password` | String | Length must be at least 8 and at most 255 characters. |

**Success response**:

Code: `200`

```json
{
  "access_token": "...",
  "user": {
    "email": "...",
    "username": "...",
    "user_role": "ROLE_USER or ROLE_ADMIN",
    "is_enabled": "true or false"
  }
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
  "path": "/api/v1/auth/sign-in"
}
```

(2)
If user with provided could not found or could not be authenticated

Code: `401`

```json
{
  "timestamp": "timestamp when error occurred",
  "status": 401,
  "error": "Unauthorized",
  "path": "/api/v1/auth/sing-in"
}
```

---

### POST /api/v1/email-verification/{token}

Verifies token sent to user after registration or request new email verification token. If provided token in path is
valid then enables user owning the token.

**Auth required**: NO

**Permissions required**: NONE

**Path variables**:

| Name    | Type   | Required |
|---------|--------|----------|
| `token` | String | True     |

**Success response**:

Code: `200`

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

---

### POST /api/v1/email-verification/resend

Resends an email with email verification token. There is a limit which states how many tokens can user request per hour,
you can specify it in `application.yaml` under `email-verification.max-number-of-mails-per-hour` (the default value is
5).

**Auth required**: NO

**Permissions required**: NONE

**Request body**:

| Name    | Type   | Constraints                   |
|---------|--------|-------------------------------|
| `email` | String | Must follow the email format. |

**Success response**:

Code: `200`

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
  "path": "/api/v1/email-verification/resend"
}
```

### POST /api/v1/thumbnail

Adds thumbnail based on provided `youtube_video_id`. When endpoint is called then app calls YouTube API to get details about video. From YouTube API response the url for thumbnail is extracted and then new thumbnail is added to database.  

**Auth required**: YES

**Permissions required**: NONE

**Request body**:

| Name               | Type   | Constraints                           |
|--------------------|--------|---------------------------------------|
| `youtube_video_id` | String | Length must be at least 5 characters. |

**Success response**:

Code: `201`

```json
{
  "id": 1,
  "youtube_video_id": "dQw4w9WgXcQ",
  "url": "https://i.ytimg.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
  "added_by": {
    "username": "user123"
  }
}
```

**Error response**:

(1)
If YouTube API responses with empty `items` list.

Code: `404`

```json
{
  "timestamp": "timestamp when error occurred",
  "status": 404,
  "error": "Not Found",
  "path": "/api/v1/thumbnail"
}
```

(2)
If `youtube_video_id` is too short.

Code: `400`

```json
{
  "timestamp": "timestamp when error occurred",
  "status": 400,
  "error": "Bad request",
  "path": "/api/v1/thumbnail"
}
```

(3)
If thumbnail with provided `youtube_video_id` already exists in database.

Code: `409`

```json
{
  "timestamp": "timestamp when error occurred",
  "status": 409,
  "error": "Bad request",
  "path": "/api/v1/thumbnail"
}
```

(4)
If YouTube API is unavailable.

Code: `503`

```json
{
  "timestamp": "timestamp when error occurred",
  "status": 503,
  "error": "Bad request",
  "path": "/api/v1/thumbnail"
}
```

### DELETE /api/v1/thumbnail/{id}

Deletes a thumbnail based on the given `id`. Authors can delete their thumbnails, the only exception is users with the role `ADMIN` - they can delete everyone's thumbnail.

**Auth required**: YES

**Permissions required**: NONE

**Path variables**:

| Name | Type | Constraints                           |
|------|------|---------------------------------------|
| `id` | Long | Length must be at least 5 characters. |

**Success response**:

Code: `200`

```json
{
  "message": "success"
}
```

**Error response**:

(1)
If thumbnail with provided `id` was not found.

Code: `404`

```json
{
  "timestamp": "timestamp when error occurred",
  "status": 404,
  "error": "Not Found",
  "path": "/api/v1/thumbnail/{id}"
}
```

(2)
If user is not an author and is hasn't got `ADMIN` role.

Code: `403`

```json
{
  "timestamp": "timestamp when error occurred",
  "status": 403,
  "error": "Not Found",
  "path": "/api/v1/thumbnail/{id}"
}
```