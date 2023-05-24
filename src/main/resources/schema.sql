DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS users CASCADE;


CREATE TABLE users (
	id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	"name" varchar NOT NULL,
	email varchar NOT NULL UNIQUE
);

CREATE TABLE requests (
	id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	description varchar NOT NULL,
	requestor_id BIGINT NOT NULL REFERENCES users ON DELETE CASCADE,
	created date NOT NULL
);

CREATE TABLE items (
	id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	"name" varchar NOT NULL,
	description varchar NOT NULL,
	is_available boolean NOT NULL,
	owner_id BIGINT NOT NULL REFERENCES users ON DELETE CASCADE,
	request_id BIGINT REFERENCES requests,
	last_booking_id BIGINT,
    next_booking_id BIGINT
);

CREATE TABLE bookings (
	id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	item_id BIGINT NOT NULL REFERENCES items ON DELETE CASCADE,
	booker_id BIGINT NOT NULL REFERENCES users ON DELETE CASCADE,
	status varchar NOT NULL
);

CREATE TABLE comments (
	id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	"text" varchar NOT NULL,
	item_id BIGINT NOT NULL REFERENCES items ON DELETE CASCADE,
	author_id BIGINT NOT NULL REFERENCES users ON DELETE CASCADE,
	created TIMESTAMP WITHOUT TIME ZONE NOT NULL
);
