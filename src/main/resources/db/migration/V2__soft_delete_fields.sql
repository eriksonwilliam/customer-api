alter table customer
    add column if not exists active boolean not null default true,
    add column if not exists deleted_at timestamp;