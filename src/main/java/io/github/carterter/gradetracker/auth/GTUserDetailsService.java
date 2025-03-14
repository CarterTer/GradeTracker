package io.github.carterter.gradetracker.auth;

import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import io.github.carterter.gradetracker.FirebaseConfiguration;
import io.github.carterter.gradetracker.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Import(FirebaseConfiguration.class)
public class GTUserDetailsService implements UserDetailsService {
    @Autowired
    private FirebaseAuth auth;

    @Autowired
    private Firestore db;

    private static final String DUPLICATE_ACCOUNT_ERROR = "EMAIL_EXISTS";

    public boolean createUser(GTUser user, String role) {
        try {
            AggregateQuerySnapshot query = db.collection("users")
                    .whereEqualTo("username", user.getUsername())
                    .count()
                    .get()
                    .get();
            if(query.getCount() > 0) return false;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        UserRecord record;
        try {
            record = auth.createUser(
                    new UserRecord.CreateRequest()
                            .setEmail(user.getEmail())
                            .setEmailVerified(true)
            );
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            return false;
        }

        User repr = new User();
        repr.role = role;
        repr.uid = record.getUid();
        repr.username = user.getUsername();
        repr.password = user.getPassword();
        db.collection("users").document(record.getUid()).set(repr);

        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Query query = db.collection("users").whereEqualTo("username", username);

        QuerySnapshot evaluated;
        try {
            evaluated = query.get().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        int size = evaluated.size();
        if(size <= 0) {
            throw new UsernameNotFoundException("No such username");
        } else if(size > 1) {
            throw new IllegalStateException("Duplicate username");
        }

        User u = evaluated.iterator().next().toObject(User.class);
        String id = u.uid;

        UserRecord record;
        try {
            record = auth.getUser(id);
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }

        // fixme: pass the correct authorities
        return new GTUser(username, u.password, record.getEmail(), List.of());
    }
}