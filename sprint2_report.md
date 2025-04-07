# Sprint 2 Report

Video Link: [https://www.youtube.com/watch?v=wm3bhM4vxpY](https://www.youtube.com/watch?v=wm3bhM4vxpY) 

## What's New (User Facing)

* Improve UI. The user interface is more consistent and easier to use.  
* A new course jump screen has been added. Instead of entering grades using course and student IDs, teachers can now use the interface to directly operate on the grades.  
* New assignments have been added, and teachers can now post multiple assignments within the same course.  
* Students can now select courses.  
* Students can now view their grades.  
* Average scores and the highest and lowest scores have been added.

## Work Summary (Developer Facing)

* Designed and implemented the /grades/enter and /grades/course/{courseId} endpoints using Spring Boot to support instructor grade assignment.  
* Integrated Firebase Firestore to persist course, student, and grading data using a nested structure: courses/{courseId}/students/{studentId}/grades/latest.  
* Used Thymeleaf to build dynamic UI templates, enabling instructors to select a course and input grades for each enrolled student without navigating away from the page.  
* Implemented student-course enrollment flow, ensuring two-way synchronization between course documents and subcollections.  
* Handled role-based access control to separate student and teacher permissions using Spring Security.  
* Resolved issues with asynchronous Firestore document structure (e.g., missing grades/latest subcollections for new students), added logic to check document existence before mapping to view models.  
* Optimized the grade viewing and updating interface to refresh in real time after grade submission.  
* Wrote CSS page styles and refactored HTML structure to improve user experience with the application.  
* Modified page navigation in order to make using the application more intuitive.

## Unfinished Work

We finished all of the work which we intended to for this sprint. In fact, midway through the sprint we increased the scope, and were still able to meet the requirements.

## Completed Issues/User Stories

Here are links to the issues that we completed in this sprint:

* [https://github.com/CarterTer/GradeTracker/issues/15](https://github.com/CarterTer/GradeTracker/issues/15)  
  * PR: [https://github.com/CarterTer/GradeTracker/pull/18](https://github.com/CarterTer/GradeTracker/pull/18)  
* [https://github.com/CarterTer/GradeTracker/issues/12](https://github.com/CarterTer/GradeTracker/issues/12)  
  * PR: [https://github.com/CarterTer/GradeTracker/pull/17](https://github.com/CarterTer/GradeTracker/pull/17)   
* [https://github.com/CarterTer/GradeTracker/issues/7](https://github.com/CarterTer/GradeTracker/issues/7)   
  * PR: [https://github.com/CarterTer/GradeTracker/pull/20](https://github.com/CarterTer/GradeTracker/pull/20) 

## Incomplete Issues/User Stories

Here are links to issues we worked on but did not complete in this sprint:

* We finished all issues we worked on in this sprint.

## Code Files for Review

Please review the following code files, which were actively developed during this  
sprint, for quality:

* [https://github.com/CarterTer/GradeTracker/blob/main/src/main/resources/static/css/main.css](https://github.com/CarterTer/GradeTracker/blob/main/src/main/resources/static/css/main.css)  
* [https://github.com/CarterTer/GradeTracker/blob/main/src/main/resources/static/css/center.css](https://github.com/CarterTer/GradeTracker/blob/main/src/main/resources/static/css/center.css)  
* [https://github.com/CarterTer/GradeTracker/blob/main/src/main/resources/templates/home.html](https://github.com/CarterTer/GradeTracker/blob/main/src/main/resources/templates/home.html)  
* [https://github.com/CarterTer/GradeTracker/blob/main/src/main/resources/templates/login.html](https://github.com/CarterTer/GradeTracker/blob/main/src/main/resources/templates/login.html)  
* [https://github.com/CarterTer/GradeTracker/blob/main/src/main/resources/templates/register.html](https://github.com/CarterTer/GradeTracker/blob/main/src/main/resources/templates/register.html) 

## Retrospective Summary

Here's what went well:

* This sprint, we had a bit better communication. A lot of this was as a result of the Friday meetings, where we could actually meet in person and discuss our plans for this sprint and the next.

Here's what we'd like to improve:

* Participation and communication could still be a lot better. While we did communicate better than last sprint, the team remained rather disjoint, and it was at times hard to know what others were working on and what their progress was.

Here are changes we plan to implement in the next sprint:

* In the next sprint, we will work more on assignment, having assignment-specific grades and feedback, and viewing of assignments for students. In addition, we will work on further improving the user interface, and adapting it as the application grows in scope. 