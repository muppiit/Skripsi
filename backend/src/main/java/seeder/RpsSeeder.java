package seeder;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class RpsSeeder {

        private static final TableName TABLE_RPS = TableName.valueOf("rps");

        public void seed(HBaseCustomClient client) throws IOException {
                seedOne(client, "RPS001", "Pemrograman Dasar", "2", "1", "CP1", "CP1-1", "SUB001", "SP001",
                                "Teknik Informatika", "LEC001", "Dosen TI Polinema Lumajang");
                seedOne(client, "RPS002", "Algoritma dan Struktur Data", "3", "2", "CP2", "CP2-1", "SUB002", "SP001",
                                "Teknik Informatika", "LEC002", "Ratna Wulandari");
                seedOne(client, "RPS003", "Praktikum Pemrograman", "2", "3", "CP3", "CP3-1", "SUB001", "SP001",
                                "Teknik Informatika", "LEC001", "Dosen TI Polinema Lumajang");
                seedOne(client, "RPS004", "Pemrograman Berorientasi Objek", "3", "4", "CP4", "CP4-1", "SUB002",
                                "SP001", "Teknik Informatika", "LEC002", "Ratna Wulandari");
                seedOne(client, "RPS009", "Jaringan Komputer Dasar", "3", "5", "CP9", "CP9-1", "SUB001",
                                "SP001", "Teknik Informatika", "LEC001", "Dosen TI Polinema Lumajang");
                seedOne(client, "RPS010", "Desain Antarmuka", "2", "6", "CP10", "CP10-1", "SUB002",
                                "SP001", "Teknik Informatika", "LEC002", "Ratna Wulandari");

                seedOne(client, "RPS005", "Sistem Informasi Manajemen", "3", "1", "CP5", "CP5-1", "SUB003", "SP002",
                                "Sistem Informasi Bisnis", "LEC003", "Fajar Hidayat");
                seedOne(client, "RPS006", "Analitik Data Bisnis", "2", "2", "CP6", "CP6-1", "SUB004", "SP002",
                                "Sistem Informasi Bisnis", "LEC004", "Sari Ayu");
                seedOne(client, "RPS007", "Perancangan Proses Bisnis", "3", "3", "CP7", "CP7-1", "SUB003", "SP002",
                                "Sistem Informasi Bisnis", "LEC003", "Fajar Hidayat");
                seedOne(client, "RPS008", "Visualisasi Data", "2", "4", "CP8", "CP8-1", "SUB004", "SP002",
                                "Sistem Informasi Bisnis", "LEC004", "Sari Ayu");
                seedOne(client, "RPS011", "Kecerdasan Bisnis", "3", "5", "CP11", "CP11-1", "SUB003",
                                "SP002", "Sistem Informasi Bisnis", "LEC003", "Fajar Hidayat");
                seedOne(client, "RPS012", "Manajemen Proyek TI", "2", "6", "CP12", "CP12-1", "SUB004",
                                "SP002", "Sistem Informasi Bisnis", "LEC004", "Sari Ayu");
                seedOne(client, "RPS013", "Keamanan Jaringan", "3", "7", "CP13", "CP13-1", "SUB001",
                                "SP001", "Teknik Informatika", "LEC001", "Dosen TI Polinema Lumajang");
                seedOne(client, "RPS014", "Interaksi Manusia Komputer", "2", "8", "CP14", "CP14-1", "SUB002",
                                "SP001", "Teknik Informatika", "LEC002", "Ratna Wulandari");
                seedOne(client, "RPS015", "Sistem Pendukung Keputusan", "3", "7", "CP15", "CP15-1", "SUB003",
                                "SP002", "Sistem Informasi Bisnis", "LEC003", "Fajar Hidayat");
                seedOne(client, "RPS016", "Manajemen Basis Data Bisnis", "2", "8", "CP16", "CP16-1", "SUB004",
                                "SP002", "Sistem Informasi Bisnis", "LEC004", "Sari Ayu");
        }

        private void seedOne(HBaseCustomClient client, String id, String name, String sks, String semester,
                        String cplProdi, String cplMk, String subjectId, String studyProgramId, String studyProgramName,
                        String lectureId, String lectureName)
                        throws IOException {
                client.insertRecord(TABLE_RPS, id, "main", "id", id);
                client.insertRecord(TABLE_RPS, id, "main", "name", name);
                client.insertRecord(TABLE_RPS, id, "main", "sks", sks);
                client.insertRecord(TABLE_RPS, id, "main", "semester", semester);
                client.insertRecord(TABLE_RPS, id, "main", "cpl_prodi", cplProdi);
                client.insertRecord(TABLE_RPS, id, "main", "cpl_mk", cplMk);

                client.insertRecord(TABLE_RPS, id, "learning_media_softwares", "soft_0",
                                "{\"id\":\"LM001\",\"name\":\"LMS\"}");
                client.insertRecord(TABLE_RPS, id, "learning_media_hardwares", "hard_0",
                                "{\"id\":\"LM002\",\"name\":\"Projector\"}");

                client.insertRecord(TABLE_RPS, id, "study_program", "id", studyProgramId);
                client.insertRecord(TABLE_RPS, id, "study_program", "name", studyProgramName);

                client.insertRecord(TABLE_RPS, id, "subject", "id", subjectId);
                client.insertRecord(TABLE_RPS, id, "subject", "name", name);

                client.insertRecord(TABLE_RPS, id, "dev_lecturers", "lecture_0",
                                "{\"id\":\"" + lectureId + "\",\"name\":\"" + lectureName + "\"}");
                client.insertRecord(TABLE_RPS, id, "teaching_lecturers", "lecture_0",
                                "{\"id\":\"" + lectureId + "\",\"name\":\"" + lectureName + "\"}");
                client.insertRecord(TABLE_RPS, id, "coordinator_lecturers", "lecture_0",
                                "{\"id\":\"" + lectureId + "\",\"name\":\"" + lectureName + "\"}");

                client.insertRecord(TABLE_RPS, id, "ka_study_program", "id", lectureId);
                client.insertRecord(TABLE_RPS, id, "ka_study_program", "name", lectureName);

                client.insertRecord(TABLE_RPS, id, "detail", "created_by", "Seeder");
        }
}
