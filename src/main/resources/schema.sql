create table lottery
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

alter table lottery owner to "lotteryDBUser";

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
	registration_date date not null,
	lottery_id serial not null
		constraint participant_lottery_id_fk
			references lottery,
	winner boolean default false not null
);

alter table participant owner to "lotteryDBUser";

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

alter table lottery_with_participant_count owner to "lotteryDBUser";

