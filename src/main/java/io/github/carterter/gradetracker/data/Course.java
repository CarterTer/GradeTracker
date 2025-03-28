package io.github.carterter.gradetracker.data;

import java.util.LinkedList;

//Basic course class, need a way to link a set of these to each user?
//fix access modifiers
public class Course {
    public String name;
    public String instructor;
    public LinkedList<Assignment> courseAssignments;
}
