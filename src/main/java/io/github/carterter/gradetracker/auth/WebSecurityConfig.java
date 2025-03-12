package io.github.carterter.gradetracker.auth;

import com.google.api.gax.rpc.UnimplementedException;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import io.github.carterter.gradetracker.FirebaseConfiguration;
import io.github.carterter.gradetracker.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Configuration
@EnableWebSecurity
@Import(FirebaseConfiguration.class)
public class WebSecurityConfig {
    @Autowired
    private FirebaseAuth auth;

    @Autowired
    private Firestore db;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/home").permitAll()
                        .requestMatchers("/register").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll());

        return http.build();
    }

    @GetMapping("register")
    public String showRegisterPage(WebRequest req, Model model) {
        throw new RuntimeException("NYI");
    }

    @PostMapping("register")
    public void doRegister(@RequestParam String email, @RequestParam String username, @RequestParam String password) {
        throw new RuntimeException("NYI");
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            public void createUser(GTUser user, String role) {
                UserRecord record;
                try {
                    record = auth.createUser(
                            new UserRecord.CreateRequest()
                                    .setEmail(user.getEmail())
                                    .setPassword(user.getPassword())
                    );
                } catch (FirebaseAuthException e) {
                    e.printStackTrace();
                    return;
                }

                User repr = new User();
                repr.role = role;
                repr.uid = record.getUid();
                repr.username = user.getUsername();
                db.collection("users").document(record.getUid()).set(repr);
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
                String id = evaluated.iterator().next().getString("uid");

                UserRecord record;
                try {
                    record = auth.getUser(id);
                } catch (FirebaseAuthException e) {
                    throw new RuntimeException(e);
                }

                // fixme: pass the correct authorities
                return new GTUser(username, null, record.getEmail(), List.of());
            }
        };
    }
}