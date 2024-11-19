CREATE TABLE tb_pessoas (
    id uuid PRIMARY KEY NOT NULL,
    apelido varchar(32) NOT NULL UNIQUE,
    nome varchar(100) NOT NULL,
    nascimento varchar(10) NOT NULL,
    stack varchar(32) ARRAY,
    busca text
)
