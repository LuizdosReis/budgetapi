ALTER TABLE transactions
    ADD COLUMN IF NOT EXISTS direction VARCHAR(3)
        CONSTRAINT transactions_direction
            CHECK (direction IN ('IN', 'OUT'));


UPDATE transactions
SET direction = CASE c.type
                    WHEN 'INCOME' THEN 'IN'
                    WHEN 'EXPENSE' THEN 'OUT'
    END
FROM transactions t
         JOIN categories c ON c.id = t.category_id
WHERE t.id = transactions.id;

ALTER TABLE transactions
    ALTER COLUMN direction SET NOT NULL;
