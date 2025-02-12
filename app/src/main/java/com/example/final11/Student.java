package com.example.final11;

public class Student {
    private String studentId;
    private String name;
    private String major;
    private String course;
    private String grade;

    // Empty constructor for Firebase
    public Student() {}

    public Student(String studentId, String name, String major, String course, String grade) {
        this.studentId = studentId;
        this.name = name;
        this.major = major;
        this.course = course;
        this.grade = grade;
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getMajor() { return major; }
    public String getCourse() { return course; }
    public String getGrade() { return grade; }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
