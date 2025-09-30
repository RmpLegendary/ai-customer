create table if not exists products (
                                        id bigint primary key,
                                        title text not null,
                                        vendor text,
                                        product_type text,
                                        created_at timestamptz default now()
    );

create table if not exists variants (
                                        id bigint primary key,
                                        product_id bigint not null references products(id) on delete cascade,
    title text not null,
    price numeric(10,2) not null,
    available boolean default true
    );

create index if not exists idx_products_vendor on products(vendor);
create index if not exists idx_products_type on products(product_type);
create index if not exists idx_variants_product_id on variants(product_id);