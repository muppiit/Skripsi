
package com.doyatama.university.repository;

import com.doyatama.university.helper.HBaseCustomClient;
import com.doyatama.university.model.Lecture;
import com.doyatama.university.model.StudyProgram;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.*;

public class LectureRepository {
    Configuration conf = HBaseConfiguration.create();
    String tableName = "lectures";

    public List<Lecture> findAll(int size) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableUsers = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        // Add the mappings to the HashMap
        columnMapping.put("id", "id");
        columnMapping.put("user_id", "user_id");
        columnMapping.put("nip", "nip");
        columnMapping.put("name", "name");
        columnMapping.put("place_born", "place_born");
        columnMapping.put("date_born", "date_born");
        columnMapping.put("gender", "gender");
        columnMapping.put("status", "status");
        columnMapping.put("address", "address");
        columnMapping.put("phone", "phone");
        columnMapping.put("religion", "religion");
        columnMapping.put("study_program", "study_program");
        return client.showListTable(tableUsers.toString(), columnMapping, Lecture.class, size);
    }

    public Lecture save(Lecture lecture) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        String rowKey = UUID.randomUUID().toString().substring(0, 5);

        TableName tableLecture = TableName.valueOf(tableName);
        client.insertRecord(tableLecture, rowKey, "main", "id", rowKey);
        if (lecture.getUser_id() != null) {
            client.insertRecord(tableLecture, rowKey, "main", "user_id", lecture.getUser_id());
        }
        client.insertRecord(tableLecture, rowKey, "main", "nip", lecture.getNip());
        client.insertRecord(tableLecture, rowKey, "main", "name", lecture.getName());
        client.insertRecord(tableLecture, rowKey, "main", "place_born", lecture.getPlace_born());
        client.insertRecord(tableLecture, rowKey, "main", "date_born", lecture.getDate_born());
        client.insertRecord(tableLecture, rowKey, "main", "gender", lecture.getGender());
        client.insertRecord(tableLecture, rowKey, "main", "status", lecture.getStatus());
        client.insertRecord(tableLecture, rowKey, "main", "phone", lecture.getPhone());
        client.insertRecord(tableLecture, rowKey, "main", "address", lecture.getAddress());
        client.insertRecord(tableLecture, rowKey, "religion", "id", lecture.getReligion().getId());
        client.insertRecord(tableLecture, rowKey, "religion", "name", lecture.getReligion().getName());
        client.insertRecord(tableLecture, rowKey, "study_program", "id", lecture.getStudyProgram().getId());
        client.insertRecord(tableLecture, rowKey, "study_program", "name", lecture.getStudyProgram().getName());
        client.insertRecord(tableLecture, rowKey, "detail", "created_by", "Doyatama");
        
        lecture.setId(rowKey);
        return lecture;
    }

    public Lecture findById(String lectureId) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableUsers = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        // Add the mappings to the HashMap
        columnMapping.put("id", "id");
        columnMapping.put("user_id", "user_id");
        columnMapping.put("nip", "nip");
        columnMapping.put("name", "name");
        columnMapping.put("place_born", "place_born");
        columnMapping.put("date_born", "date_born");
        columnMapping.put("gender", "gender");
        columnMapping.put("status", "status");
        columnMapping.put("address", "address");
        columnMapping.put("phone", "phone");
        columnMapping.put("religion", "religion");
        columnMapping.put("study_program", "study_program");

        return client.showDataTable(tableUsers.toString(), columnMapping, lectureId, Lecture.class);
    }

    public List<Lecture> findAllById(List<String> lectureIds) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName table = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        // Add the mappings to the HashMap
        columnMapping.put("id", "id");
        columnMapping.put("user_id", "user_id");
        columnMapping.put("nip", "nip");
        columnMapping.put("name", "name");
        columnMapping.put("place_born", "place_born");
        columnMapping.put("date_born", "date_born");
        columnMapping.put("gender", "gender");
        columnMapping.put("status", "status");
        columnMapping.put("address", "address");
        columnMapping.put("phone", "phone");
        columnMapping.put("religion", "religion");
        columnMapping.put("study_program", "study_program");

        List<Lecture> lectures = new ArrayList<>();
        for (String lectureId : lectureIds) {
            Lecture lecture = client.showDataTable(table.toString(), columnMapping, lectureId, Lecture.class);
            if (lecture != null) {
                lectures.add(lecture);
            }
        }

        return lectures;
    }

    public List<Lecture> findAllByIds(List<List<String>> lectureIdsList) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName table = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("id", "id");
        columnMapping.put("user_id", "user_id");
        columnMapping.put("nip", "nip");
        columnMapping.put("name", "name");
        columnMapping.put("place_born", "place_born");
        columnMapping.put("date_born", "date_born");
        columnMapping.put("gender", "gender");
        columnMapping.put("status", "status");
        columnMapping.put("address", "address");
        columnMapping.put("phone", "phone");
        columnMapping.put("religion", "religion");
        columnMapping.put("study_program", "study_program");

        List<Lecture> lectures = new ArrayList<>();

        // Iterate through each List<String> inside List<List<String>>
        for (List<String> lectureIds : lectureIdsList) {
            for (String lectureId : lectureIds) {
                Lecture lecture = client.showDataTable(table.toString(), columnMapping, lectureId, Lecture.class);
                if (lecture != null) {
                    lectures.add(lecture);
                }
            }
        }

        return lectures;
    }

    public List<Lecture> findRelationById(List<String> lectureIds) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName table = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        // Add the mappings to the HashMap
        columnMapping.put("id", "id");
        columnMapping.put("user_id", "user_id");
        columnMapping.put("nip", "nip");
        columnMapping.put("name", "name");
        columnMapping.put("place_born", "place_born");
        columnMapping.put("date_born", "date_born");
        columnMapping.put("gender", "gender");
        columnMapping.put("status", "status");
        columnMapping.put("address", "address");
        columnMapping.put("phone", "phone");
        columnMapping.put("religion", "religion");
        columnMapping.put("study_program", "study_program");

        List<Lecture> lectures = new ArrayList<>();
        for (String lectureId : lectureIds) {
            Lecture lecture = client.showDataTable(table.toString(), columnMapping, lectureId, Lecture.class);
            if (lecture != null) {
                lectures.add(lecture);
            }
        }

        return lectures;
    }

    public Lecture update(String lectureId, Lecture lecture) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableLecture = TableName.valueOf(tableName);
        if (lecture.getUser_id() != null) {
            client.insertRecord(tableLecture, lectureId, "main", "user_id", lecture.getUser_id());
        }
        client.insertRecord(tableLecture, lectureId, "main", "nip", lecture.getNip());
        client.insertRecord(tableLecture, lectureId, "main", "name", lecture.getName());
        client.insertRecord(tableLecture, lectureId, "main", "place_born", lecture.getPlace_born());
        client.insertRecord(tableLecture, lectureId, "main", "date_born", lecture.getDate_born());
        client.insertRecord(tableLecture, lectureId, "main", "gender", lecture.getGender());
        client.insertRecord(tableLecture, lectureId, "main", "status", lecture.getStatus());
        client.insertRecord(tableLecture, lectureId, "main", "phone", lecture.getPhone());
        client.insertRecord(tableLecture, lectureId, "main", "address", lecture.getAddress());
        client.insertRecord(tableLecture, lectureId, "religion", "id", lecture.getReligion().getId());
        client.insertRecord(tableLecture, lectureId, "religion", "name", lecture.getReligion().getName());
        client.insertRecord(tableLecture, lectureId, "study_program", "id", lecture.getStudyProgram().getId());
        client.insertRecord(tableLecture, lectureId, "study_program", "name", lecture.getStudyProgram().getName());
        
        lecture.setId(lectureId);
        return lecture;
    }

    public Lecture findByUserId(String userId) throws IOException {
        if (userId == null || userId.trim().isEmpty()) {
            return new Lecture();
        }

        HBaseCustomClient client = new HBaseCustomClient(conf);
        Scan scan = new Scan();
        SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes("main"), Bytes.toBytes("user_id"),
                CompareOperator.EQUAL, Bytes.toBytes(userId));
        filter.setFilterIfMissing(true);
        scan.setFilter(filter);

        ResultScanner scanner = client.getTable(tableName).getScanner(scan);
        try {
            for (Result result : scanner) {
                return findById(Bytes.toString(result.getRow()));
            }
        } finally {
            scanner.close();
        }

        return new Lecture();
    }

    public void updateStudyProgramByUserId(String userId, StudyProgram studyProgram) throws IOException {
        Lecture lecture = findByUserId(userId);
        if (lecture == null || lecture.getId() == null || studyProgram == null || studyProgram.getId() == null) {
            return;
        }

        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tableLecture = TableName.valueOf(tableName);
        client.insertRecord(tableLecture, lecture.getId(), "study_program", "id", studyProgram.getId());
        client.insertRecord(tableLecture, lecture.getId(), "study_program", "name", studyProgram.getName());
    }

    public boolean deleteById(String lectureId) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        client.deleteRecord("lectures", lectureId);
        return true;
    }
}
