package com.doyatama.university.payload;

import java.util.List;

public class SeasonRequest {
    private String idSeason;
    private String studyProgram_id;
    private String kelas_id;
    private String lecture_id;
    private String tahunAjaran_id;
    private String semester_id;
    private List<String> student_id;
    private List<String> subject_id;

    public SeasonRequest() {
    }

    public SeasonRequest(String idSeason, String studyProgram_id, String kelas_id, String lecture_id,
            String tahunAjaran_id, String semester_id, List<String> student_id, List<String> subject_id) {
        this.idSeason = idSeason;
        this.studyProgram_id = studyProgram_id;
        this.kelas_id = kelas_id;
        this.lecture_id = lecture_id;
        this.tahunAjaran_id = tahunAjaran_id;
        this.semester_id = semester_id;
        this.student_id = student_id;
        this.subject_id = subject_id;
    }

    public String getIdSeason() {
        return idSeason;
    }

    public void setIdSeason(String idSeason) {
        this.idSeason = idSeason;
    }

    public String getStudyProgram_id() {
        return studyProgram_id;
    }

    public void setStudyProgram_id(String studyProgram_id) {
        this.studyProgram_id = studyProgram_id;
    }

    public String getSemester_id() {
        return semester_id;
    }

    public void setSemester_id(String semester_id) {
        this.semester_id = semester_id;
    }

    public String getKelas_id() {
        return kelas_id;
    }

    public void setKelas_id(String kelas_id) {
        this.kelas_id = kelas_id;
    }

    public String getLecture_id() {
        return lecture_id;
    }

    public void setLecture_id(String lecture_id) {
        this.lecture_id = lecture_id;
    }

    public String getTahunAjaran_id() {
        return tahunAjaran_id;
    }

    public void setTahunAjaran_id(String tahunAjaran_id) {
        this.tahunAjaran_id = tahunAjaran_id;
    }

    public List<String> getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(List<String> subject_id) {
        this.subject_id = subject_id;
    }

    public List<String> getStudent_id() {
        return student_id;
    }

    public void setStudent_id(List<String> student_id) {
        this.student_id = student_id;
    }

    // public String getJadwalPelajaran_id() {
    // return jadwalPelajaran_id;
    // }
    //
    // public void setJadwalPelajaran_id(String jadwalPelajaran_id) {
    // this.jadwalPelajaran_id = jadwalPelajaran_id;
    // }

    // Metode set dengan penambahan lecture_id, student_id, dan jadwalPelajaran_id
    public void set(String fieldName, Object value) {
        switch (fieldName) {
            case "idSeason":
                this.idSeason = (String) value;
                break;
            case "studyProgram_id":
                this.studyProgram_id = (String) value;
                break;
            case "kelas_id":
                this.kelas_id = (String) value;
                break;
            case "lecture_id":
                this.lecture_id = (String) value;
                break;
            case "tahunAjaran_id":
                this.tahunAjaran_id = (String) value;
                break;
            case "semester_id":
                this.semester_id = (String) value;
                break;
            // case "student_id":
            // this.student_id = (List<String>) value;
            // break;
            // case "jadwalPelajaran_id":
            // this.jadwalPelajaran_id = (List<String>) value;
            // break;
            default:
                throw new IllegalArgumentException("Invalid field name: " + fieldName);
        }
    }
}
