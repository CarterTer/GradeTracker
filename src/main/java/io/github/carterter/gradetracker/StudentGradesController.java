package io.github.carterter.gradetracker.controller;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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

        DocumentSnapshot userDoc = snapshot.getDocuments().get(0);
        String studentId = userDoc.getId(); // UID
        
        var courses = db.collection("courses").get().get().getDocuments();
        for (var course : courses) {
            List<String> students = (List<String>) course.get("students");
            if (students != null && students.contains(studentId)) {
                return new RedirectView("/student/" + studentId + "/course/" + course.getId() + "/grades");
            }
        }

        throw new RuntimeException("No course found for student: " + studentId);
    }

    @GetMapping("/{studentId}/course/{courseId}/grades")
    public String viewGradesStats(@PathVariable String studentId,
                                   @PathVariable String courseId,
                                   Model model) throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();
        List<Map<String, Object>> gradeList = new ArrayList<>();
        List<Double> allScores = new ArrayList<>();
        double personalTotal = 0;

        var assignmentDocs = db.collection("courses")
                .document(courseId)
                .collection("assignments")
                .get().get().getDocuments();

        for (var assignmentDoc : assignmentDocs) {
            String assignmentId = assignmentDoc.getId();
            String title = (String) assignmentDoc.get("title");

            var currentGrade = db.collection("courses")
                    .document(courseId)
                    .collection("assignments")
                    .document(assignmentId)
                    .collection("grades")
                    .document(studentId)
                    .get().get();

            Double studentScore = null;
            String feedback = null;
            if (currentGrade.exists() && currentGrade.contains("score")) {
                studentScore = Double.parseDouble(currentGrade.get("score").toString());
                feedback = (String) currentGrade.get("feedback");
                personalTotal += studentScore;
            }

            var allStudentGrades = db.collection("courses")
                    .document(courseId)
                    .collection("assignments")
                    .document(assignmentId)
                    .collection("grades")
                    .get().get().getDocuments();

            List<Double> scores = new ArrayList<>();
            for (var g : allStudentGrades) {
                if (g.contains("score")) {
                    scores.add(Double.parseDouble(g.get("score").toString()));
                    allScores.add(Double.parseDouble(g.get("score").toString()));
                }
            }

            double avg = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double max = scores.stream().mapToDouble(Double::doubleValue).max().orElse(0);
            double min = scores.stream().mapToDouble(Double::doubleValue).min().orElse(0);
            double median = 0;
            if (!scores.isEmpty()) {
                List<Double> sorted = scores.stream().sorted().collect(Collectors.toList());
                int mid = sorted.size() / 2;
                median = sorted.size() % 2 == 0
                        ? (sorted.get(mid - 1) + sorted.get(mid)) / 2
                        : sorted.get(mid);
            }

            Map<String, Object> entry = new HashMap<>();
            entry.put("title", title);
            entry.put("score", studentScore);
            entry.put("feedback", feedback);
            entry.put("average", avg);
            entry.put("max", max);
            entry.put("min", min);
            entry.put("median", median);
            gradeList.add(entry);
        }

        model.addAttribute("grades", gradeList);
        model.addAttribute("total", personalTotal);
        model.addAttribute("courseId", courseId);
        model.addAttribute("studentId", studentId);
        return "studentgrades";
    }
}
