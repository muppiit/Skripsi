
package com.doyatama.university.repository;

import com.doyatama.university.helper.HBaseCustomClient;
import com.doyatama.university.model.TahunAjaran;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.springframework.stereotype.Repository;

@Repository
public class TahunAjaranRepository {
    Configuration conf = HBaseConfiguration.create();
    String tableName = "tahunAjaran";

    public List<TahunAjaran> findAll(int size) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableTahun = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        // Add the mappings to the HashMap
        columnMapping.put("idTahun", "idTahun");
        columnMapping.put("tahunAjaran", "tahunAjaran");
        columnMapping.put("study_program", "study_program");
        return client.showListTable(tableTahun.toString(), columnMapping, TahunAjaran.class, size);
    }

    public TahunAjaran save(TahunAjaran tahun) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        String rowKey = tahun.getIdTahun();
        TableName tableTahun = TableName.valueOf(tableName);
        client.insertRecord(tableTahun, rowKey, "main", "idTahun", rowKey);
        client.insertRecord(tableTahun, rowKey, "main", "tahunAjaran", tahun.getTahunAjaran());

        client.insertRecord(tableTahun, rowKey, "study_program", "idSchool", tahun.getStudyProgram().getIdSchool());
        client.insertRecord(tableTahun, rowKey, "study_program", "nameSchool", tahun.getStudyProgram().getNameSchool());

        client.insertRecord(tableTahun, rowKey, "detail", "created_by", "Doyatama");
        return tahun;
    }

    public TahunAjaran findTahunById(String tahunId) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableTahun = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        // Add the mappings to the HashMap
        columnMapping.put("idTahun", "idTahun");
        columnMapping.put("tahunAjaran", "tahunAjaran");
        columnMapping.put("study_program", "study_program");

        return client.showDataTable(tableTahun.toString(), columnMapping, tahunId, TahunAjaran.class);
    }

    public TahunAjaran findById(String tahunId) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableTahun = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        // Add the mappings to the HashMap
        columnMapping.put("idTahun", "idTahun");
        columnMapping.put("tahunAjaran", "tahunAjaran");
        columnMapping.put("study_program", "study_program");

        return client.showDataTable(tableTahun.toString(), columnMapping, tahunId, TahunAjaran.class);
    }

    public List<TahunAjaran> findAllById(List<String> tahunIds) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableTahun = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("idTahun", "idTahun");
        columnMapping.put("tahunAjaran", "tahunAjaran");

        List<TahunAjaran> tahuns = new ArrayList<>();
        for (String tahunId : tahunIds) {
            TahunAjaran tahun = client.showDataTable(tableTahun.toString(), columnMapping, tahunId, TahunAjaran.class);
            if (tahun != null) {
                tahuns.add(tahun);
            }
        }

        return tahuns;
    }

    public List<TahunAjaran> findTahunAjaranByStudyProgram(String studyProgramId, int size) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableTahun = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        // Add the mappings to the HashMap
        columnMapping.put("idTahun", "idTahun");
        columnMapping.put("tahunAjaran", "tahunAjaran");
        columnMapping.put("study_program", "study_program");

        List<TahunAjaran> tahun = client.getDataListByColumn(tableTahun.toString(), columnMapping, "study_program",
                "idSchool", studyProgramId, TahunAjaran.class, size);

        return tahun;
    }

    public TahunAjaran update(String tahunId, TahunAjaran tahun) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableTahun = TableName.valueOf(tableName);

        if (tahun.getTahunAjaran() != null) {
            client.insertRecord(tableTahun, tahunId, "main", "tahunAjaran", tahun.getTahunAjaran());
        }

        if (tahun.getStudyProgram() != null) {
            client.insertRecord(tableTahun, tahunId, "study_program", "idSchool", tahun.getStudyProgram().getIdSchool());
            client.insertRecord(tableTahun, tahunId, "study_program", "nameSchool", tahun.getStudyProgram().getNameSchool());
        }

        client.insertRecord(tableTahun, tahunId, "detail", "created_by", "Doyatama");
        return tahun;
    }

    public boolean deleteById(String tahunId) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        client.deleteRecord(tableName, tahunId);
        return true;
    }

    public boolean existsById(String tahunId) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tableTahun = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();

        columnMapping.put("idTahun", "idTahun");

        TahunAjaran tahun = client.getDataByColumn(tableTahun.toString(), columnMapping, "main", "idTahun", tahunId,
                TahunAjaran.class);

        return tahun.getIdTahun() != null;
    }
}
