USE Fp_Foodx;
Create table login(
	userId int(11) primary key auto_increment,
    username varchar(30) not null,
    password varchar(100) not null
);