/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.doyatama.university.payload;

public class KelasRequest {
    private String idKelas;
    private String namaKelas;
    private String idStudyProgram;
    private String angkatan;

    public KelasRequest() {
    }

    public KelasRequest(String idKelas, String namaKelas, String idStudyProgram, String angkatan) {
        this.idKelas = idKelas;
        this.namaKelas = namaKelas;
        this.idStudyProgram = idStudyProgram;
        this.angkatan = angkatan;
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

    public String getAngkatan() {
        return angkatan;
    }

    public void setAngkatan(String angkatan) {
        this.angkatan = angkatan;
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
            case "angkatan":
                this.angkatan = value;
                break;
            default:
                throw new IllegalArgumentException("Invalid field name: " + fieldName);
        }
    }
}
