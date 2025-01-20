--DROP USER quote_admin;
CREATE USER quote_admin WITH PASSWORD 'postgres';

--DROP DATABASE quote_service;
CREATE DATABASE quote_service WITH
    OWNER quote_admin
    ENCODING 'UTF8'
    TEMPLATE template0;
