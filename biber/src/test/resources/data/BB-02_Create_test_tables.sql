-- liquibase formatted sql
-- changeset OleksiiSkachkov:BB-02_Create_test_tables

create table if not exists users (
    id bigint primary key,
    name varchar(50),
    email varchar(50)
);

create table if not exists addresses (
    id bigint primary key,
    city varchar(50),
    street varchar(50)
);

create table if not exists orders (
   id bigint,
   name varchar(50) not null,
   price decimal not null,
   address_id bigint,
   CONSTRAINT PK_orders PRIMARY KEY (id),
   CONSTRAINT FK_orders_addresses FOREIGN KEY (address_id) REFERENCES addresses
);

create table if not exists cards (
    id bigint primary key,
    number varchar(50) not null,
    type varchar(50) not null,
    user_id bigint unique references users
);

create table if not exists buyers (
    id bigint,
    name varchar(50) not null,
    phone varchar(50) not null,
    CONSTRAINT PK_buyers PRIMARY KEY (id)
);

create table if not exists buyers_users (
    buyer_id bigint REFERENCES buyers,
    user_id bigint REFERENCES users,
    CONSTRAINT PK_buyers_users PRIMARY KEY (buyer_id, user_id),
    CONSTRAINT FK_buyers_users_buyers FOREIGN KEY (buyer_id) REFERENCES buyers,
    CONSTRAINT FK_buyers_users_users FOREIGN KEY (user_id) REFERENCES users
);

INSERT INTO users (id, name, email)
SELECT '1', 'Ken', 'ken@gmail.com'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = '1');

INSERT INTO users (id, name, email)
SELECT '2', 'John', 'john@gmail.com'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = '2');

INSERT INTO users (id, name, email)
SELECT '3', 'Helga', 'helga@gmail.com'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = '3');

INSERT INTO addresses (id, city, street)
SELECT '10', 'Riga', 'Rebenstrasse'
WHERE NOT EXISTS (SELECT 1 FROM addresses WHERE id = '10');

INSERT INTO addresses (id, city, street)
SELECT '11', 'Odessa', 'R.Luksemburg'
WHERE NOT EXISTS (SELECT 1 FROM addresses WHERE id = '11');

INSERT INTO addresses (id, city, street)
SELECT '12', 'Lviv', 'Centralnaya'
WHERE NOT EXISTS (SELECT 1 FROM addresses WHERE id = '12');

INSERT INTO orders (id, name, price, address_id)
SELECT '1', 'Pencil', 1.01, '10'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE id = '1');

INSERT INTO orders (id, name, price, address_id)
SELECT '2', 'Pen', 1.02, '10'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE id = '2');

INSERT INTO orders (id, name, price, address_id)
SELECT '3', 'Book', 1.03, '10'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE id = '3');

INSERT INTO cards (id, number, type, user_id)
SELECT '1', '2386', 'visa', '1'
WHERE NOT EXISTS (SELECT 1 FROM cards WHERE id = '1');

INSERT INTO buyers (id, name, phone)
SELECT '1', 'Alex', '380665624786'
WHERE NOT EXISTS (SELECT 1 FROM buyers WHERE id = '1');

INSERT INTO buyers_users (buyer_id, user_id)
SELECT '1', '1'
WHERE NOT EXISTS (SELECT 1 FROM buyers_users WHERE buyer_id = '1' AND user_id = '1');

INSERT INTO buyers_users (buyer_id, user_id)
SELECT '1', '2'
WHERE NOT EXISTS (SELECT 1 FROM buyers_users WHERE buyer_id = '1' AND user_id = '2');