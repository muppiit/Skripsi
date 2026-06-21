package seeder2;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class ReferenceLearning2Seeder extends Seeder2Support {
    private static final TableName LEARNING_MEDIA = TableName.valueOf("learning_medias");
    private static final TableName FORM_LEARNING = TableName.valueOf("form_learnings");
    private static final TableName LEARNING_METHOD = TableName.valueOf("learning_methods");
    private static final TableName ASSESSMENT_CRITERIA = TableName.valueOf("assessment_criterias");
    private static final TableName APPRAISAL_FORM = TableName.valueOf("appraisal_forms");

    public void seed(HBaseCustomClient client) throws IOException {
        seedLearningMedia(client);
        seedFormLearning(client);
        seedLearningMethod(client);
        seedAssessmentCriteria(client);
        seedAppraisalForm(client);
    }

    private void seedLearningMedia(HBaseCustomClient client) throws IOException {
        main(client, LEARNING_MEDIA, "LM001", "id", "name", "LM001", "LMS", "Learning Management System");
        put(client, LEARNING_MEDIA, "LM001", "main", "type", "software");
        main(client, LEARNING_MEDIA, "LM002", "id", "name", "LM002", "Laptop", "Perangkat laptop dosen/mahasiswa");
        put(client, LEARNING_MEDIA, "LM002", "main", "type", "hardware");
        main(client, LEARNING_MEDIA, "LM003", "id", "name", "LM003", "Proyektor", "Media presentasi kelas");
        put(client, LEARNING_MEDIA, "LM003", "main", "type", "hardware");
    }

    private void seedFormLearning(HBaseCustomClient client) throws IOException {
        main(client, FORM_LEARNING, "FL001", "id", "name", "FL001", "Tatap Muka", "Pembelajaran luring di kelas");
        main(client, FORM_LEARNING, "FL002", "id", "name", "FL002", "Praktikum", "Pembelajaran praktik di laboratorium");
    }

    private void seedLearningMethod(HBaseCustomClient client) throws IOException {
        main(client, LEARNING_METHOD, "LMTH001", "id", "name", "LMTH001", "Project Based Learning",
                "Pembelajaran berbasis proyek");
        main(client, LEARNING_METHOD, "LMTH002", "id", "name", "LMTH002", "Problem Based Learning",
                "Pembelajaran berbasis masalah");
    }

    private void seedAssessmentCriteria(HBaseCustomClient client) throws IOException {
        main(client, ASSESSMENT_CRITERIA, "AC001", "id", "name", "AC001", "Ketepatan Jawaban",
                "Menilai ketepatan jawaban mahasiswa");
        main(client, ASSESSMENT_CRITERIA, "AC002", "id", "name", "AC002", "Kelengkapan Solusi",
                "Menilai kelengkapan solusi mahasiswa");
    }

    private void seedAppraisalForm(HBaseCustomClient client) throws IOException {
        main(client, APPRAISAL_FORM, "AF001", "id", "name", "AF001", "Tes Tulis", "Penilaian melalui tes tulis");
        main(client, APPRAISAL_FORM, "AF002", "id", "name", "AF002", "Rubrik Praktikum",
                "Penilaian praktik menggunakan rubrik");
    }
}
