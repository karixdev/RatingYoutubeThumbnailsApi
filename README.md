# YouTube Thumbnail Ranking

## Table of contents

<!-- TOC -->
* [YouTube Thumbnail Ranking](#youtube-thumbnail-ranking)
  * [1. Description](#1-description)
  * [2. Available endpoints](#2-available-endpoints)
    * [POST /api/v1/auth/register](#post-apiv1authregister)
    * [POST /api/v1/auth/sign-in](#post-apiv1authsign-in)
    * [POST /api/v1/email-verification/{token}](#post-apiv1email-verificationtoken)
    * [POST /api/v1/email-verification/resend](#post-apiv1email-verificationresend)
    * [POST /api/v1/thumbnail](#post-apiv1thumbnail)
    * [DELETE /api/v1/thumbnail/{id}](#delete-apiv1thumbnailid)
    * [POST /api/v1/game/start](#post-apiv1gamestart)
    * [POST /api/v1/game/round-result/{id}](#post-apiv1gameround-resultid)
    * [POST /api/v1/game/end/{id}](#post-apiv1gameendid)
    * [GET /api/v1/game](#get-apiv1game)
    * [GET /api/v1/rating/{youtubeVideoId}](#get-apiv1ratingyoutubevideoid)
<!-- TOC -->

## 1. Description

The project aimed to create a REST API where users can start a game where they are choosing between two thumbnails of YouTube videos. Based on the user's choices, the thumbnail receives a certain amount of rating points, which are calculated using the [ELO rating system](https://en.wikipedia.org/wiki/Elo_rating_system). When the user [starts game](#post-apiv1gamestart) two thumbnails are drawn, the user chooses the one he likes better and then sends the corresponding request to the REST API with [round result](#post-apiv1gameround-resultid), then another thumbnail is drawn, and so on.

To add a thumbnail of an YouTube video you just need to know its link, for example
- If YouTube video's link is given in the shorter format:
  - `https://youtu.be/dQw4w9WgXcQ` then `id` is equal to `dQw4w9WgXcQ`
- If YouTube video's link is given in the longer format:
  - `https://www.youtube.com/watch?v=YnopHCL1Jk8&ab_channel=TimeRecords` then `id` is equal to `YnopHCL1Jk8`

## 2. Available endpoints

### POST /api/v1/auth/register

Creates new, disabled user based on provided credentials and sends an email with email verification token.

**Auth required**: NO

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

(2)
If username or email is already taken

Code: `409`

---

### POST /api/v1/auth/sign-in

Signs in enabled user. Response includes `JWT` and user details such as: `email`, `username`, `user_role`, `is_enabled`. 

**Auth required**: NO

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

(2)
If user with provided could not found or could not be authenticated

Code: `401`

---

### POST /api/v1/email-verification/{token}

Verifies token sent to user after registration or request new email verification token. If provided token in path is
valid then enables user owning the token.

**Auth required**: NO

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

(2)
If user is already enabled or `token` has expired:

Code: `400`

---

### POST /api/v1/email-verification/resend

Resends an email with email verification token. There is a limit which states how many tokens can user request per hour,
you can specify it in `application.yaml` under `email-verification.max-number-of-mails-per-hour` (the default value is
5).

**Auth required**: NO

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

(2)
If user is already enabled or requested too many tokens in one hour:

Code: `400`

---

### POST /api/v1/thumbnail

Adds thumbnail based on provided `youtube_video_id`. When endpoint is called then app calls YouTube API to get details about video. From YouTube API response the url for thumbnail is extracted and then new thumbnail is added to database.  

**Auth required**: YES

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

(2)
If `youtube_video_id` is too short.

Code: `400`

(3)
If thumbnail with provided `youtube_video_id` already exists in database.

Code: `409`

(4)
If YouTube API is unavailable.

Code: `503`

---

### DELETE /api/v1/thumbnail/{id}

Deletes a thumbnail based on the given `id`. Authors can delete their thumbnails, the only exception is users with the role `ADMIN` - they can delete everyone's thumbnail.

**Auth required**: YES

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

(2)
If user is not an author and is hasn't got `ADMIN` role.

Code: `403`

---

### POST /api/v1/game/start

Starts a game in which the user chooses between two thumbnails. If the game is left without any action it deactivates itself after time specified in `application.yaml` under variable `game.duration`

**Auth required**: YES

**Success response**:

Code: `200`

```json
{
  "id": 1,
  "thumbnail1": {
    "id": 1,
    "url": "address-to-url-1"
  },
  "thumbnail2": {
    "id": 2,
    "url": "address-to-url-2"
  }
}
```

**Error response**:

(1)
If user has started a game, and it hasn't expired or ended

Code: `400`

(2)
If there are not enough thumbnails in database to start a game.

Code: `500`

---

### POST /api/v1/game/round-result/{id}

Updates rankings of thumbnails taking part in the game - it is done using [Elo rating system](https://en.wikipedia.org/wiki/Elo_rating_system). Winner stays in game, and the new opponent is chosen.

**Auth required**: YES

**Path variables**:

| Name | Type | Required |
|------|------|----------|
| `id` | Long | True     |

**Request body**:

| Name        | Type | Constraints       |
|-------------|------|-------------------|
| `winner_id` | Long | Must not be null. |

**Success response**:

Code: `200`

(1)
If `winner_id` is `thumbnail1` id

```json
{
  "id": 1,
  "thumbnail1": {
    "id": 1,
    "url": "address-to-url-1"
  },
  "thumbnail2": {
    "id": 3,
    "url": "address-to-url-3"
  }
}
```

(2)
If `winner_id` is `thumbnail2` id

```json
{
  "id": 1,
  "thumbnail1": {
    "id": 3,
    "url": "address-to-url-3"
  },
  "thumbnail2": {
    "id": 2,
    "url": "address-to-url-2"
  }
}
```

**Error response**:

(1)
If game with provided `id` was not found.

Code: `404`

(2)
If user is not owner of the game.

Code: `403`

(3)
If game has expired or has been ended.

Code: `409`

(4)
If `winer_id` isn't match thumbnail1 or thumbnail2 id.

Code: `400`

(5)
If there are not enough thumbnails in database to pick a new opponent.

Code: `500`

---

### POST /api/v1/game/end/{id}

Ends game with provided `id`.

**Auth required**: YES

**Path variables**:

| Name | Type | Required |
|------|------|----------|
| `id` | Long | True     |

**Success response**:

Code: `200`

```json
{
  "message": "success"
}
```

**Error response**:

(1)
If game with provided `id` was not found.

Code: `404`

(2)
If user is not owner of the game.

Code: `403`

(3)
If game has been already ended.

Code: `400`

---

### GET /api/v1/game

Gets user's actual active, not expired game.

**Auth required**: YES

**Success response**:

Code: `200`

```json
{
  "id": 1,
  "thumbnail1": {
    "id": 1,
    "url": "address-to-url-1"
  },
  "thumbnail2": {
    "id": 2,
    "url": "address-to-url-2"
  }
}
```

**Error response**:

(1)
If user doesn't have active, not expired game.

Code: `404`

---

### GET /api/v1/rating/{youtubeVideoId}

Calculates average rating points for thumbnail with provided `yotoubeVideoId`, and retrieves rating points for authenticated user (if he is not authenticated then `null` is returned)

**Auth required**: NO

**Path variables**:

| Name             | Type   | Required |
|------------------|--------|----------|
| `youtubeVideoId` | String | True     |

**Success response**:

Code: `200`

(1)
If user is authenticated.

```json
{
  "global_rating_points": 1337.10,
  "user_rating_points": 2731.15
}
```

(1)
If user is not authenticated.

```json
{
  "global_rating_points": 1337.10,
  "user_rating_points": null
}
```

**Error response**:

(1)
If thumbnail with provided `youtubeVideoId` was not found.

Code: `404`

---