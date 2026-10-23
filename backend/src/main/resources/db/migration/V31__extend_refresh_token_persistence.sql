-- Extend existing active sessions to one year from migration time.
-- The application then uses a rolling one-year expiry whenever the token is refreshed.
UPDATE refresh_tokens
SET expires_at = GREATEST(expires_at, CURRENT_TIMESTAMP::timestamp + INTERVAL '365 days')
WHERE revoked = FALSE;
