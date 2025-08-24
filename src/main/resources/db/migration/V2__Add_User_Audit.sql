ALTER TABLE orders
    ADD COLUMN created_by varchar(255);
ALTER TABLE orders
    ADD COLUMN updated_by varchar(255);