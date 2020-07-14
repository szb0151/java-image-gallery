
create table users (username varchar(200) primary key not null, password varchar(200), full_name varchar(200));
create table images (username varchar(200), imageid varchar(200) not null, foreign key (username) references users(username));
insert into users values ('fred', 'bedrock', 'Fred Flintstone');
insert into users values ('barney', 'blah', 'Barney Rubble');
insert into users values ('dongji', 'cpsc4973', 'Dongji Feng TA');
