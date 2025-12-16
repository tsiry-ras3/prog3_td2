\c mini_dish_db;

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

Create table Dish (
    id int primary key,
    name varchar(255),
    dish_type dish_type_enum
);
Create table Ingredient (
    id int primary key,
    name varchar(255),
    price numeric(10,2),
    category ingredient_category_enum,
    id_dish int not null,
    constraint fk_dish foreign key (id_dish) references dish (id)
);