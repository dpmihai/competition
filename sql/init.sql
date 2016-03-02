create database competitiondb;

CREATE USER 'competitionsql'@'localhost' IDENTIFIED BY 'k27ToO5l67MG67xUQyX4';
grant all on competitiondb.* to 'competitionsql'@'localhost';
flush privileges;

DROP TABLE IF EXISTS app_parameters;
CREATE TABLE app_parameters ( 
    name varchar(255) NOT NULL, 
    value varchar(255), 
    PRIMARY KEY (name) 
); 

DROP TABLE IF EXISTS persistent_logins;
CREATE TABLE persistent_logins (
	uuid varchar(128) primary key, 
	username varchar(255) not null 	
);

DROP TABLE IF EXISTS users;
CREATE TABLE users ( 
    username varchar(255) NOT NULL, 
    admin tinyint(1) DEFAULT '0',    
    created_by varchar(50), 
    creation_date datetime, 
    email varchar(255),     
    phone varchar(255),
    password varchar(255), 
    team varchar(255),
    avatarFile varchar(255),
    PRIMARY KEY (username) 
); 

DROP TABLE IF EXISTS logins;
CREATE TABLE logins ( 
    username varchar(255) NOT NULL,      
    last_login datetime,     
    PRIMARY KEY (username) 
); 


DROP TABLE IF EXISTS competitions;
CREATE TABLE competitions ( 
    id bigint(20) NOT NULL AUTO_INCREMENT, 
    name varchar(255),   
    imageFile varchar(255),
    rss varchar(255),
    active tinyint(1) DEFAULT '0',  
    finished tinyint(1) DEFAULT '0', 
    quiz_date datetime, 
    playoff_first_stage_id bigint(20),
    PRIMARY KEY (id) 
); 

DROP TABLE IF EXISTS stages;
CREATE TABLE stages( 
    id bigint(20) NOT NULL AUTO_INCREMENT, 
    competition_id bigint(20), 
    name varchar(255), 
    fixture_date datetime, 
    PRIMARY KEY (id) 
); 

DROP TABLE IF EXISTS teams;
CREATE TABLE teams( 
    id bigint(20) NOT NULL AUTO_INCREMENT, 
    competition_id bigint(20), 
    name varchar(255), 
    abbreviation varchar(3), 
    avatarFile varchar(255),
    PRIMARY KEY (id) 
); 

DROP TABLE IF EXISTS games;
CREATE TABLE games( 
    id bigint(20) NOT NULL AUTO_INCREMENT, 
    stage_id bigint(20), 
    hosts_id bigint(20), 
    guests_id bigint(20),     
    hosts_score int(10), 
    guests_score int(10),
    fixture_date datetime,      
    PRIMARY KEY (id) 
); 

DROP TABLE IF EXISTS user_registrations;
CREATE TABLE user_registrations ( 
    id bigint(20) NOT NULL AUTO_INCREMENT, 
    competition_id bigint(20) NOT NULL, 
    username varchar(255) NOT NULL,      
    PRIMARY KEY (id) 
); 

DROP TABLE IF EXISTS user_scores;
CREATE TABLE user_scores ( 
    id bigint(20) NOT NULL AUTO_INCREMENT, 
    username varchar(255) NOT NULL,  
    game_id bigint(20), 
    hosts_score int(10), 
    guests_score int(10),
    points int(10) default 0,
    PRIMARY KEY (id) 
); 

DROP TABLE IF EXISTS user_champions;
CREATE TABLE user_champions ( 
    id bigint(20) NOT NULL AUTO_INCREMENT, 
    username varchar(255) NOT NULL,  
    team varchar(255),
    avatarFile varchar(255),
    competition varchar(255),
    enddate datetime,
    PRIMARY KEY (id) 
);    

DROP TABLE IF EXISTS user_total_scores;
CREATE TABLE user_total_scores ( 
	id bigint(20) NOT NULL AUTO_INCREMENT, 
	competition_id bigint(20), 
    username varchar(255) NOT NULL,    
    team varchar(255),
    points int(10) default 0,
    bonusPoints int(10) default 0,
    exactResults int(10) default 0,
    results1 int(10) default 0,
    resultsX int(10) default 0,
    results2 int(10) default 0,
    totalResults int(10) default 0,
    PRIMARY KEY (id) 
); 

DROP TABLE IF EXISTS user_lucky_team_points;
CREATE TABLE user_lucky_team_points ( 
	id bigint(20) NOT NULL AUTO_INCREMENT, 
	competition_id bigint(20), 
    username varchar(255) NOT NULL,    
    team varchar(255),
    points int(10) default 0,   
    goals int(10) default 0,
    PRIMARY KEY (id) 
); 

DROP TABLE IF EXISTS user_rankings;
CREATE TABLE user_rankings ( 
	id bigint(20) NOT NULL AUTO_INCREMENT, 
	competition_id bigint(20), 
    username varchar(255) NOT NULL,    
    team varchar(255),
    current_ranking int(10) default 0,
    previous_ranking int(10) default 0,
    PRIMARY KEY (id) 
); 

DROP TABLE IF EXISTS team_rankings;
CREATE TABLE team_rankings ( 
	id bigint(20) NOT NULL AUTO_INCREMENT, 
	competition_id bigint(20),         
    team varchar(255),
    avatarFile varchar(255),
    points int(10) default 0,
    games_played int(10) default 0,
    goals_for int(10) default 0,
    goals_against int(10) default 0,
    difference int(10) default 0,
    win int(10) default 0,
    deuce int(10) default 0,
    lost int(10) default 0,
    evolution varchar(5),
    PRIMARY KEY (id) 
);

DROP TABLE IF EXISTS best_stage_performers;
CREATE TABLE best_stage_performers ( 
    id bigint(20) NOT NULL AUTO_INCREMENT, 
    username varchar(255) NOT NULL, 
    stagename varchar(255) NOT NULL,
    team varchar(255) NOT NULL,
    points int(10) default 0,
    competition_id bigint(20), 
    PRIMARY KEY (id) 
); 

DROP TABLE IF EXISTS questions;
CREATE TABLE questions ( 
    id bigint(20) NOT NULL AUTO_INCREMENT, 
    competition_id bigint(20) NOT NULL, 
    question varchar(255) NOT NULL, 
    response varchar(255),
    PRIMARY KEY (id) 
); 

DROP TABLE IF EXISTS user_responses;
CREATE TABLE user_responses ( 
    id bigint(20) NOT NULL AUTO_INCREMENT, 
    competition_id bigint(20) NOT NULL, 
    username varchar(255) NOT NULL,    
    question_id  bigint(20) NOT NULL, 
    response varchar(255),
    PRIMARY KEY (id) 
); 

DROP TABLE IF EXISTS stages_playoff;
CREATE TABLE stages_playoff( 
    id bigint(20) NOT NULL AUTO_INCREMENT, 
    competition_id bigint(20), 
    name varchar(255), 
    stage_id bigint(20), /* stage from competition */ 
    PRIMARY KEY (id) 
); 

DROP TABLE IF EXISTS users_playoff;
CREATE TABLE users_playoff (   
	username varchar(255) NOT NULL,    
    stage_playoff_id bigint(20),     
    already_qualified tinyint(1) DEFAULT '0',      
    PRIMARY KEY (username)  
); 

DROP TABLE IF EXISTS games_playoff;
CREATE TABLE games_playoff( 
    id bigint(20) NOT NULL AUTO_INCREMENT, 
    stage_playoff_id bigint(20), 
    host_user varchar(255), 
    guest_user varchar(255),     
    hosts_score int(10), 
    guests_score int(10),
    shootout_winner varchar(255),
    PRIMARY KEY (id) 
); 

DROP TABLE IF EXISTS user_stage_bonus_points;
CREATE TABLE user_stage_bonus_points ( 
	id bigint(20) NOT NULL AUTO_INCREMENT, 
	stage_id bigint(20), 
    username varchar(255) NOT NULL,        
    bonus_points int(10) default 0,       
    PRIMARY KEY (id) 
); 

/** ALTER TABLE competitions ADD quiz_date date*/
/** ALTER TABLE competitions ADD playoff_first_stage_id bigint(20) */
/** ALTER TABLE users ADD phone varchar(255) */
/** ALTER TABLE user_total_scores ADD bonusPoints int(10) */

INSERT INTO users (username, admin, email, password) VALUES ('mike', 1, 'dpmihai@gmail.com', '1');
INSERT INTO users (username, admin, email, password) VALUES ('bogdan', 0, 'bogdanthfc@yahoo.com', '1');
INSERT INTO users (username, admin, email, password) VALUES ('bogdand', 0, 'bogdan.dancau@gmail.com', '1');
INSERT INTO users (username, admin, email, password) VALUES ('john', 0, 'ionutciocan@yahoo.com', '1');
INSERT INTO users (username, admin, email, password) VALUES ('cosmin', 0, 'cosbote@yahoo.com', '1');
INSERT INTO users (username, admin, email, password) VALUES ('gabi', 0, 'gabifloares@gmail.com', '1');
INSERT INTO users (username, admin, email, password) VALUES ('simona', 0, 'simona@gmail.com', '1');
INSERT INTO users (username, admin, email, password) VALUES ('maria', 0, 'maria@gmail.com', '1');


/*  ALTER TABLE teams ADD abbreviation varchar(3) */

