package io.github.carterter.gradetracker;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/courses")
public class CourseSelectionController {

    @GetMapping("/join")
    public String showCourses(Model model) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection("courses").get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        model.addAttribute("courses", documents);
        return "joincourse";
    }

    @PostMapping("/join")
    public String joinCourse(@RequestParam String courseId, Authentication auth, Model model) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        String username = auth.getName();
    
        QuerySnapshot snapshot = db.collection("users")
                .whereEqualTo("username", username)
                .get().get();
    
        if (snapshot.isEmpty()) {
            model.addAttribute("error", "User not found in Firestore");
            return "joincourse";
        }
    
        String uid = snapshot.getDocuments().get(0).getId();

        db.collection("courses")
          .document(courseId)
          .update("students", FieldValue.arrayUnion(uid));
    
        Map<String, Object> studentInfo = new HashMap<>();
        studentInfo.put("joined", true);
        studentInfo.put("username", username);
    
        db.collection("courses")
          .document(courseId)
          .collection("students")
          .document(uid)
          .set(studentInfo);
    
        return "redirect:/home";
    }
    
}
