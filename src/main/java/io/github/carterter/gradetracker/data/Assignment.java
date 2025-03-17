package io.github.carterter.gradetracker.data;

//Stores the name, value, current grade, and feedback as strings for display to user
//Fixme: access modifiers
public class Assignment {
    public Assignment()
    {
        this.name = "New Assignment";
        this.pointValue = "0";
        this.gradeReceived = null;
        this.feedback = null;
    }

    public String name;
    public String gradeReceived;
    public String pointValue;
    public String feedback;
}
