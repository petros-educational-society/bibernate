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

create table if not exists orders (
   id bigint,
   name varchar(50) not null,
   price decimal not null,
   address_id bigint,
   CONSTRAINT PK_orders PRIMARY KEY (id),
   CONSTRAINT FK_orders_addresses FOREIGN KEY (address_id) REFERENCES addresses
);

create table cards (
    id bigint primary key,
    number varchar(50) not null,
    type varchar(50) not null,
    user_id bigint unique references users
);

create table buyers (
    id bigint,
    name varchar(50) not null,
    phone varchar(50) not null,
    CONSTRAINT PK_buyers PRIMARY KEY (id)
);

create table buyers_users (
    buyer_id bigint REFERENCES buyers,
    user_id bigint REFERENCES users,
    CONSTRAINT PK_buyers_users PRIMARY KEY (buyer_id, user_id),
    CONSTRAINT FK_buyers_users_buyers FOREIGN KEY (buyer_id) REFERENCES buyers,
    CONSTRAINT FK_buyers_users_users FOREIGN KEY (user_id) REFERENCES users
);

INSERT INTO users(id, name, email) VALUES ('1', 'Ken', 'ken@gmail.com');
INSERT INTO users(id, name, email) VALUES ('2', 'John', 'john@gmail.com');
INSERT INTO users(id, name, email) VALUES ('3', 'Helga', 'helga@gmail.com');
INSERT INTO addresses(id, city, street) VALUES ('10', 'Riga', 'Rebenstrasse');
INSERT INTO addresses(id, city, street) VALUES ('11', 'Odessa', 'R.Luksemburg');
INSERT INTO addresses(id, city, street) VALUES ('12', 'Lviv', 'Centralnaya');
INSERT INTO orders(id, name, price, address_id) VALUES ('1', 'Pencil', 1.01, '10');
INSERT INTO orders(id, name, price, address_id) VALUES ('2', 'Pen', 1.02, '10');
INSERT INTO orders(id, name, price, address_id) VALUES ('3', 'Book', 1.03, '10');
INSERT INTO cards(id, number, type, user_id) VALUES ('1', '2386', 'visa', '1');
INSERT INTO buyers(id, name, phone) VALUES ('1', 'Alex', '380665624786');
INSERT INTO buyers_users(buyer_id, user_id) VALUES ('1', '1');
INSERT INTO buyers_users(buyer_id, user_id) VALUES ('1', '2');