package com.doyatama.university.payload;

public class StudentRequest {
    private String id;
    private String nisn;
    private String name;
    private String gender;
    private String phone;
    private String religion_id;
    private String idStudyProgram;
    private String idKelas;
    private String idTahunAjaran;
    private String birth_date;
    private String place_born;
    private String address;

    public StudentRequest() {
    }

    public StudentRequest(String id, String nisn, String name, String gender, String phone,
            String religion_id, String idStudyProgram, String idKelas, String idTahunAjaran,
            String birth_date, String place_born, String address) {
        this.id = id;
        this.nisn = nisn;
        this.name = name;
        this.gender = gender;
        this.phone = phone;
        this.religion_id = religion_id;
        this.idStudyProgram = idStudyProgram;
        this.idKelas = idKelas;
        this.idTahunAjaran = idTahunAjaran;
        this.birth_date = birth_date;
        this.place_born = place_born;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNisn() {
        return nisn;
    }

    public void setNisn(String nisn) {
        this.nisn = nisn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getReligion_id() {
        return religion_id;
    }

    public void setReligion_id(String religion_id) {
        this.religion_id = religion_id;
    }

    public String getIdStudyProgram() {
        return idStudyProgram;
    }

    public void setIdStudyProgram(String idStudyProgram) {
        this.idStudyProgram = idStudyProgram;
    }

    public String getIdKelas() {
        return idKelas;
    }

    public void setIdKelas(String idKelas) {
        this.idKelas = idKelas;
    }

    public String getIdTahunAjaran() {
        return idTahunAjaran;
    }

    public void setIdTahunAjaran(String idTahunAjaran) {
        this.idTahunAjaran = idTahunAjaran;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getPlace_born() {
        return place_born;
    }

    public void setPlace_born(String place_born) {
        this.place_born = place_born;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void set(String fieldName, String value) {
        switch (fieldName) {
            case "id":
                this.id = value;
                break;
            case "nisn":
                this.nisn = value;
                break;
            case "name":
                this.name = value;
                break;
            case "gender":
                this.gender = value;
                break;
            case "phone":
                this.phone = value;
                break;
            case "birth_date":
                this.birth_date = value;
                break;
            case "place_born":
                this.place_born = value;
                break;
            case "address":
                this.address = value;
                break;
            case "religion_id":
                this.religion_id = value;
                break;
            case "idStudyProgram":
                this.idStudyProgram = value;
                break;
            case "idKelas":
                this.idKelas = value;
                break;
            case "idTahunAjaran":
                this.idTahunAjaran = value;
                break;
            default:
                throw new IllegalArgumentException("Invalid field name: " + fieldName);
        }
    }
}
