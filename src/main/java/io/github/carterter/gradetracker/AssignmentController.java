package io.github.carterter.gradetracker.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import io.github.carterter.gradetracker.data.Assignment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Controller
public class AssignmentController {

    @GetMapping("/courses/{courseId}/assignments/new")
    public String showForm(@PathVariable String courseId, Model model) {
        model.addAttribute("courseId", courseId);
        return "createassignment";
    }

    @PostMapping("/courses/{courseId}/assignments")
    public String createAssignment(@PathVariable String courseId,
                                   @RequestParam String title,
                                   @RequestParam String content,
                                   @RequestParam String deadline,
                                   @RequestParam String createdBy,
                                   Model model) {
        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("content", content);
        data.put("deadline", deadline);
        data.put("createdBy", createdBy);
        data.put("timestamp", new Date());

        db.collection("courses")
          .document(courseId)
          .collection("assignments")
          .add(data);

        model.addAttribute("message", "Assignment created successfully!");
        model.addAttribute("courseId", courseId);
        return "createassignment";
    }

    @GetMapping("/courses/{courseId}/assignments")
    public String listAssignments(@PathVariable String courseId, Model model) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        List<Assignment> assignments = new ArrayList<>();
        var docs = db.collection("courses")
                     .document(courseId)
                     .collection("assignments")
                     .get()
                     .get()
                     .getDocuments();

        for (var doc : docs) {
            Assignment a = doc.toObject(Assignment.class);
            a.setId(doc.getId());
            assignments.add(a);
        }

        model.addAttribute("assignments", assignments);
        model.addAttribute("courseId", courseId);
        return "assignment";
    }
}
