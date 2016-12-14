-- Table: account

-- DROP TABLE account;

CREATE TABLE account
(
  iban character varying(27) NOT NULL,
  name character varying(48),
  balance double precision,
  changed boolean,
  CONSTRAINT account_pk PRIMARY KEY (iban)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE account
  OWNER TO postgres;