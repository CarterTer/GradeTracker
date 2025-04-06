package io.github.carterter.gradetracker;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/courses")
public class CourseSelectionController {

    @GetMapping("/join")
    public String showCourses(Model model) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection("courses").get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        model.addAttribute("courses", documents);
        return "joincourse";
    }

    @PostMapping("/join")
    public String joinCourse(@RequestParam String courseId, Authentication auth, Model model) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        String username = auth.getName();
    
        // 通过用户名获取 UID（Firebase 用户 ID）
        QuerySnapshot snapshot = db.collection("users")
                .whereEqualTo("username", username)
                .get().get();
    
        if (snapshot.isEmpty()) {
            model.addAttribute("error", "User not found in Firestore");
            return "joincourse";
        }
    
        String uid = snapshot.getDocuments().get(0).getId();
    
        // 写入到 courses/{courseId}/students 字段（数组）中
        db.collection("courses")
                .document(courseId)
                .update("students", FieldValue.arrayUnion(uid));
    
        // 写入一个占位文档（用于后续查找）
        Map<String, Object> placeholder = new HashMap<>();
        placeholder.put("joined", true);
        db.collection("courses")
                .document(courseId)
                .collection("students")
                .document(uid)
                .set(placeholder);
    
        return "redirect:/home";
    }
    
    
}
