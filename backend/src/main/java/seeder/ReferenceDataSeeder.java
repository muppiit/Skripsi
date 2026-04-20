package seeder;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class ReferenceDataSeeder {

    private static final TableName TABLE_LEARNING_MEDIA = TableName.valueOf("learning_medias");
    private static final TableName TABLE_FORM_LEARNING = TableName.valueOf("form_learnings");
    private static final TableName TABLE_LEARNING_METHOD = TableName.valueOf("learning_methods");
    private static final TableName TABLE_APPRAISAL_FORM = TableName.valueOf("appraisal_forms");
    private static final TableName TABLE_ASSESSMENT_CRITERIA = TableName.valueOf("assessment_criterias");

    public void seed(HBaseCustomClient client) throws IOException {
        seedLearningMedia(client);
        seedFormLearning(client);
        seedLearningMethod(client);
        seedAppraisalForm(client);
        seedAssessmentCriteria(client);
    }

    private void seedLearningMedia(HBaseCustomClient client) throws IOException {
        client.insertRecord(TABLE_LEARNING_MEDIA, "LM001", "main", "id", "LM001");
        client.insertRecord(TABLE_LEARNING_MEDIA, "LM001", "main", "name", "LMS");
        client.insertRecord(TABLE_LEARNING_MEDIA, "LM001", "main", "description", "Learning Management System");
        client.insertRecord(TABLE_LEARNING_MEDIA, "LM001", "main", "type", "software");
        client.insertRecord(TABLE_LEARNING_MEDIA, "LM001", "detail", "created_by", "Seeder");

        client.insertRecord(TABLE_LEARNING_MEDIA, "LM002", "main", "id", "LM002");
        client.insertRecord(TABLE_LEARNING_MEDIA, "LM002", "main", "name", "Projector");
        client.insertRecord(TABLE_LEARNING_MEDIA, "LM002", "main", "description", "Media visual kelas");
        client.insertRecord(TABLE_LEARNING_MEDIA, "LM002", "main", "type", "hardware");
        client.insertRecord(TABLE_LEARNING_MEDIA, "LM002", "detail", "created_by", "Seeder");
    }

    private void seedFormLearning(HBaseCustomClient client) throws IOException {
        client.insertRecord(TABLE_FORM_LEARNING, "FL001", "main", "id", "FL001");
        client.insertRecord(TABLE_FORM_LEARNING, "FL001", "main", "name", "Tatap Muka");
        client.insertRecord(TABLE_FORM_LEARNING, "FL001", "main", "description", "Pembelajaran luring");
        client.insertRecord(TABLE_FORM_LEARNING, "FL001", "detail", "created_by", "Seeder");
    }

    private void seedLearningMethod(HBaseCustomClient client) throws IOException {
        client.insertRecord(TABLE_LEARNING_METHOD, "LMTH001", "main", "id", "LMTH001");
        client.insertRecord(TABLE_LEARNING_METHOD, "LMTH001", "main", "name", "Project Based Learning");
        client.insertRecord(TABLE_LEARNING_METHOD, "LMTH001", "main", "description", "Model pembelajaran proyek");
        client.insertRecord(TABLE_LEARNING_METHOD, "LMTH001", "detail", "created_by", "Seeder");
    }

    private void seedAppraisalForm(HBaseCustomClient client) throws IOException {
        client.insertRecord(TABLE_APPRAISAL_FORM, "AF001", "main", "id", "AF001");
        client.insertRecord(TABLE_APPRAISAL_FORM, "AF001", "main", "name", "Rubrik Penilaian");
        client.insertRecord(TABLE_APPRAISAL_FORM, "AF001", "main", "description", "Rubrik penilaian tugas");
        client.insertRecord(TABLE_APPRAISAL_FORM, "AF001", "detail", "created_by", "Seeder");
    }

    private void seedAssessmentCriteria(HBaseCustomClient client) throws IOException {
        client.insertRecord(TABLE_ASSESSMENT_CRITERIA, "AC001", "main", "id", "AC001");
        client.insertRecord(TABLE_ASSESSMENT_CRITERIA, "AC001", "main", "name", "Kelengkapan Solusi");
        client.insertRecord(TABLE_ASSESSMENT_CRITERIA, "AC001", "main", "description", "Menilai kelengkapan jawaban");
        client.insertRecord(TABLE_ASSESSMENT_CRITERIA, "AC001", "detail", "created_by", "Seeder");
    }
}
