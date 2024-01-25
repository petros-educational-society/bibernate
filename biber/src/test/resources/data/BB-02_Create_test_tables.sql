-- liquibase formatted sql
-- changeset OleksiiSkachkov:BB-02_Create_test_tables

create table users (
    id bigint primary key,
    name varchar(50),
    email varchar(50)
);

create table addresses (
    id bigint primary key,
    city varchar(50),
    street varchar(50)
);

INSERT INTO users(id, name, email) VALUES ('1', 'Ken', 'ken@gmail.com');
INSERT INTO users(id, name, email) VALUES ('2', 'John', 'john@gmail.com');
INSERT INTO users(id, name, email) VALUES ('3', 'Helga', 'helga@gmail.com');
INSERT INTO addresses(id, city, street) VALUES ('10', 'Riga', 'Rebenstrasse');
INSERT INTO addresses(id, city, street) VALUES ('11', 'Odessa', 'R.Luksemburg');
INSERT INTO addresses(id, city, street) VALUES ('12', 'Lviv', 'Centralnaya');