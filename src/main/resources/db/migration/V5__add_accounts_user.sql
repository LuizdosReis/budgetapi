ALTER TABLE accounts ADD COLUMN IF NOT EXISTS user_id uuid REFERENCES users (id);
DELETE FROM accounts WHERE user_id is null;
ALTER TABLE accounts ALTER COLUMN user_id set not null;