# Sprint 1 Report

Video Link: [https://www.youtube.com/watch?v=QErPC\_C\_PEo](https://www.youtube.com/watch?v=QErPC_C_PEo) 

## What's new

* User Authorization and Login: Users can register an account, which they can then login to.  
* Users can choose to register as either teachers or students. The permissions of these two accounts are different: teachers can change grades and post comments, while students can view grades.  
* Teachers can enter student grades  
* Basic grade view for students.

## Work Summary

This Sprint mainly focused on developing some of the base-level functionality needed for the project, including Firebase support and user authorization through Google services. In addition, we laid a lot of the groundwork required using the Spring Boot set of libraries, filling out the setup for the backend. It took substantially longer than anticipated to implement many of the features as everyone on the team had to familiarize themselves with the frameworks and languages involved.

## Completed Issues/User Stories

* Create database schema: [https://github.com/CarterTer/GradeTracker/issues/1](https://github.com/CarterTer/GradeTracker/issues/1)  
  * Priority: High  
  * Assigned: Jasmine Erringer.  
  * Note: there is no associated pull request/user story as this is an intermediate issue, and not a feature by itself. Also, it took place on the database side, and not in the GitHub files.  
* Login/registration: [https://github.com/CarterTer/GradeTracker/issues/2](https://github.com/CarterTer/GradeTracker/issues/2)  
  * Priority: High  
  * Assigned: Jasmine Erringer.  
  * Pull request: [https://github.com/CarterTer/GradeTracker/pull/10](https://github.com/CarterTer/GradeTracker/pull/10).  
  * User stories:  
    * As a user, I would like to be able to securely log into the system so I can access the information which is relevant to my account.  
    * As a user, I would like to register for an account, so that I can log in and use the service.  
* Enter grades: [https://github.com/CarterTer/GradeTracker/issues/5](https://github.com/CarterTer/GradeTracker/issues/5)  
  * Priority: Medium  
  * Assigned: Yuhang Zhang  
  * Not yet to Merged:[https://github.com/CarterTer/GradeTracker/tree/Entergrade](https://github.com/CarterTer/GradeTracker/tree/Entergrade)  
  * User stories:  
    * As a teacher, I would like to be able to enter student grades and post comments and feedback.  
* Grade view: [https://github.com/CarterTer/GradeTracker/issues/6](https://github.com/CarterTer/GradeTracker/issues/6)  
  * Priority: High  
  * Assigned: Carter Terpening

## Code Files for Review

Please review the following code files, which were actively developed during this sprint, for quality:

* [https://github.com/CarterTer/GradeTracker/blob/main/src/main/java/io/github/carterter/gradetracker/FirebaseConfiguration.java](https://github.com/CarterTer/GradeTracker/blob/main/src/main/java/io/github/carterter/gradetracker/FirebaseConfiguration.java)  
* [https://github.com/CarterTer/GradeTracker/blob/main/src/main/java/io/github/carterter/gradetracker/auth/GTUserDetailsService.java](https://github.com/CarterTer/GradeTracker/blob/main/src/main/java/io/github/carterter/gradetracker/auth/GTUserDetailsService.java)  
* [https://github.com/CarterTer/GradeTracker/blob/main/src/main/java/io/github/carterter/gradetracker/auth/WebSecurityConfig.java](https://github.com/CarterTer/GradeTracker/blob/main/src/main/java/io/github/carterter/gradetracker/auth/WebSecurityConfig.java)  
* [https://github.com/CarterTer/GradeTracker/blob/main/src/main/java/io/github/carterter/gradetracker/data/User.java](https://github.com/CarterTer/GradeTracker/blob/main/src/main/java/io/github/carterter/gradetracker/data/User.java)  
* [https://github.com/CarterTer/GradeTracker/blob/main/src/main/java/io/github/carterter/gradetracker/auth/GTUser.java](https://github.com/CarterTer/GradeTracker/blob/main/src/main/java/io/github/carterter/gradetracker/auth/GTUser.java)

The following files are on a branch which the person working on has not yet merged into main:

* [https://github.com/CarterTer/GradeTracker/blob/StudentView/src/main/resources/templates/GradeView.html](https://github.com/CarterTer/GradeTracker/blob/StudentView/src/main/resources/templates/GradeView.html)  
* [https://github.com/CarterTer/GradeTracker/blob/StudentView/src/main/java/io/github/carterter/gradetracker/view/GradeViewController.java](https://github.com/CarterTer/GradeTracker/blob/StudentView/src/main/java/io/github/carterter/gradetracker/view/GradeViewController.java)  
* [https://github.com/CarterTer/GradeTracker/blob/StudentView/src/main/java/io/github/carterter/gradetracker/data/Assignment.java](https://github.com/CarterTer/GradeTracker/blob/StudentView/src/main/java/io/github/carterter/gradetracker/data/Assignment.java)  
* [https://github.com/CarterTer/GradeTracker/blob/StudentView/src/main/java/io/github/carterter/gradetracker/data/Course.java](https://github.com/CarterTer/GradeTracker/blob/StudentView/src/main/java/io/github/carterter/gradetracker/data/Course.java)

## Retrospective Summary

### What went well:

* We successfully laid the groundwork for future development. Some of the most fundamental elements, such as user authentication, have been completed. In addition, we have completed several of the most basic features.

### What to improve (team-wise):

* Communication. The team's communication overall could have been much better. Many members of the team did not communicate their intentions clearly, and so the team was left fragmented. This can be improved for future sprints, but only with the effort of all members.

### Plans for next sprint:

* Provide management of grades by category.  
* Calculating GPA.  
* Improvement of user interface.  
  * Better error messages.

  