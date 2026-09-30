insert into location(id, name, location_kind, create_date)
    values ('00000000-0000-0000-0000-000000000001', 'Moscow', 'PARENT', now());

insert into location(id, name, location_kind, create_date)
    values ('00000000-0000-0000-0000-000000000002', 'Moscow(Khimky)', 'PARENT', now());

insert into location(id, name, location_kind, create_date)
    values ('00000000-0000-0000-0000-000000000003', 'Moscowsk(Center)', 'PARENT', now());

insert into location(id, name, location_kind, create_date)
    values ('00000000-0000-0000-0000-000000000004', 'Kazan(Center)', 'PARENT', now());

insert into volunteer(user_id, first_name, last_name, middle_name, status, create_date)
    values ( 'User001', 'User', 'User', 'Vladimirovich', 'AVAILABLE', now());

insert into volunteer(user_id, first_name, last_name, middle_name, status, location_id, create_date)
    values ( 'User002', 'Ivan', 'Petrov', 'Sergeevich', 'AVAILABLE', '00000000-0000-0000-0000-000000000001', now());

insert into volunteer(user_id, first_name, last_name, middle_name, status, location_id, create_date)
    values ( 'User003', 'Anna', 'Smirnova', 'Olegovna', 'ASSIGNMENT', '00000000-0000-0000-0000-000000000002', now());

insert into volunteer(user_id, first_name, last_name, middle_name, status, location_id, create_date)
    values ( 'User004', 'Petr', 'Sidorov', null, 'AVAILABLE', '00000000-0000-0000-0000-000000000003', now());

insert into volunteer(user_id, first_name, last_name, middle_name, status, location_id, create_date)
    values ( 'User005', 'Maria', 'Kuznetsova', 'Andreevna', null, '00000000-0000-0000-0000-000000000004', now());

insert into volunteer(id, user_id, first_name, last_name, middle_name, status, create_date)
    values ('00000000-0000-0000-0000-000000000100', 'Username', 'Имя', 'Фамилия', 'Отчество', 'AVAILABLE', now());

insert into contact_info(contact, contact_type, volunteer_id, create_date)
    values ('username@mail.com', 'EMAIL', '00000000-0000-0000-0000-000000000100', now());

insert into contact_info(contact, contact_type, volunteer_id, create_date)
    values ('79990000000', 'PHONE', '00000000-0000-0000-0000-000000000100', now());
