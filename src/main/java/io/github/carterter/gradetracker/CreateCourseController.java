package io.github.carterter.gradetracker;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;

@Controller
@RequestMapping("/courses")
public class CreateCourseController {

    @GetMapping("/create")
    public String showCourseForm(@RequestParam(required = false) String error, Model model) {
        model.addAttribute("error", error);
        return "createcourse";
    }

    @PostMapping("/create")
    public String submitCourse(
            @RequestParam String courseId,
            @RequestParam String title,
            Authentication auth,
            Model model
    ) throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        // ✅ Step 1: 检查 course_catalog 中是否有这个课程 ID
        DocumentSnapshot catalogDoc = db.collection("course_catalog").document(courseId).get().get();
        if (!catalogDoc.exists()) {
            return "redirect:/courses/create?error=Course%20ID%20not%20in%20catalog.";
        }

        // ✅ Step 2: 检查 courses 中是否已经存在
        DocumentSnapshot existingCourse = db.collection("courses").document(courseId).get().get();
        if (existingCourse.exists()) {
            return "redirect:/courses/create?error=Course%20already%20created.";
        }

        // ✅ Step 3: 创建课程
        Map<String, Object> courseData = new HashMap<>();
        courseData.put("id", courseId);
        courseData.put("title", title);
        courseData.put("teacher", auth.getName());
        courseData.put("students", new ArrayList<String>());

        db.collection("courses").document(courseId).set(courseData).get();

        return "redirect:/home";
    }
}
