drop table account;

CREATE TABLE account(
 id integer PRIMARY KEY,
 iban VARCHAR(50) UNIQUE NOT NULL,
 owner_name VARCHAR (50) NOT NULL,
 balance NUMERIC(20,3) NOT NULL,
 currency VARCHAR (5) NOT NULL
);

drop table acc_history;

CREATE TABLE acc_history(
 id integer PRIMARY KEY,
 acc_id integer NOT NULL,
 balance NUMERIC(20,3) NOT NULL,
 stamp timestamp NOT NULL,
 CONSTRAINT account_id_fkey FOREIGN KEY (acc_id)
      REFERENCES account (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
