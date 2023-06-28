CREATE TABLE IF NOT EXISTS seats (
    flight_id BIGINT,
    flight_date INT,
    flight_seatnumber INT,
    user_id BIGINT,
    user_name VARCHAR(20),
    PRIMARY KEY (flight_id, flight_date, flight_seatnumber)
);