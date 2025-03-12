package io.github.carterter.gradetracker;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfiguration {
    @Bean
    GoogleCredentials googleCredentials() {
        try {
            InputStream serviceAccount = new FileInputStream("serviceaccount.json");
            return GoogleCredentials.fromStream(serviceAccount);
        } catch(IOException e) {
            throw new RuntimeException("Could not load GoogleCredentials! Did you set up a serviceaccount.json?", e);
        }
    }

    @Bean
    FirebaseApp firebaseApp(GoogleCredentials creds) {
        return FirebaseApp.initializeApp(
                FirebaseOptions.builder()
                        .setCredentials(creds)
                        .build()
        );
    }

    @Bean
    Firestore firestore(FirebaseApp app) {
        return FirestoreClient.getFirestore(app);
    }

    @Bean
    FirebaseAuth firebaseAuth(FirebaseApp app) {
        return FirebaseAuth.getInstance(app);
    }
}
