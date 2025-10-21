ALTER TABLE membro
    ADD COLUMN codigo_membro INT; -- Ou BIGINT se o número for muito grande

-- Se o código do membro DEVE ser único e obrigatório (recomendado):
ALTER TABLE membro
    ADD CONSTRAINT uk_membro_codigo UNIQUE (codigo_membro);

ALTER TABLE membro
    ALTER COLUMN codigo_membro SET NOT NULL;