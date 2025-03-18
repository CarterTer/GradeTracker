package io.github.carterter.gradetracker.controller;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/grades")
public class EnterGradesController {

    @GetMapping("/enter")
    public String showEnterForm() {
        return "entergrades";
    }

    @PostMapping("/enter")
    public String submitGrade(
        @RequestParam String courseId,
        @RequestParam String studentId,
        @RequestParam int score,
        @RequestParam(required = false) String feedback
    ) {
        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> gradeData = new HashMap<>();
        gradeData.put("score", score);
        gradeData.put("feedback", feedback);
        gradeData.put("timestamp", System.currentTimeMillis());

        db.collection("courses")
          .document(courseId)
          .collection("students")
          .document(studentId)
          .collection("grades")
          .add(gradeData);

        return "redirect:/grades/enter";
    }
}
