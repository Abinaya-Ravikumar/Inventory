create table inventory(
  itemId int auto_increment primary key,
  itemName varchar(100) not null,
  category varchar(100) not null,
  quantity int not null,
  price decimal(10,2) not null
);
  
