package com.doyatama.university.repository;

import com.doyatama.university.helper.HBaseCustomClient;
import com.doyatama.university.model.Student;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentRepository {
    Configuration conf = HBaseConfiguration.create();
    String tableName = "students";

    private Map<String, String> baseMapping() {
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("id", "id");
        columnMapping.put("nisn", "nisn");
        columnMapping.put("name", "name");
        columnMapping.put("gender", "gender");
        columnMapping.put("phone", "phone");
        columnMapping.put("birth_date", "birth_date");
        columnMapping.put("place_born", "place_born");
        columnMapping.put("address", "address");
        columnMapping.put("religion", "religion");
        columnMapping.put("study_program", "study_program");
        columnMapping.put("kelas", "kelas");
        columnMapping.put("tahunAjaran", "tahunAjaran");
        return columnMapping;
    }

    public List<Student> findAll(int size) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        return client.showListTable(tableName, baseMapping(), Student.class, size);
    }

    public List<Student> findAllById(List<String> studentIds) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tableStudent = TableName.valueOf(tableName);

        List<Student> students = new ArrayList<>();
        for (String studentId : studentIds) {
            Student student = client.showDataTable(tableStudent.toString(), baseMapping(), studentId, Student.class);
            if (student != null) {
                students.add(student);
            }
        }
        return students;
    }

    public List<List<Student>> findAllById2D(List<List<String>> studentIdGroups) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tableStudent = TableName.valueOf(tableName);

        List<List<Student>> students2D = new ArrayList<>();
        for (List<String> studentIds : studentIdGroups) {
            List<Student> studentRow = new ArrayList<>();
            for (String studentId : studentIds) {
                Student student = client.showDataTable(tableStudent.toString(), baseMapping(), studentId, Student.class);
                if (student != null) {
                    studentRow.add(student);
                }
            }
            students2D.add(studentRow);
        }
        return students2D;
    }

    public Student save(Student student) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        String rowKey = student.getId();
        TableName tableStudent = TableName.valueOf(tableName);

        client.insertRecord(tableStudent, rowKey, "main", "id", rowKey);
        client.insertRecord(tableStudent, rowKey, "main", "nisn", student.getNisn());
        client.insertRecord(tableStudent, rowKey, "main", "name", student.getName());
        client.insertRecord(tableStudent, rowKey, "main", "gender", student.getGender().toString());
        client.insertRecord(tableStudent, rowKey, "main", "phone", student.getPhone());
        client.insertRecord(tableStudent, rowKey, "main", "birth_date", student.getBirth_date().toString());
        client.insertRecord(tableStudent, rowKey, "main", "place_born", student.getPlace_born().toString());
        client.insertRecord(tableStudent, rowKey, "main", "address", student.getAddress());

        client.insertRecord(tableStudent, rowKey, "religion", "id", student.getReligion().getId());
        client.insertRecord(tableStudent, rowKey, "religion", "name", student.getReligion().getName());

        client.insertRecord(tableStudent, rowKey, "study_program", "id", student.getStudyProgram().getId());
        client.insertRecord(tableStudent, rowKey, "study_program", "name", student.getStudyProgram().getName());

        client.insertRecord(tableStudent, rowKey, "kelas", "idKelas", student.getKelas().getIdKelas());
        client.insertRecord(tableStudent, rowKey, "kelas", "namaKelas", student.getKelas().getNamaKelas());

        if (student.getTahunAjaran() != null) {
            client.insertRecord(tableStudent, rowKey, "tahunAjaran", "idTahun", student.getTahunAjaran().getIdTahun());
            client.insertRecord(tableStudent, rowKey, "tahunAjaran", "tahunAjaran",
                    student.getTahunAjaran().getTahunAjaran());
        }

        client.insertRecord(tableStudent, rowKey, "detail", "created_by", "Doyatama");
        return student;
    }

    public Student findById(String studentId) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        return client.showDataTable(tableName, baseMapping(), studentId, Student.class);
    }

    public Student update(String studentId, Student student) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tableStudent = TableName.valueOf(tableName);

        client.insertRecord(tableStudent, studentId, "main", "nisn", student.getNisn());
        client.insertRecord(tableStudent, studentId, "main", "name", student.getName());
        client.insertRecord(tableStudent, studentId, "main", "gender", student.getGender().toString());
        client.insertRecord(tableStudent, studentId, "main", "phone", student.getPhone());
        client.insertRecord(tableStudent, studentId, "main", "birth_date", student.getBirth_date().toString());
        client.insertRecord(tableStudent, studentId, "main", "place_born", student.getPlace_born().toString());
        client.insertRecord(tableStudent, studentId, "main", "address", student.getAddress());

        client.insertRecord(tableStudent, studentId, "religion", "id", student.getReligion().getId());
        client.insertRecord(tableStudent, studentId, "religion", "name", student.getReligion().getName());

        client.insertRecord(tableStudent, studentId, "study_program", "id", student.getStudyProgram().getId());
        client.insertRecord(tableStudent, studentId, "study_program", "name", student.getStudyProgram().getName());

        client.insertRecord(tableStudent, studentId, "kelas", "idKelas", student.getKelas().getIdKelas());
        client.insertRecord(tableStudent, studentId, "kelas", "namaKelas", student.getKelas().getNamaKelas());

        if (student.getTahunAjaran() != null) {
            client.insertRecord(tableStudent, studentId, "tahunAjaran", "idTahun",
                    student.getTahunAjaran().getIdTahun());
            client.insertRecord(tableStudent, studentId, "tahunAjaran", "tahunAjaran",
                    student.getTahunAjaran().getTahunAjaran());
        }

        client.insertRecord(tableStudent, studentId, "detail", "created_by", "Doyatama");
        return student;
    }

    public boolean existsByUserID(String UID) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tableUsers = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("id", "id");

        Student student = client.getDataByColumn(tableUsers.toString(), columnMapping, "user", "id", UID,
                Student.class);
        return student != null && student.getId() != null;
    }

    public boolean deleteById(String studentId) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        client.deleteRecord(tableName, studentId);
        return true;
    }
}
