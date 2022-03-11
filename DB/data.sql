INSERT INTO USER(city, number, street, zipcode, firstname, password, surname, username)
values ('Bucuresti', 2, 'Lalelelor', '600135', 'AdminFirst', 'password', 'lastName','adminUsername'),
       ('Timisoara', 12, 'Libertatii', '600875', 'ClientFirst', 'passwordTwo', 'ClientlastName','clientUsername'),
       ('Bacau', 122, 'Libertatii', '600848', 'ExpeditorFirst', 'passwordThree', 'ExpeditorlastName','ExpeditorUsername');
Insert into user_roles values (1,'ADMIN'),(1,'EXPEDITOR');
Insert into user_roles values (2,'CLIENT');
Insert into user_roles values (3,'EXPEDITOR')