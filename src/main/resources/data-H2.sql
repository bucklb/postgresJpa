

-- Just add a question and some corresponding answers

insert into births (id, name, birthday) values (1, 'fred', 'yesterday');
insert into births (id, name, birthday) values (2, 'betty', 'today');
insert into births (id, name, birthday) values (3, 'wilma', 'tomorrow');

insert into questions (id, created_at, updated_at, title, description) values (99, now(),now(),  '9', '9');
insert into answers (id, created_at, updated_at, text, question_id) values (01, now(),now(),  'answer 01', 99);
insert into answers (id, created_at, updated_at, text, question_id) values (02, now(),now(),  'answer 02', 99);
insert into answers (id, created_at, updated_at, text, question_id) values (03, now(),now(),  'answer 03', 99);

--insert into births (id, created_at, updated_at, name, dateOfBirth) values (03, now(),now(),  'fred', 'yesterday');

