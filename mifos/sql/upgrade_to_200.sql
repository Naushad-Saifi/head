ALTER TABLE BATCH_LOAN_ARREARS_AGING 
    ADD COLUMN INTEREST_AGING DECIMAL(20,3) NOT NULL;
ALTER TABLE BATCH_LOAN_ARREARS_AGING 
    ADD COLUMN INTEREST_AGING_CURRENCY_ID SMALLINT NOT NULL;

UPDATE DATABASE_VERSION SET DATABASE_VERSION=200 WHERE DATABASE_VERSION=199;