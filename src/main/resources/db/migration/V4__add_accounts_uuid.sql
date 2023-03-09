ALTER TABLE accounts ADD COLUMN IF NOT EXISTS uuidid uuid;

UPDATE accounts SET uuidid = gen_random_uuid() WHERE uuidid is null;

ALTER TABLE accounts DROP COLUMN IF EXISTS id;
ALTER TABLE accounts RENAME COLUMN uuidid TO id;
ALTER TABLE accounts ADD PRIMARY KEY (id);