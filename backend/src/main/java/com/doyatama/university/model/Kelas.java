package com.doyatama.university.model;

public class Kelas {
    private String idKelas;
    private String namaKelas;
    private StudyProgram study_program;
    private TahunAjaran tahunAjaran;

    public Kelas() {
    }

    public Kelas(String idKelas, String namaKelas, StudyProgram study_program, TahunAjaran tahunAjaran) {
        this.idKelas = idKelas;
        this.namaKelas = namaKelas;
        this.study_program = study_program;
        this.tahunAjaran = tahunAjaran;
    }

    public String getIdKelas() {
        return idKelas;
    }

    public void setIdKelas(String idKelas) {
        this.idKelas = idKelas;
    }

    public String getNamaKelas() {
        return namaKelas;
    }

    public void setNamaKelas(String namaKelas) {
        this.namaKelas = namaKelas;
    }

    public StudyProgram getStudyProgram() {
        return study_program;
    }

    public void setStudyProgram(StudyProgram study_program) {
        this.study_program = study_program;
    }

    public TahunAjaran getTahunAjaran() {
        return tahunAjaran;
    }

    public void setTahunAjaran(TahunAjaran tahunAjaran) {
        this.tahunAjaran = tahunAjaran;
    }

    public boolean isValid() {
        return this.idKelas != null && this.namaKelas != null;
    }

    public void set(String fieldName, String value) {
        switch (fieldName) {
            case "idKelas":
                this.idKelas = value;
                break;
            case "namaKelas":
                this.namaKelas = value;
                break;
            default:
                throw new IllegalArgumentException("Invalid field name: " + fieldName);
        }
    }
}
