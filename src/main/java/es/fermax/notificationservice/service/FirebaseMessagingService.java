package es.fermax.notificationservice.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FirebaseMessagingService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseMessagingService.class);


    public FirebaseSendResponse sendMessage(Message message) {
    	String messageAsJson = new Gson().toJson(message);
        log.info("Sending Firebase Message... {}", messageAsJson);
        try {
            // Send a message to the device corresponding to the provided registration token.
            String response = FirebaseMessaging.getInstance().send(message);

            // Response is a message ID string.
            log.info("Successfully sent message: {}", response);

            return new FirebaseSendResponse(true, response.substring(response.lastIndexOf("/") + 1), null, null);
        } catch (FirebaseMessagingException e) {
            log.error("Error sending message to Firebase. {} {}", e.getMessage(), e.getCause().getMessage());
            return new FirebaseSendResponse(false, null, e.getMessage(), e.getMessagingErrorCode());
        }
    }
}
