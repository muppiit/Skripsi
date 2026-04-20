package seeder;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class LectureSeeder {

    private static final TableName TABLE_LECTURE = TableName.valueOf("lectures");

    public void seed(HBaseCustomClient client) throws IOException {
        client.insertRecord(TABLE_LECTURE, "LEC001", "main", "id", "LEC001");
        client.insertRecord(TABLE_LECTURE, "LEC001", "main", "nip", "19880001");
        client.insertRecord(TABLE_LECTURE, "LEC001", "main", "name", "Dosen Pengampu");
        client.insertRecord(TABLE_LECTURE, "LEC001", "main", "place_born", "Lumajang");
        client.insertRecord(TABLE_LECTURE, "LEC001", "main", "date_born", "1988-01-01");
        client.insertRecord(TABLE_LECTURE, "LEC001", "main", "gender", "L");
        client.insertRecord(TABLE_LECTURE, "LEC001", "main", "status", "Aktif");
        client.insertRecord(TABLE_LECTURE, "LEC001", "main", "phone", "081234567890");
        client.insertRecord(TABLE_LECTURE, "LEC001", "main", "address", "Jl. Raya Kampus No. 1");
        client.insertRecord(TABLE_LECTURE, "LEC001", "religion", "id", "R001");
        client.insertRecord(TABLE_LECTURE, "LEC001", "religion", "name", "Islam");
        client.insertRecord(TABLE_LECTURE, "LEC001", "study_program", "id", "SP001");
        client.insertRecord(TABLE_LECTURE, "LEC001", "study_program", "name", "Teknik Informatika");
        client.insertRecord(TABLE_LECTURE, "LEC001", "detail", "created_by", "Seeder");
    }
}
