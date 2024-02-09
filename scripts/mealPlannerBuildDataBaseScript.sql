CREATE DATABASE meals_db;

\c meals_db

create table meals (
meal_id SERIAL PRIMARY KEY,
meal_name VARCHAR(100) UNIQUE NOT NULL,
category VARCHAR(30) NOT NULL
);

create table ingredients (
ingredient_id SERIAL PRIMARY KEY,
ingredient_name VARCHAR(50) UNIQUE NOT NULL,
amount INT
);

create table meal_to_ingredient_relations (
meal_id INT NOT NULL,
ingredient_id INT NOT NULL,
PRIMARY KEY(meal_id,ingredient_id),
FOREIGN KEY(meal_id) REFERENCES meals(meal_id),
FOREIGN KEY(ingredient_id) REFERENCES ingredients(ingredient_id)
);