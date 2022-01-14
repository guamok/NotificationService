-- APP_TOKENS UPDATE AND MODIFY
ALTER TABLE app_tokens ADD COLUMN CURRENT_CREATION_DATE TIMESTAMP NOT NULL;
ALTER TABLE app_tokens ADD COLUMN CURRENT_UPDATE_DATE TIMESTAMP NOT NULL;

update  app_tokens set  CURRENT_CREATION_DATE = CREATION_DATE, CURRENT_UPDATE_DATE = UPDATE_DATE;

ALTER TABLE app_tokens DROP COLUMN CREATION_DATE;
ALTER TABLE app_tokens ADD COLUMN CREATION_DATE TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3);
ALTER TABLE app_tokens DROP COLUMN UPDATE_DATE;
ALTER TABLE app_tokens ADD COLUMN UPDATE_DATE TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3);

update  app_tokens set CREATION_DATE = CURRENT_CREATION_DATE,  UPDATE_DATE = CURRENT_UPDATE_DATE;

ALTER TABLE app_tokens DROP COLUMN CURRENT_CREATION_DATE;
ALTER TABLE app_tokens DROP COLUMN CURRENT_UPDATE_DATE;

-- MESSAGES UPDATE AND MODIFY
ALTER TABLE fcm_messages ADD COLUMN CURRENT_CREATION_DATE TIMESTAMP NOT NULL;
ALTER TABLE fcm_messages ADD COLUMN CURRENT_UPDATE_DATE TIMESTAMP NOT NULL;

update  fcm_messages set  CURRENT_CREATION_DATE = CREATION_DATE, CURRENT_UPDATE_DATE = UPDATE_DATE;

ALTER TABLE fcm_messages DROP COLUMN CREATION_DATE;
ALTER TABLE fcm_messages ADD COLUMN CREATION_DATE TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3);
ALTER TABLE fcm_messages DROP COLUMN UPDATE_DATE;
ALTER TABLE fcm_messages ADD COLUMN UPDATE_DATE TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3);

update  fcm_messages set CREATION_DATE = CURRENT_CREATION_DATE,  UPDATE_DATE = CURRENT_UPDATE_DATE;

ALTER TABLE fcm_messages DROP COLUMN CURRENT_CREATION_DATE;
ALTER TABLE fcm_messages DROP COLUMN CURRENT_UPDATE_DATE;




