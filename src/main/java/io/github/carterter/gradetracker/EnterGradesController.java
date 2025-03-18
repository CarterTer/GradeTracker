package io.github.carterter.gradetracker.controller;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/grades")
public class EnterGradesController {


    @GetMapping("/enter")
    public String showEnterForm(Model model) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        List<String> courseList = new ArrayList<>();
        var courses = db.collection("courses").get().get().getDocuments();
        for (QueryDocumentSnapshot doc : courses) {
            courseList.add(doc.getId());
        }

        List<String> studentList = new ArrayList<>();
        var students = db.collectionGroup("students").get().get().getDocuments();
        for (QueryDocumentSnapshot doc : students) {
            studentList.add(doc.getId());
        }

        model.addAttribute("courses", courseList);
        model.addAttribute("students", studentList);

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
