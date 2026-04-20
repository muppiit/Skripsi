package com.doyatama.university.model;

import java.util.ArrayList;
import java.util.List;

public class Season {
    private String idSeason;
    private StudyProgram study_program;
    private Kelas kelas;
    private Semester semester;
    private Lecture lecture;
    private TahunAjaran tahunAjaran;
    private List<Student> student = new ArrayList<>();
    private List<Subject> subject = new ArrayList<>();

    public Season() {
    }

    public Season(String idSeason, StudyProgram study_program, Kelas kelas, Semester semester, Lecture lecture,
            TahunAjaran tahunAjaran, List<Student> student, List<Subject> subject) {
        this.idSeason = idSeason;
        this.study_program = study_program;
        this.kelas = kelas;
        this.semester = semester;
        this.lecture = lecture;
        this.tahunAjaran = tahunAjaran;
        this.student = student;
        this.subject = subject;
    }

    public String getIdSeason() {
        return idSeason;
    }

    public void setIdSeason(String idSeason) {
        this.idSeason = idSeason;
    }

    public StudyProgram getStudyProgram() {
        return study_program;
    }

    public void setStudyProgram(StudyProgram study_program) {
        this.study_program = study_program;
    }

    public Kelas getKelas() {
        return kelas;
    }

    public void setKelas(Kelas kelas) {
        this.kelas = kelas;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public Lecture getLecture() {
        return lecture;
    }

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    public TahunAjaran getTahunAjaran() {
        return tahunAjaran;
    }

    public void setTahunAjaran(TahunAjaran tahunAjaran) {
        this.tahunAjaran = tahunAjaran;
    }

    public List<Subject> getSubject() {
        return subject;
    }

    public void setSubject(List<Subject> subject) {
        this.subject = subject;
    }

    public void addSubject(Subject value) {
        this.subject.add(value);
    }

    public List<Student> getStudent() {
        return student;
    }

    public void setStudent(List<Student> student) {
        this.student = student;
    }

    public void addStudent(Student student) {
        this.student.add(student);
    }

    public boolean isValid() {
        return this.idSeason != null;
    }

    public void set(String fieldName, String value) {
        switch (fieldName) {
            case "idSeason":
                this.idSeason = value;
                break;
            default:
                throw new IllegalArgumentException("Invalid field name: " + fieldName);
        }
    }
}
