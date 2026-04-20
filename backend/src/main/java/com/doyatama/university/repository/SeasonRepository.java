package com.doyatama.university.repository;

import com.doyatama.university.helper.HBaseCustomClient;
import com.doyatama.university.model.Season;
import com.doyatama.university.model.Student;
import com.doyatama.university.model.Subject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.springframework.stereotype.Repository;

@Repository
public class SeasonRepository {
    Configuration conf = HBaseConfiguration.create();
    String tableName = "seasons";

    public List<Season> findAll(int size) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableSeason = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        // Add the mappings to the HashMap
        columnMapping.put("idSeason", "idSeason");
        columnMapping.put("study_program", "study_program");
        columnMapping.put("kelas", "kelas");
        columnMapping.put("semester", "semester");
        columnMapping.put("lecture", "lecture");
        columnMapping.put("tahunAjaran", "tahunAjaran");
        columnMapping.put("student", "student");
        columnMapping.put("subject", "subject");

        return client.showListTable(tableSeason.toString(), columnMapping, Season.class, size);
    }

    public Season save(Season season) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        String rowKey = UUID.randomUUID().toString();
        TableName tableSeason = TableName.valueOf(tableName);
        client.insertRecord(tableSeason, rowKey, "main", "idSeason", rowKey);
        client.insertRecord(tableSeason, rowKey, "study_program", "id", season.getStudyProgram().getId());
        client.insertRecord(tableSeason, rowKey, "study_program", "name", season.getStudyProgram().getName());
        client.insertRecord(tableSeason, rowKey, "kelas", "idKelas", season.getKelas().getIdKelas());
        client.insertRecord(tableSeason, rowKey, "kelas", "namaKelas", season.getKelas().getNamaKelas());
        client.insertRecord(tableSeason, rowKey, "tahunAjaran", "idTahun", season.getTahunAjaran().getIdTahun());
        client.insertRecord(tableSeason, rowKey, "tahunAjaran", "tahunAjaran",
                season.getTahunAjaran().getTahunAjaran());
        client.insertRecord(tableSeason, rowKey, "semester", "idSemester", season.getSemester().getIdSemester());
        client.insertRecord(tableSeason, rowKey, "semester", "namaSemester", season.getSemester().getNamaSemester());
        client.insertRecord(tableSeason, rowKey, "lecture", "id", season.getLecture().getId());
        client.insertRecord(tableSeason, rowKey, "lecture", "name", season.getLecture().getName());
        client.insertRecord(tableSeason, rowKey, "lecture", "nip", season.getLecture().getNip());

        if (season.getStudent() != null && !season.getStudent().isEmpty()) {
            int index = 0;
            for (Student stud : season.getStudent()) {
                if (stud != null) {
                    try {
                        client.insertRecord(tableSeason, rowKey, "student", "id" + index, stud.getId());
                        client.insertRecord(tableSeason, rowKey, "student", "nisn" + index, stud.getNisn());
                        client.insertRecord(tableSeason, rowKey, "student", "name" + index, stud.getName());
                        client.insertRecord(tableSeason, rowKey, "student", "gender" + index, stud.getGender());
                        client.insertRecord(tableSeason, rowKey, "student", "phone" + index, stud.getPhone());
                        index++;
                    } catch (Exception e) {
                        System.err.println("Error inserting student: " + e.getMessage());
                    }
                }
            }
        }

        if (season.getSubject() != null && !season.getSubject().isEmpty()) {
            int index = 0;
            for (Subject subject : season.getSubject()) {
                if (subject != null) {
                    try {
                        client.insertRecord(tableSeason, rowKey, "subject", "id" + index, subject.getId());
                        client.insertRecord(tableSeason, rowKey, "subject", "name" + index, subject.getName());
                        index++;
                    } catch (Exception e) {
                        System.err.println("Error inserting subject: " + e.getMessage());
                    }
                }
            }
        }

        client.insertRecord(tableSeason, rowKey, "detail", "created_by", "Doyatama");
        return season;
    }

    public Season findById(String seasonId) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableSeason = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        // Add the mappings to the HashMap
        columnMapping.put("idSeason", "idSeason");
        columnMapping.put("study_program", "study_program");
        columnMapping.put("kelas", "kelas");
        columnMapping.put("semester", "semester");
        columnMapping.put("lecture", "lecture");
        columnMapping.put("tahunAjaran", "tahunAjaran");
        columnMapping.put("student", "student");
        columnMapping.put("subject", "subject");

        return client.showDataTable(tableSeason.toString(), columnMapping, seasonId, Season.class);
    }

    public List<Season> findAllById(List<String> seasonIds) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableSeason = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("idSeason", "idSeason");
        columnMapping.put("study_program", "study_program");
        columnMapping.put("kelas", "kelas");
        columnMapping.put("semester", "semester");
        columnMapping.put("lecture", "lecture");
        columnMapping.put("tahunAjaran", "tahunAjaran");
        columnMapping.put("student", "student");
        columnMapping.put("subject", "subject");

        List<Season> seasons = new ArrayList<>();
        for (String seasonId : seasonIds) {
            Season season = client.showDataTable(tableSeason.toString(), columnMapping, seasonId, Season.class);
            if (season != null) {
                seasons.add(season);
            }
        }

        return seasons;
    }

    public boolean deleteById(String seasonId) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        client.deleteRecord(tableName, seasonId);
        return true;
    }
}
