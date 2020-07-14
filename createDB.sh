#!/usr/bin/bash

psql -h $PG_HOST -U $IG_USER
$IG_PASSWD
create table users (username varchar(200) primary key not null, password varchar(200), full_name varchar(200));
create admin dongji login password 'cpsc4973';
create table images (username varchar(200), imageid varchar(200) not null, foreign key (username) references users(username));
\q
