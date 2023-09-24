-- liquibase formatted sql

CREATE TABLE "user"
(
    id           uuid primary key not null,
    age          integer,
    name         VARCHAR(256),
    phone_number VARCHAR(256)
);

CREATE TABLE cart
(
    id      uuid primary key not null,
    user_id uuid,
    foreign key (user_id) references "user" (id)
        match simple on update no action on delete no action
);

CREATE TABLE product
(
    id       uuid primary key not null,
    name     VARCHAR(256) not null,
    price    numeric(38, 2) not null,
    category VARCHAR(256) not null
);

CREATE TABLE cart_product
(
    cart_id    uuid not null,
    product_id uuid not null,
    foreign key (product_id) references product (id)
        match simple on update no action on delete no action,
    foreign key (cart_id) references cart (id)
        match simple on update no action on delete no action
);

CREATE TABLE category
(
    id   uuid primary key not null,
    name VARCHAR(256)
);

CREATE TABLE "order"
(
    id          uuid primary key not null,
    cart_id     VARCHAR(256),
    discounts   numeric(38, 2),
    order_total numeric(38, 2),
    products    integer          not null,
    shipping    numeric(38, 2)
);
