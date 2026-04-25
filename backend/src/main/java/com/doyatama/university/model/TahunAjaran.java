package com.doyatama.university.model;

public class TahunAjaran {
    private String idTahun;
    private String tahunAjaran;
    private School study_program;

    public TahunAjaran() {
    }

    public TahunAjaran(String idTahun, String tahunAjaran, School study_program) {
        this.idTahun = idTahun;
        this.tahunAjaran = tahunAjaran;
        this.study_program = study_program;
    }

    public String getIdTahun() {
        return idTahun;
    }

    public void setIdTahun(String idTahun) {
        this.idTahun = idTahun;
    }

    public String getTahunAjaran() {
        return tahunAjaran;
    }

    public void setTahunAjaran(String tahunAjaran) {
        this.tahunAjaran = tahunAjaran;
    }

    public School getStudyProgram() {
        return study_program;
    }

    public void setStudyProgram(School study_program) {
        this.study_program = study_program;
    }

    public boolean isValid() {
        return this.idTahun != null && this.tahunAjaran != null;
    }

    public void set(String fieldName, String value) {
        switch (fieldName) {
            case "idTahun":
                this.idTahun = value;
                break;
            case "tahunAjaran":
                this.tahunAjaran = value;
                break;
            default:
                throw new IllegalArgumentException("Invalid field name: " + fieldName);
        }
    }
}
