insert into authority (name) values ('ROLE_ADMIN')
insert into authority (name) values ('ROLE_USER')
insert into users (id,version,username,password_hash,first_name,last_name,email,image_url,activated,lang_key,activation_key,reset_key,created_by,created_date,reset_date,last_modified_by,last_modified_date) values (1,1,'admin','$2a$10$jpLNVNeA7Ar/ZQ2DKbKCm.MuT2ESe.Qop96jipKMq7RaUgCoQedV.','Administrator','Administrator','admin@localhost','',1,'vi',NULL,NULL,'system',NULL,NULL,'system',NULL)
insert into users (id,version,username,password_hash,first_name,last_name,email,image_url,activated,lang_key,activation_key,reset_key,created_by,created_date,reset_date,last_modified_by,last_modified_date) values (2,1,'user','$2a$10$xdbKoM48VySZqVSU/cSlVeJn0Z04XCZ7KZBjUBC00eKo5uLswyOpe','User','User','user@localhost','',1,'vi',NULL,NULL,'system',NULL,NULL,'system',NULL)
insert into user_authority (user_id,authority_name) values (1,'ROLE_ADMIN')
insert into user_authority (user_id,authority_name) values (2,'ROLE_USER')

