create table upload_statistics
(
    client_ip   varchar(45) not null,
    date        date        not null,
    usage_count bigint,
    primary key (client_ip, date)
)