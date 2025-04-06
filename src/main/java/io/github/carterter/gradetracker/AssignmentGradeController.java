package io.github.carterter.gradetracker.controller;

import com.google.cloud.firestore.*;
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

        DocumentSnapshot doc = db.collection("courses")
                .document(courseId)
                .collection("assignments")
                .document(assignmentId)
                .get()
                .get();

        Assignment assignment = doc.toObject(Assignment.class);
        if (assignment != null) {
            assignment.setId(doc.getId());
        }

        DocumentSnapshot courseDoc = db.collection("courses").document(courseId).get().get();
        List<String> students = (List<String>) courseDoc.get("students");
        if (students == null) {
            students = new ArrayList<>();
        }

        Map<String, Map<String, Object>> grades = new HashMap<>();
        for (String studentId : students) {
            DocumentSnapshot gdoc = db.collection("courses")
                    .document(courseId)
                    .collection("assignments")
                    .document(assignmentId)
                    .collection("grades")
                    .document(studentId)
                    .get()
                    .get();
            if (gdoc.exists()) {
                grades.put(studentId, gdoc.getData());
            } else {
                grades.put(studentId, null); 
            }
        }

        model.addAttribute("assignment", assignment);
        model.addAttribute("courseId", courseId);
        model.addAttribute("assignmentId", assignmentId);
        model.addAttribute("students", students);
        model.addAttribute("grades", grades);

        return "gradeassignment";
    }

    @PostMapping("/courses/{courseId}/assignments/{assignmentId}/grades/{studentId}")
    public String submitGrade(@PathVariable String courseId,
                              @PathVariable String assignmentId,
                              @PathVariable String studentId,
                              @RequestParam String score,
                              @RequestParam String feedback) {

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
                .set(grade);

        return "redirect:/courses/" + courseId + "/assignments/" + assignmentId + "/grades";
    }
}
