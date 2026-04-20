package seeder;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class SemesterSeeder {

    private static final TableName TABLE_SEMESTER = TableName.valueOf("semester");

    public void seed(HBaseCustomClient client) throws IOException {
        seedOne(client, "SMT001", "Semester 1", "SP001", "Teknik Informatika");
        seedOne(client, "SMT002", "Semester 2", "SP001", "Teknik Informatika");
        seedOne(client, "SMT003", "Semester 1", "SP002", "Sistem Informasi Bisnis");
        seedOne(client, "SMT004", "Semester 2", "SP002", "Sistem Informasi Bisnis");
    }

    private void seedOne(HBaseCustomClient client, String idSemester, String namaSemester,
            String studyProgramId, String studyProgramName) throws IOException {
        client.insertRecord(TABLE_SEMESTER, idSemester, "main", "idSemester", idSemester);
        client.insertRecord(TABLE_SEMESTER, idSemester, "main", "namaSemester", namaSemester);
        client.insertRecord(TABLE_SEMESTER, idSemester, "study_program", "idSchool", studyProgramId);
        client.insertRecord(TABLE_SEMESTER, idSemester, "study_program", "nameSchool", studyProgramName);
        client.insertRecord(TABLE_SEMESTER, idSemester, "detail", "created_by", "Seeder");
    }
}
