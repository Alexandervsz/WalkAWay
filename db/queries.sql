create table if not exists metvalues
(
    pkey serial not null
    constraint metvalues_pkey
    primary key,
    metvalue numeric,
    speeda numeric,
    activity varchar,
    speedb numeric
);

insert into metvalues (pkey, metvalue, speeda, activity, speedb)
values  (6, 3, 4.02336, 'Walking', 0),
        (7, 3.5, 4.5061632, 'Walking', 5.1499008),
        (8, 4.3, 5.632704, 'Walking', 0),
        (9, 5, 6.437376, 'Walking', 0),
        (10, 7, 7.242048, 'Walking', 0),
        (11, 8.3, 8.04672, 'Walking', 0),
        (12, 6, 6.437376, 'Running', 0),
        (13, 8.3, 8.04672, 'Running', 0),
        (14, 9.8, 9.656064, 'Running', 0),
        (15, 11, 11.265408, 'Running', 0),
        (16, 11.8, 12.07008, 'Running', 0),
        (17, 11.8, 12.874752, 'Running', 0),
        (18, 12.8, 14.484096, 'Running', 0),
        (19, 14.5, 16.09344, 'Running', 0),
        (20, 16, 17.702784, 'Running', 0),
        (21, 19, 19.312128, 'Running', 0),
        (22, 19.8, 20.921472, 'Running', 0),
        (23, 23, 22.530816, 'Running', 0),
        (24, 5, 0, 'Skateboarding', 0),
        (25, 7, 0, 'Rollerskating', 0),
        (26, 7.5, 14.484096, 'Rollerblading', 0),
        (27, 9.8, 17.702784, 'Rollerblading', 0),
        (28, 12.3, 20.921472, 'Rollerblading', 21.8870784),
        (29, 14, 24.14016, 'Rollerblading', 0);

create table waytype
(
    id serial not null
        constraint nodetype_pkey
            primary key,
    main_type varchar not null,
    sub_type varchar
);

insert into waytype (id, main_type, sub_type)
values  (1, 'highway', 'residential'),
        (2, 'highway', 'footway'),
        (3, 'highway', 'cycleway'),
        (4, 'highway', 'path'),
        (7, 'highway', 'service'),
        (9, 'highway', 'unclassified'),
        (10, 'highway', 'living_street'),
        (11, 'highway', 'track'),
        (12, 'highway', 'pedestrian');