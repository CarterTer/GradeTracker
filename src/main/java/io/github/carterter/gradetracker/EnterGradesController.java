package io.github.carterter.gradetracker.controller;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import io.github.carterter.gradetracker.data.Grade;

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
    
        if (!courseDoc.exists()) {
            return "redirect:/grades/enter";
        }
    
        Map<String, Object> courseData = courseDoc.getData();
        List<String> students = (List<String>) courseData.get("students");
    
        if (students == null) {
            students = new ArrayList<>();
        }
    
        // 这里开始构建学生成绩 Map<String, Grade>
        Map<String, Grade> studentGrades = new HashMap<>();
        for (String studentId : students) {
            var gradeDoc = db.collection("courses")
                             .document(courseId)
                             .collection("students")
                             .document(studentId)
                             .collection("grades")
                             .document("latest")
                             .get()
                             .get();
    
            if (gradeDoc.exists()) {
                Grade grade = gradeDoc.toObject(Grade.class);
                studentGrades.put(studentId, grade);
            }
        }
    
        model.addAttribute("courseId", courseId);
        model.addAttribute("students", students);
        model.addAttribute("studentGrades", studentGrades); // ✅ 这一行确保放进 model！
    
        return "coursestudents";
    }
    

    
    

    @PostMapping("/submit")
    public String submitGradeInline(
            @RequestParam String courseId,
            @RequestParam String studentId,
            @RequestParam int score,
            @RequestParam(required = false) String feedback
    ) {
        System.out.println(">>> Submitting grade for " + studentId + " in course " + courseId);
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
    
}
