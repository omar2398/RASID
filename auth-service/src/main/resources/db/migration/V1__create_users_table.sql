create table users(
id UUID primary key default gen_random_uuid(),
username varchar(50) not null unique,
email varchar(100) not null unique,
password varchar(255) not null,
role varchar(20) not null,
is_active boolean  not null default true,
created_at timestamp not null default now(),
updated_at timestamp not null default now()
)