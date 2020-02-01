create IF NOT EXISTS schema public;

comment on schema public is 'standard public schema';

alter schema public owner to lotteryDBUser;

create sequence IF NOT EXISTS lottery_id_seq;

alter sequence lottery_id_seq owner to "lotteryDBUser";

create table IF NOT EXISTS participant
(
	id bigserial not null
		constraint participant_pk
			primary key,
	email varchar default 100 not null,
	age integer default 4 not null,
	code varchar default 16
);

alter table participant owner to "lotteryDBUser";

create unique index participant_id_uindex
	on participant (id);

create table IF NOT EXISTS lottery
(
	id bigserial not null
		constraint lottery_pk
			primary key,
	title varchar default 400 not null,
	start_date date default now() not null,
	end_date date
);

alter table lottery owner to "lotteryDBUser";

create unique index lottery_id_uindex
	on lottery (id);

