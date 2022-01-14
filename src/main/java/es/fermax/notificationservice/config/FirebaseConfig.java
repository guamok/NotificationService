package es.fermax.notificationservice.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase-sdk.keys.path}")
    public String keysPath;

    @Value("${firebase-sdk.keys.file}")
    public String keysFile;


    @Bean
    public FirebaseConfig firebaseInstanceConfig() {
        log.info("Initializing Firebase SDK with keys: {} {}", keysPath, keysFile );

        try {
            InputStream serviceAccount = new FileInputStream(keysPath + keysFile);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://fermax-blue.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);
            log.info("Firebase SDK has been initialized successfully. With KEYS on path: {}{}", keysPath, keysFile );

        } catch (FileNotFoundException e) {
            log.error("Could not find the firebase file");
        } catch (IOException e) {
            log.error("Could not open the firebase file");
        }

        return new FirebaseConfig();
    }
}
