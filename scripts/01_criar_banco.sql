CREATE DATABASE agencia_viagens
    WITH ENCODING 'UTF8'
    TEMPLATE template0;

CREATE USER agencia_app WITH PASSWORD 'agencia123';

GRANT ALL PRIVILEGES ON DATABASE agencia_viagens TO agencia_app;
