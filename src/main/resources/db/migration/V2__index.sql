CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS tb_pessoas_busca_idx ON tb_pessoas USING GIST (busca gist_trgm_ops(siglen=64));
