drop schema if exists public CASCADE ;
drop table if exists participant;
drop table if exists lottery;
create schema public;

alter schema public owner to "lotteryDBUserDev";


create table public.lottery
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

create table public.participant
(
	id serial not null
		constraint participant_pk
			primary key,
	email varchar(99) not null,
	age integer not null,
	code varchar(16) not null,
    registration_date date not null,
	lottery_id serial not null
		constraint participant_lottery_id_fk
			references lottery,
    is_winner boolean default false not null
);

alter table participant owner to "lotteryDBUserDev";

create unique index participant_id_uindex
	on participant (id);

