create table lottery
(
	id serial not null
		constraint lottery_pk
			primary key,
	title varchar(400) not null,
	participant_limit integer not null,
	start_date date not null,
	end_date date
);

alter table lottery owner to "lotteryDBUserDev";

create unique index lottery_id_uindex
	on lottery (id);

create table participant
(
	id serial not null
		constraint participant_pk
			primary key,
	email varchar(99) not null,
	age integer not null,
	code varchar(16) not null,
	lottery_id serial not null
		constraint participant_lottery_id_fk
			references lottery
);

alter table participant owner to "lotteryDBUserDev";

create unique index participant_id_uindex
	on participant (id);

