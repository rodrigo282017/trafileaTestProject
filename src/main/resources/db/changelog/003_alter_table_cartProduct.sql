-- This alter is to add the quantity to the relation between the tables cart and product.

alter table cart_product
    add quantity integer;