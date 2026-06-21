package seeder2;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class Rps2Seeder extends Seeder2Support {
    private static final TableName TABLE = TableName.valueOf("rps");

    public void seed(HBaseCustomClient client) throws IOException {
        seedOne(client, "RPS001", "Pemrograman Dasar", "3", "1", "SUB001", SP_TI_ID, SP_TI_NAME, "LEC001",
                "Budi Raharjo");
        seedOne(client, "RPS002", "Algoritma dan Struktur Data", "3", "1", "SUB002", SP_TI_ID, SP_TI_NAME,
                "LEC002", "Ratna Wulandari");
        seedOne(client, "RPS003", "Sistem Informasi Manajemen", "3", "1", "SUB003", SP_SIB_ID, SP_SIB_NAME,
                "LEC003", "Fajar Hidayat");
        seedOne(client, "RPS004", "Analitik Data Bisnis", "3", "1", "SUB004", SP_SIB_ID, SP_SIB_NAME, "LEC004",
                "Sari Ayu");
    }

    private void seedOne(HBaseCustomClient client, String id, String name, String sks, String semester,
            String subjectId, String spId, String spName, String lectureId, String lectureName) throws IOException {
        put(client, TABLE, id, "main", "id", id);
        put(client, TABLE, id, "main", "name", name);
        put(client, TABLE, id, "main", "sks", sks);
        put(client, TABLE, id, "main", "semester", semester);
        put(client, TABLE, id, "main", "cpl_prodi", "Mahasiswa mampu memahami konsep dasar " + name);
        put(client, TABLE, id, "main", "cpl_mk", "Mahasiswa mampu menerapkan materi " + name);

        put(client, TABLE, id, "learning_media_softwares", "soft_0", json("LM001", "LMS"));
        put(client, TABLE, id, "learning_media_hardwares", "hard_0", json("LM002", "Laptop"));
        put(client, TABLE, id, "learning_media_hardwares", "hard_1", json("LM003", "Proyektor"));
        put(client, TABLE, id, "requirement_subjects", "req_0", json(subjectId, name));

        studyProgram(client, TABLE, id, "study_program", spId, spName);
        put(client, TABLE, id, "subject", "id", subjectId);
        put(client, TABLE, id, "subject", "name", name);
        put(client, TABLE, id, "dev_lecturers", "lecture_0", json(lectureId, lectureName));
        put(client, TABLE, id, "teaching_lecturers", "lecture_0", json(lectureId, lectureName));
        put(client, TABLE, id, "coordinator_lecturers", "lecture_0", json(lectureId, lectureName));
        put(client, TABLE, id, "ka_study_program", "id", lectureId);
        put(client, TABLE, id, "ka_study_program", "name", lectureName);
        put(client, TABLE, id, "detail", "created_by", CREATED_BY);
    }
}
