package com.doyatama.university.payload;

public class KelasRequest {
    private String idKelas;
    private String namaKelas;
    private String idStudyProgram;
    private String idTahunAjaran;

    public KelasRequest() {
    }

    public KelasRequest(String idKelas, String namaKelas, String idStudyProgram, String idTahunAjaran) {
        this.idKelas = idKelas;
        this.namaKelas = namaKelas;
        this.idStudyProgram = idStudyProgram;
        this.idTahunAjaran = idTahunAjaran;
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

    public String getIdStudyProgram() {
        return idStudyProgram;
    }

    public void setIdStudyProgram(String idStudyProgram) {
        this.idStudyProgram = idStudyProgram;
    }

    public String getIdTahunAjaran() {
        return idTahunAjaran;
    }

    public void setIdTahunAjaran(String idTahunAjaran) {
        this.idTahunAjaran = idTahunAjaran;
    }

    public void set(String fieldName, String value) {
        switch (fieldName) {
            case "idKelas":
                this.idKelas = value;
                break;
            case "namaKelas":
                this.namaKelas = value;
                break;
            case "idStudyProgram":
                this.idStudyProgram = value;
                break;
            case "idTahunAjaran":
                this.idTahunAjaran = value;
                break;
            default:
                throw new IllegalArgumentException("Invalid field name: " + fieldName);
        }
    }
}
