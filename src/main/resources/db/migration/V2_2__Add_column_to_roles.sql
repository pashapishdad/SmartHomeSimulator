ALTER TABLE roles ADD parentUser varchar(255) FIRST;
UPDATE roles SET parentUser = 'test' WHERE username = 'test';