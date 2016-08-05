CREATE DATABASE IF NOT EXISTS Fp_Foodx;
USE Fp_Foodx;

create table ZipcodeLatitude (
    zip varchar(5) primary key,
    city varchar(64),
    state char(2),
    longitude decimal(14, 10),
    lattitude decimal(14, 10),
    timezone int
);
