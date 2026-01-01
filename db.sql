Create database mini_dish_db;

Create user mini_dish_db_manager with password '123456';


grant connect on database mini_dish_db to mini_dish_db_manager;
grant connect on database mini_dish_db to mini_dish_db_manager;

\c mini_dish_db;

grant usage on schema public to mini_dish_db_manager;
grant Create on schema public to mini_dish_db_manager;

grant select, update, delete, insert on all tables in schema public to mini_dish_db_manager;
grant usage, select on all sequences in schema public to mini_dish_db_manager;