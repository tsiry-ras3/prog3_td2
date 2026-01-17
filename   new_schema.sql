\c
mini_dish_db;

CREATE TYPE ingredient_category_enum AS ENUM (
  'VEGETABLE',
  'ANIMAL',
  'MARINE',
  'DAIRY',
  'OTHER'
);



CREATE TYPE dish_type_enum AS ENUM (
  'START',
  'MAIN',
  'DESSERT'
);

CREATE TYPE unit_type_enum AS ENUM (
    'PCS',
    'KG',
    'L'
);

Create table Dish
(
    id        serial primary key,
    name      varchar(255),
    dish_type dish_type_enum,
    price     numeric(10, 2)
);
Create table Ingredient
(
    id       serial primary key,
    name     varchar(255),
    price    numeric(10, 2),
    category ingredient_category_enum
);
CREATE TABLE dish_ingredient
(
    id                serial primary key,
    id_dish           int,
    id_ingredient     int,
    quantity_required numeric(10, 2) not null,
    unit              unit_type_enum,

    CONSTRAINT fk_dish
        FOREIGN KEY (id_dish)
            REFERENCES dish (id),

    CONSTRAINT fk_ingredient
        FOREIGN KEY (id_ingredient)
            REFERENCES ingredient (id),

    CONSTRAINT unique_dish_ingredient
        UNIQUE (id_dish, id_ingredient)
);