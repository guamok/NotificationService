

CREATE TABLE fcm_messages (
    ID 					BINARY(16) NOT NULL,
    FCM_ID 				VARCHAR(255) NULL,
    NOTIFICATION_TITLE	VARCHAR(255) NULL,
    NOTIFICATION_BODY 	VARCHAR(512) NULL,
    NOTIFICATION_IMAGE_URL 	VARCHAR(255) NULL,
    DELIVERY_STATUS 	CHAR(1) NOT NULL,
    ERROR_DESC 			VARCHAR(512) NULL,
    ANDROID_CONFIG 		VARCHAR(512) NULL,
    APNS_CONFIG 		VARCHAR(512) NULL,
    WEB_PUSH_CONFIG 	VARCHAR(512) NULL,
    FCM_OPTIONS_LABEL 	VARCHAR(64) NULL,
    TARGET CHAR(1) 		NOT NULL,
    TARGET_VALUE 		VARCHAR(512) NOT NULL,
    CREATION_DATE 		TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATE_DATE 		TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (ID)
);

CREATE TABLE data_entries (
    ID 					BINARY(16) NOT NULL,
    PROPERTY 			VARCHAR(255) NOT NULL,
    VALUE 				VARCHAR(255) NOT NULL,
    PRIMARY KEY (ID , PROPERTY)
);

