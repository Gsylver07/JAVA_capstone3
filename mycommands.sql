create database if not exists poised_pms;
use poised_pms;

drop table if exists project;
create table project (
Project_Number int,
Project_Name varchar(50),
Building_Type varchar(50),
Physical_Address varchar(50),
ERF_Number varchar(50),
Project_Initial_Date varchar(50),
Deadline_Date  varchar(50),
Total_Fee float,
Total_Amount_Paid float,
Amount_Outstanding float,
Project_Status varchar(50),
Deadline_Status  varchar(50)

);

drop table if exists client;
create table client (
Name varchar(50),
Email_Address varchar(50),
Physical_Address varchar(50),
Tel varchar(50),
proNum int);

drop table if exists contractor;
create table contractor (
Name varchar(50),
Email_Address varchar(50),
Physical_Address varchar(50),
Telephone_Number varchar(50)

);

drop table if exists architect;
create table architect (
Name varchar(50),
Physical_Address varchar(50),
Email_Address varchar(50),
Telephone_Number varchar(50)
);


