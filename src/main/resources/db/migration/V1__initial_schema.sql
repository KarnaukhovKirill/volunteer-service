create table location
(
    id            uuid primary key      default gen_random_uuid(),
    name          varchar(255) not null unique,
    parent_loc_id uuid references location (id),
    location_kind varchar(50)  not null default 'PARENT' check (location_kind in ('PARENT', 'CHILD')),
    create_date   timestamp(6) not null default now(),
    update_date   timestamp(6)
);

create index idx_location_parent_loc_id on location (parent_loc_id);

create table volunteer
(
    id                  uuid primary key      default gen_random_uuid(),
    user_id             varchar(255) not null unique,
    first_name          varchar(255) not null,
    last_name           varchar(255) not null,
    middle_name         varchar(255),
    status              varchar(50) check (status in ('AVAILABLE', 'ASSIGNMENT')),
    location_id         uuid references location (id),
    current_incident_id uuid,
    create_date         timestamp(6) not null default now(),
    update_date         timestamp(6)
);

create index idx_volunteer_location_id on volunteer (location_id);

create table contact_info
(
    id           uuid primary key      default gen_random_uuid(),
    contact      varchar(255) not null unique,
    contact_type varchar(50)  not null check (contact_type in ('EMAIL', 'PHONE')),
    -- при удалении волонтёра удаляем и его контакты
    volunteer_id uuid references volunteer (id) on delete cascade,
    create_date  timestamp(6) not null default now(),
    update_date  timestamp(6)
);

create index idx_contact_info_volunteer_id on contact_info (volunteer_id);
