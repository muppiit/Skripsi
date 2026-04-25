
package com.doyatama.university.repository;

import com.doyatama.university.helper.HBaseCustomClient;
import com.doyatama.university.model.Kelas;
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
public class KelasRepository {
    Configuration conf = HBaseConfiguration.create();
    String tableName = "kelas";

    private Map<String, String> baseMapping() {
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("idKelas", "idKelas");
        columnMapping.put("namaKelas", "namaKelas");
        columnMapping.put("study_program", "study_program");
        columnMapping.put("tahunAjaran", "tahunAjaran");
        return columnMapping;
    }

    public List<Kelas> findAll(int size) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        return client.showListTable(tableName, baseMapping(), Kelas.class, size);
    }

    public Kelas save(Kelas kelas) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        String rowKey = kelas.getIdKelas();
        TableName tableKelas = TableName.valueOf(tableName);

        client.insertRecord(tableKelas, rowKey, "main", "idKelas", rowKey);
        client.insertRecord(tableKelas, rowKey, "main", "namaKelas", kelas.getNamaKelas());

        client.insertRecord(tableKelas, rowKey, "study_program", "id", kelas.getStudyProgram().getId());
        client.insertRecord(tableKelas, rowKey, "study_program", "name", kelas.getStudyProgram().getName());

        client.insertRecord(tableKelas, rowKey, "tahunAjaran", "idTahun", kelas.getTahunAjaran().getIdTahun());
        client.insertRecord(tableKelas, rowKey, "tahunAjaran", "tahunAjaran", kelas.getTahunAjaran().getTahunAjaran());

        client.insertRecord(tableKelas, rowKey, "detail", "created_by", "Doyatama");
        return kelas;
    }

    public Kelas findById(String kelasId) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        return client.showDataTable(tableName, baseMapping(), kelasId, Kelas.class);
    }

    public List<Kelas> findAllById(List<String> kelasIds) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        List<Kelas> kelass = new ArrayList<>();
        for (String kelasId : kelasIds) {
            Kelas kelas = client.showDataTable(tableName, baseMapping(), kelasId, Kelas.class);
            if (kelas != null) {
                kelass.add(kelas);
            }
        }
        return kelass;
    }

    public List<Kelas> findKelasByStudyProgram(String studyProgramId, int size) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        return client.getDataListByColumn(tableName, baseMapping(), "study_program", "id",
                studyProgramId, Kelas.class, size);
    }

    public Kelas update(String kelasId, Kelas kelas) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);

        TableName tableKelas = TableName.valueOf(tableName);

        if (kelas.getNamaKelas() != null) {
            client.insertRecord(tableKelas, kelasId, "main", "namaKelas", kelas.getNamaKelas());
        }

        if (kelas.getStudyProgram() != null && kelas.getStudyProgram().getId() != null) {
            client.insertRecord(tableKelas, kelasId, "study_program", "id", kelas.getStudyProgram().getId());
        }

        if (kelas.getStudyProgram() != null && kelas.getStudyProgram().getName() != null) {
            client.insertRecord(tableKelas, kelasId, "study_program", "name", kelas.getStudyProgram().getName());
        }

        if (kelas.getTahunAjaran() != null && kelas.getTahunAjaran().getIdTahun() != null) {
            client.insertRecord(tableKelas, kelasId, "tahunAjaran", "idTahun", kelas.getTahunAjaran().getIdTahun());
            client.insertRecord(tableKelas, kelasId, "tahunAjaran", "tahunAjaran", kelas.getTahunAjaran().getTahunAjaran());
        }

        return kelas;
    }

    public boolean deleteById(String kelasId) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        client.deleteRecord(tableName, kelasId);
        return true;
    }

    public boolean existsById(String kelasId) throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tableKelas = TableName.valueOf(tableName);
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("idKelas", "idKelas");

        Kelas kelas = client.getDataByColumn(tableKelas.toString(), columnMapping, "main", "idKelas",
                kelasId, Kelas.class);
        return kelas != null && kelas.getIdKelas() != null;
    }
}
