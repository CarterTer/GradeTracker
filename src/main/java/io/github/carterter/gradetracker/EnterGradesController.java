package io.github.carterter.gradetracker.controller;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import io.github.carterter.gradetracker.data.Assignment;
import io.github.carterter.gradetracker.data.Grade;
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
                .document("latest")
                .set(gradeData);

        return "redirect:/grades/enter";
    }

    @GetMapping("/course/{courseId}")
    public String showStudentsForCourse(@PathVariable String courseId, Model model) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        var courseDoc = db.collection("courses").document(courseId).get().get();

        if (!courseDoc.exists()) return "redirect:/grades/enter";

        List<String> students = (List<String>) courseDoc.get("students");
        if (students == null) students = new ArrayList<>();

        Map<String, Grade> studentGrades = new HashMap<>();
        for (String studentId : students) {
            var gradeDoc = db.collection("courses")
                    .document(courseId)
                    .collection("students")
                    .document(studentId)
                    .collection("grades")
                    .document("latest")
                    .get().get();

            Grade grade = gradeDoc.exists() ? gradeDoc.toObject(Grade.class) : null;
            studentGrades.put(studentId, grade);
        }

        List<Assignment> assignments = new ArrayList<>();
        var assignmentDocs = db.collection("courses")
                .document(courseId)
                .collection("assignments")
                .get().get().getDocuments();

        for (var doc : assignmentDocs) {
            Assignment a = doc.toObject(Assignment.class);
            a.setId(doc.getId());
            assignments.add(a);
        }

        model.addAttribute("courseId", courseId);
        model.addAttribute("students", students);
        model.addAttribute("studentGrades", studentGrades);
        model.addAttribute("assignments", assignments); 

        return "coursestudents";
    }

    @PostMapping("/submit")
    public String submitGradeInline(
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
                .document("latest")
                .set(gradeData);

        return "redirect:/grades/course/" + courseId;
    }

    @PostMapping("/submit-assignment")
    public String submitAssignmentGrade(
            @RequestParam String courseId,
            @RequestParam String assignmentId,
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
                .document(assignmentId)
                .set(gradeData);

        return "redirect:/grades/course/" + courseId + "/assignment/" + assignmentId + "/grade";
    }

    @GetMapping("/course/{courseId}/assignment/{assignmentId}/grade")
    public String showAssignmentGradingPage(
            @PathVariable String courseId,
            @PathVariable String assignmentId,
            Model model) throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        var assignmentDoc = db.collection("courses")
                .document(courseId)
                .collection("assignments")
                .document(assignmentId)
                .get().get();

        String assignmentTitle = assignmentDoc.exists() ? (String) assignmentDoc.get("title") : assignmentId;

        var courseDoc = db.collection("courses").document(courseId).get().get();
        List<String> students = (List<String>) courseDoc.get("students");
        if (students == null) students = new ArrayList<>();

        Map<String, Grade> studentGrades = new HashMap<>();
        for (String studentId : students) {
            var gradeDoc = db.collection("courses")
                    .document(courseId)
                    .collection("students")
                    .document(studentId)
                    .collection("grades")
                    .document(assignmentId)
                    .get().get();

            Grade grade = gradeDoc.exists() ? gradeDoc.toObject(Grade.class) : null;
            studentGrades.put(studentId, grade);
        }

        model.addAttribute("courseId", courseId);
        model.addAttribute("assignmentId", assignmentId);
        model.addAttribute("assignmentTitle", assignmentTitle);
        model.addAttribute("students", students);
        model.addAttribute("studentGrades", studentGrades);

        return "gradeassignment";
    }
}
