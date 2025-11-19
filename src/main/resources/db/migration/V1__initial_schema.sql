create table customer
(
    id         uuid primary key,
    name       varchar(100) not null,
    cpf        varchar(11)  not null,
    email      varchar(100) not null,
    phone      varchar(20),
    created_at timestamp    not null default now(),
    updated_at timestamp    not null default now(),

    constraint uk_customer_cpf unique (cpf),
    constraint uk_customer_email unique (email)
);