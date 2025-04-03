package io.github.carterter.gradetracker.view;

import io.github.carterter.gradetracker.data.Course;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class GradeViewController {
    //Need a way to pass in the course or access a currently selected course through firebase auth?
    @GetMapping("/assignmentGrades")
    public String assignmentGrades(Authentication authentication, Model model, Course selectedCourse)
    {
        if (authentication != null)
        {
            model.addAttribute("coursegrades", selectedCourse.courseAssignments);
        }
        else
        {
            model.addAttribute("errormessage", "Log In to View Grades");
        }
        return "assignmentGrades";
    }

    @GetMapping("/instructor")
    public String instructor(Model model, Course selectedCourse)
    {
        if (selectedCourse != null)
        {
            model.addAttribute("instructorName", selectedCourse.instructor);
        }
        else
        {
            model.addAttribute("errormessage", "No Course Selected");
        }
        return "instructor";
    }

    @GetMapping("/courseName")
    public String courseName(Model model, Course selectedCourse)
    {
        if (selectedCourse != null)
        {
            model.addAttribute("courseName", selectedCourse.name);
        }
        else
        {
            model.addAttribute("errorMessage", "No Course Selected");
        }
        return "courseName";
    }
}
