create table refresh_tokens(
id UUID primary key default gen_random_uuid(),
user_id uuid not null references users(id),
token_hash varchar(512) unique not null,
expires_at timestamp default (now() + interval '7 days'),
created_at timestamp default now()
)