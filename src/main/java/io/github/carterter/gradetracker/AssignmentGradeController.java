package io.github.carterter.gradetracker.controller;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import io.github.carterter.gradetracker.data.Assignment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Controller
public class AssignmentGradeController {

    @GetMapping("/courses/{courseId}/assignments/{assignmentId}/grades")
    public String viewAssignmentGrades(@PathVariable String courseId,
                                       @PathVariable String assignmentId,
                                       Model model) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        DocumentSnapshot assignmentDoc = db.collection("courses")
                .document(courseId)
                .collection("assignments")
                .document(assignmentId)
                .get().get();

        Assignment assignment = assignmentDoc.toObject(Assignment.class);
        if (assignment != null) assignment.setId(assignmentDoc.getId());

        DocumentSnapshot courseDoc = db.collection("courses")
                .document(courseId)
                .get().get();

        @SuppressWarnings("unchecked")
        List<String> students = (List<String>) courseDoc.get("students");
        if (students == null) students = new ArrayList<>();

        Map<String, Map<String, Object>> studentGrades = new LinkedHashMap<>();
        for (String sid : students) {
            Map<String, Object> combined = new HashMap<>();
        
            DocumentSnapshot gradeDoc = db.collection("courses")
                    .document(courseId)
                    .collection("assignments")
                    .document(assignmentId)
                    .collection("grades")
                    .document(sid)
                    .get().get();
            if (gradeDoc.exists() && gradeDoc.getData() != null) {
                combined.putAll(gradeDoc.getData());
            }
        
            DocumentSnapshot studentDoc = db.collection("courses")
                    .document(courseId)
                    .collection("students")
                    .document(sid)
                    .get().get();
            if (studentDoc.exists() && studentDoc.contains("username")) {
                combined.put("username", studentDoc.getString("username"));
            }
        
            studentGrades.put(sid, combined);
        }
        

        model.addAttribute("studentGrades", studentGrades);
        model.addAttribute("assignment", assignment);
        model.addAttribute("courseId", courseId);
        model.addAttribute("assignmentId", assignmentId);
        model.addAttribute("students", students);
        //model.addAttribute("grades", grades);

        return "gradeassignment";
    }

    @PostMapping("/courses/{courseId}/assignments/{assignmentId}/grades/{studentId}")
    public String submitGrade(@PathVariable String courseId,
                              @PathVariable String assignmentId,
                              @PathVariable String studentId,
                              @RequestParam int score,
                              @RequestParam String feedback) throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        Map<String, Object> grade = new HashMap<>();
        grade.put("score", score);
        grade.put("feedback", feedback);
        grade.put("timestamp", new Date());

        db.collection("courses")
                .document(courseId)
                .collection("assignments")
                .document(assignmentId)
                .collection("grades")
                .document(studentId)
                .set(grade)
                .get();

        return "redirect:/courses/" + courseId + "/assignments/" + assignmentId + "/grades";
    }
}
