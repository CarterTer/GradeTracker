package io.github.carterter.gradetracker.controller;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/student")
public class StudentGradesController {

    @GetMapping("/viewmygrades")
    public RedirectView redirectToGrades(Authentication auth) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        String username = auth.getName();
    
        QuerySnapshot snapshot = db.collection("users")
                .whereEqualTo("username", username)
                .get().get();
    
        if (snapshot.isEmpty()) {
            throw new RuntimeException("User not found in Firestore");
        }
    
        String studentId = username; 
    
        String courseId = null;
        for (var courseDoc : db.collection("courses").get().get().getDocuments()) {
            List<String> students = (List<String>) courseDoc.get("students");
            if (students != null && students.contains(studentId)) {
                courseId = courseDoc.getId();
                break;
            }
        }
    
        if (courseId == null) {
            throw new RuntimeException("No course found for student: " + studentId);
        }
    
        return new RedirectView("/student/" + studentId + "/course/" + courseId + "/grades");
    }
    

    @GetMapping("/{studentId}/course/{courseId}/grades")
public String viewGradesStats(@PathVariable String studentId,
                              @PathVariable String courseId,
                              Model model) throws ExecutionException, InterruptedException {
    Firestore db = FirestoreClient.getFirestore();

    List<Double> scores = new ArrayList<>();
    List<Map<String, Object>> gradeList = new ArrayList<>();

    var assignmentDocs = db.collection("courses")
            .document(courseId)
            .collection("assignments")
            .get().get().getDocuments();

    for (var doc : assignmentDocs) {
        String assignmentId = doc.getId();
        String title = (String) doc.get("title");

        var gradeDoc = db.collection("courses")
                .document(courseId)
                .collection("assignments")
                .document(assignmentId)
                .collection("grades")
                .document(studentId)
                .get().get();

        if (gradeDoc.exists() && gradeDoc.get("score") != null) {
            double score = Double.parseDouble(gradeDoc.get("score").toString());

            Map<String, Object> gradeEntry = new HashMap<>();
            gradeEntry.put("assignment", title);
            gradeEntry.put("score", score);
            gradeEntry.put("feedback", gradeDoc.get("feedback"));
            gradeList.add(gradeEntry);
            scores.add(score);
        }
    }

    double avg = scores.stream().mapToDouble(d -> d).average().orElse(0);
    double max = scores.stream().mapToDouble(d -> d).max().orElse(0);
    double min = scores.stream().mapToDouble(d -> d).min().orElse(0);
    double median = 0;
    if (!scores.isEmpty()) {
        List<Double> sorted = scores.stream().sorted().toList();
        int mid = sorted.size() / 2;
        median = sorted.size() % 2 == 0 ? (sorted.get(mid - 1) + sorted.get(mid)) / 2 : sorted.get(mid);
    }

    model.addAttribute("grades", gradeList);
    model.addAttribute("average", avg);
    model.addAttribute("max", max);
    model.addAttribute("min", min);
    model.addAttribute("median", median);
    model.addAttribute("studentId", studentId);
    model.addAttribute("courseId", courseId);

    return "studentgrades";
}

}
