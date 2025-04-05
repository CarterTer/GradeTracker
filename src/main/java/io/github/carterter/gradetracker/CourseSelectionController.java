package io.github.carterter.gradetracker;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
    public String joinCourse(@RequestParam String courseId, Authentication auth) {
        Firestore db = FirestoreClient.getFirestore();
        db.collection("courses")
          .document(courseId)
          .update("students", FieldValue.arrayUnion(auth.getName()));
        return "redirect:/home";
    }
}
