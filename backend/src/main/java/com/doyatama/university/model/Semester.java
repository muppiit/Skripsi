package com.doyatama.university.model;

public class Semester {
    private String idSemester;
    private String namaSemester;
    private School study_program;

    public Semester() {
    }

    public Semester(String idSemester, String namaSemester, School study_program) {
        this.idSemester = idSemester;
        this.namaSemester = namaSemester;
        this.study_program = study_program;
    }

    public String getIdSemester() {
        return idSemester;
    }

    public void setIdSemester(String idSemester) {
        this.idSemester = idSemester;
    }

    public String getNamaSemester() {
        return namaSemester;
    }

    public void setNamaSemester(String namaSemester) {
        this.namaSemester = namaSemester;
    }

    public School getStudyProgram() {
        return study_program;
    }

    public void setStudyProgram(School study_program) {
        this.study_program = study_program;
    }

    public boolean isValid() {
        return this.idSemester != null && this.namaSemester != null;
    }

    public void set(String fieldName, String value) {
        switch (fieldName) {
            case "idSemester":
                this.idSemester = value;
                break;
            case "namaSemester":
                this.namaSemester = value;
                break;
            default:
                throw new IllegalArgumentException("Invalid field name: " + fieldName);
        }
    }
}
