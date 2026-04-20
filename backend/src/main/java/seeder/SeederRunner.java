package seeder;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

public class SeederRunner {

    public static void main(String[] args) throws Exception {
        String mode = args != null && args.length > 0 ? args[0].toLowerCase() : "all";

        Configuration conf = HBaseConfiguration.create();
        HBaseCustomClient client = new HBaseCustomClient(conf);

        switch (mode) {
            case "user":
                new UserSeeder().seed(client);
                System.out.println("Seeder user selesai");
                break;
            case "study-program":
                new StudyProgramSeeder().seed(client);
                System.out.println("Seeder study program selesai");
                break;
            case "master":
                seedMasterData(client);
                System.out.println("Seeder master selesai");
                break;
            case "academic":
                new TahunAjaranSeeder().seed(client);
                new SemesterSeeder().seed(client);
                new KelasSeeder().seed(client);
                new AcademicSeeder().seed(client);
                System.out.println("Seeder academic selesai");
                break;
            case "season":
                new StudentSeeder().seed(client);
                new SeasonSeeder().seed(client);
                System.out.println("Seeder student + season selesai");
                break;
            case "rps":
                new RpsSeeder().seed(client);
                new RpsDetailSeeder().seed(client);
                System.out.println("Seeder RPS dan RPS Detail selesai");
                break;
            case "exam":
                new SoalUjianSeeder().seed(client);
                new BankSoalSeeder().seed(client);
                System.out.println("Seeder Soal Ujian dan Bank Soal selesai");
                break;
            case "all":
                seedMasterData(client);
                new UserSeeder().seed(client);
                new RpsSeeder().seed(client);
                new RpsDetailSeeder().seed(client);
                new SoalUjianSeeder().seed(client);
                new BankSoalSeeder().seed(client);
                System.out.println("Seeder all selesai");
                break;
            default:
                System.out.println("Mode tidak dikenal: " + mode);
                System.out.println("Gunakan: user | study-program | master | academic | season | rps | exam | all");
                break;
        }
    }

    private static void seedMasterData(HBaseCustomClient client) throws Exception {
        new DepartmentSeeder().seed(client);
        new StudyProgramSeeder().seed(client);
        new SubjectGroupSeeder().seed(client);
        new SubjectSeeder().seed(client);
        new TahunAjaranSeeder().seed(client);
        new SemesterSeeder().seed(client);
        new KelasSeeder().seed(client);
        new AcademicSeeder().seed(client);
        new ReferenceDataSeeder().seed(client);
        new LectureSeeder().seed(client);
        new StudentSeeder().seed(client);
        new SeasonSeeder().seed(client);
    }
}
