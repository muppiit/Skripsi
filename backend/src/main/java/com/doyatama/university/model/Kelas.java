/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.doyatama.university.model;

public class Kelas {
    private String idKelas;
    private String namaKelas;
    private String angkatan;
    private StudyProgram study_program;

    public Kelas() {
    }

    public Kelas(String idKelas, String namaKelas, String angkatan, StudyProgram study_program) {
        this.idKelas = idKelas;
        this.namaKelas = namaKelas;
        this.angkatan = angkatan;
        this.study_program = study_program;
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

    public String getAngkatan() {
        return angkatan;
    }

    public void setAngkatan(String angkatan) {
        this.angkatan = angkatan;
    }

    public StudyProgram getStudyProgram() {
        return study_program;
    }

    public void setStudyProgram(StudyProgram study_program) {
        this.study_program = study_program;
    }

    public boolean isValid() {
        return this.idKelas != null && this.namaKelas != null && this.angkatan != null && this.study_program != null;
    }

    public void set(String fieldName, String value) {
        switch (fieldName) {
            case "idKelas":
                this.idKelas = value;
                break;
            case "namaKelas":
                this.namaKelas = value;
                break;
            case "angkatan":
                this.angkatan = value;
                break;
            default:
                throw new IllegalArgumentException("Invalid field name: " + fieldName);
        }
    }
}
