package io.github.carterter.gradetracker.data;

public class Grade {
    private int score;
    private String feedback;
    private long timestamp;

    public Grade() {}

    public Grade(int score, String feedback, long timestamp) {
        this.score = score;
        this.feedback = feedback;
        this.timestamp = timestamp;
    }

    public int getScore() {
        return score;
    }

    public String getFeedback() {
        return feedback;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
