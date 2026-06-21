package seeder2;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

public class Seeder2Runner {
    public static void main(String[] args) throws Exception {
        Configuration conf = HBaseConfiguration.create();
        HBaseCustomClient client = new HBaseCustomClient(conf);

        new Department2Seeder().seed(client);
        new StudyProgram2Seeder().seed(client);
        new SubjectGroup2Seeder().seed(client);
        new Religion2Seeder().seed(client);
        new Academic2Seeder().seed(client);
        new User2Seeder().seed(client);
        new Lecture2Seeder().seed(client);
        new Student2Seeder().seed(client);
        new Season2Seeder().seed(client);
        new ReferenceLearning2Seeder().seed(client);
        new Rps2Seeder().seed(client);
        new RpsDetail2Seeder().seed(client);

        System.out.println("Seeder2 selesai: master, akademik, user, dosen, mahasiswa, kelas ajaran, RPS, dan detail RPS berhasil dibuat.");
    }
}
