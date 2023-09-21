-- liquibase formatted sql

CREATE TABLE "user"
(
    id           uuid primary key not null,
    age          VARCHAR(256),
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
    id    uuid primary key not null,
    name  VARCHAR(256),
    price numeric(38, 2)
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

create table product_category
(
    product_id  uuid not null,
    category    VARCHAR(256),
    category_id uuid not null,
    primary key (product_id, category_id),
    foreign key (product_id) references product (id)
        match simple on update no action on delete no action,
    foreign key (category_id) references category (id)
        match simple on update no action on delete no action
);
