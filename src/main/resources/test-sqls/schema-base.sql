drop schema if exists public CASCADE ;
drop view if exists lottery_with_participant_count;
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
	end_date date,
    completed boolean default false not null
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
    winner boolean default false not null
);

alter table participant owner to "lotteryDBUserDev";

create unique index participant_id_uindex
	on participant (id);

create view lottery_with_participant_count(id, title, participant_limit, start_date, end_date, participants) as
SELECT l.id,
       l.title,
       l.participant_limit,
       l.start_date,
       l.end_date,
       count(p.*) AS participants
FROM lottery l
         LEFT JOIN participant p ON p.lottery_id = l.id
GROUP BY l.id;

alter table lottery_with_participant_count owner to "lotteryDBUserDev";


create view full_lottery_with_participant_count(id, title, participant_limit, start_date, end_date, completed, participants) as
SELECT l.id,
       l.title,
       l.participant_limit,
       l.start_date,
       l.end_date,
       l.completed,
       count(p.*) AS participants
FROM lottery l
         LEFT JOIN participant p ON p.lottery_id = l.id
GROUP BY l.id;

alter table full_lottery_with_participant_count owner to "lotteryDBUserDev";