package seeder;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;
import java.time.Instant;

public class UserSeeder {

    private static final TableName TABLE_USER = TableName.valueOf("users");
    private static final String DEFAULT_PASSWORD_HASH = "$2a$10$SDRWMUk.2fnli0GTmqodJexjRksTw0En98dU8fdKsw7nTbZzMrj.2";

    public void seed(HBaseCustomClient client) throws IOException {
        seedAdmin(client);
        seedOperator1(client);
        seedOperator2(client);
        seedTeacher(client);
        seedAdditionalLecturers(client);
        seedStudents(client);
    }

    private void seedAdmin(HBaseCustomClient client) throws IOException {
        client.insertRecord(TABLE_USER, "USR001", "main", "id", "USR001");
        client.insertRecord(TABLE_USER, "USR001", "main", "email", "admin@gmail.com");
        client.insertRecord(TABLE_USER, "USR001", "main", "name", "Administrator");
        client.insertRecord(TABLE_USER, "USR001", "main", "username", "admin");
        client.insertRecord(TABLE_USER, "USR001", "main", "password", DEFAULT_PASSWORD_HASH);
        client.insertRecord(TABLE_USER, "USR001", "main", "roles", "1");
        client.insertRecord(TABLE_USER, "USR001", "main", "created_at", "2023-05-14T04:56:23.174Z");
        client.insertRecord(TABLE_USER, "USR001", "detail", "created_by", "Doyatama");
    }

    private void seedOperator1(HBaseCustomClient client) throws IOException {
        client.insertRecord(TABLE_USER, "USR002", "main", "id", "USR002");
        client.insertRecord(TABLE_USER, "USR002", "main", "email", "operator1@gmail.com");
        client.insertRecord(TABLE_USER, "USR002", "main", "name", "Operator1");
        client.insertRecord(TABLE_USER, "USR002", "main", "username", "operator1");
        client.insertRecord(TABLE_USER, "USR002", "main", "password", DEFAULT_PASSWORD_HASH);
        client.insertRecord(TABLE_USER, "USR002", "study_program", "idSchool", "SP001");
        client.insertRecord(TABLE_USER, "USR002", "study_program", "nameSchool", "Teknik Informatika");
        client.insertRecord(TABLE_USER, "USR002", "main", "roles", "2");
        client.insertRecord(TABLE_USER, "USR002", "main", "created_at", "2023-05-14T04:56:23.174Z");
        client.insertRecord(TABLE_USER, "USR002", "detail", "created_by", "Doyatama");
    }

    private void seedOperator2(HBaseCustomClient client) throws IOException {
        client.insertRecord(TABLE_USER, "USR003", "main", "id", "USR003");
        client.insertRecord(TABLE_USER, "USR003", "main", "email", "operator2@gmail.com");
        client.insertRecord(TABLE_USER, "USR003", "main", "name", "Operator2");
        client.insertRecord(TABLE_USER, "USR003", "main", "username", "operator2");
        client.insertRecord(TABLE_USER, "USR003", "main", "password", DEFAULT_PASSWORD_HASH);
        client.insertRecord(TABLE_USER, "USR003", "study_program", "idSchool", "SP002");
        client.insertRecord(TABLE_USER, "USR003", "study_program", "nameSchool", "Sistem Informasi Bisnis");
        client.insertRecord(TABLE_USER, "USR003", "main", "roles", "2");
        client.insertRecord(TABLE_USER, "USR003", "main", "created_at", "2023-05-14T04:56:23.174Z");
        client.insertRecord(TABLE_USER, "USR003", "detail", "created_by", "Doyatama");
    }

    private void seedTeacher(HBaseCustomClient client) throws IOException {
        client.insertRecord(TABLE_USER, "USR004", "main", "id", "USR004");
        client.insertRecord(TABLE_USER, "USR004", "main", "email", "dosen.ti@polinema.ac.id");
        client.insertRecord(TABLE_USER, "USR004", "main", "name", "Dosen TI Polinema Lumajang");
        client.insertRecord(TABLE_USER, "USR004", "main", "username", "dosen_ti_lmj");
        client.insertRecord(TABLE_USER, "USR004", "main", "password", DEFAULT_PASSWORD_HASH);
        client.insertRecord(TABLE_USER, "USR004", "study_program", "idSchool", "SP001");
        client.insertRecord(TABLE_USER, "USR004", "study_program", "nameSchool", "Teknik Informatika");
        client.insertRecord(TABLE_USER, "USR004", "main", "roles", "3");
        client.insertRecord(TABLE_USER, "USR004", "main", "created_at", Instant.now().toString());
        client.insertRecord(TABLE_USER, "USR004", "detail", "created_by", "Doyatama");
    }

    private void seedAdditionalLecturers(HBaseCustomClient client) throws IOException {
        client.insertRecord(TABLE_USER, "USR009", "main", "id", "USR009");
        client.insertRecord(TABLE_USER, "USR009", "main", "email", "ratna.wulandari@polinema.ac.id");
        client.insertRecord(TABLE_USER, "USR009", "main", "name", "Ratna Wulandari");
        client.insertRecord(TABLE_USER, "USR009", "main", "username", "ratna_wulandari");
        client.insertRecord(TABLE_USER, "USR009", "main", "password", DEFAULT_PASSWORD_HASH);
        client.insertRecord(TABLE_USER, "USR009", "study_program", "idSchool", "SP001");
        client.insertRecord(TABLE_USER, "USR009", "study_program", "nameSchool", "Teknik Informatika");
        client.insertRecord(TABLE_USER, "USR009", "main", "roles", "3");
        client.insertRecord(TABLE_USER, "USR009", "main", "created_at", Instant.now().toString());
        client.insertRecord(TABLE_USER, "USR009", "detail", "created_by", "Doyatama");

        client.insertRecord(TABLE_USER, "USR010", "main", "id", "USR010");
        client.insertRecord(TABLE_USER, "USR010", "main", "email", "fajar.hidayat@polinema.ac.id");
        client.insertRecord(TABLE_USER, "USR010", "main", "name", "Fajar Hidayat");
        client.insertRecord(TABLE_USER, "USR010", "main", "username", "fajar_hidayat");
        client.insertRecord(TABLE_USER, "USR010", "main", "password", DEFAULT_PASSWORD_HASH);
        client.insertRecord(TABLE_USER, "USR010", "study_program", "idSchool", "SP002");
        client.insertRecord(TABLE_USER, "USR010", "study_program", "nameSchool", "Sistem Informasi Bisnis");
        client.insertRecord(TABLE_USER, "USR010", "main", "roles", "3");
        client.insertRecord(TABLE_USER, "USR010", "main", "created_at", Instant.now().toString());
        client.insertRecord(TABLE_USER, "USR010", "detail", "created_by", "Doyatama");

        client.insertRecord(TABLE_USER, "USR011", "main", "id", "USR011");
        client.insertRecord(TABLE_USER, "USR011", "main", "email", "sari.ayu@polinema.ac.id");
        client.insertRecord(TABLE_USER, "USR011", "main", "name", "Sari Ayu");
        client.insertRecord(TABLE_USER, "USR011", "main", "username", "sari_ayu");
        client.insertRecord(TABLE_USER, "USR011", "main", "password", DEFAULT_PASSWORD_HASH);
        client.insertRecord(TABLE_USER, "USR011", "study_program", "idSchool", "SP002");
        client.insertRecord(TABLE_USER, "USR011", "study_program", "nameSchool", "Sistem Informasi Bisnis");
        client.insertRecord(TABLE_USER, "USR011", "main", "roles", "3");
        client.insertRecord(TABLE_USER, "USR011", "main", "created_at", Instant.now().toString());
        client.insertRecord(TABLE_USER, "USR011", "detail", "created_by", "Doyatama");
    }

    private void seedStudents(HBaseCustomClient client) throws IOException {
        client.insertRecord(TABLE_USER, "USR005", "main", "id", "USR005");
        client.insertRecord(TABLE_USER, "USR005", "main", "email", "andi.pratama@student.polinema.ac.id");
        client.insertRecord(TABLE_USER, "USR005", "main", "name", "Andi Pratama");
        client.insertRecord(TABLE_USER, "USR005", "main", "username", "andi_pratama");
        client.insertRecord(TABLE_USER, "USR005", "main", "password", DEFAULT_PASSWORD_HASH);
        client.insertRecord(TABLE_USER, "USR005", "study_program", "idSchool", "SP001");
        client.insertRecord(TABLE_USER, "USR005", "study_program", "nameSchool", "Teknik Informatika");
        client.insertRecord(TABLE_USER, "USR005", "main", "roles", "5");
        client.insertRecord(TABLE_USER, "USR005", "main", "created_at", Instant.now().toString());
        client.insertRecord(TABLE_USER, "USR005", "detail", "created_by", "Doyatama");

        client.insertRecord(TABLE_USER, "USR006", "main", "id", "USR006");
        client.insertRecord(TABLE_USER, "USR006", "main", "email", "budi.santoso@student.polinema.ac.id");
        client.insertRecord(TABLE_USER, "USR006", "main", "name", "Budi Santoso");
        client.insertRecord(TABLE_USER, "USR006", "main", "username", "budi_santoso");
        client.insertRecord(TABLE_USER, "USR006", "main", "password", DEFAULT_PASSWORD_HASH);
        client.insertRecord(TABLE_USER, "USR006", "study_program", "idSchool", "SP001");
        client.insertRecord(TABLE_USER, "USR006", "study_program", "nameSchool", "Teknik Informatika");
        client.insertRecord(TABLE_USER, "USR006", "main", "roles", "5");
        client.insertRecord(TABLE_USER, "USR006", "main", "created_at", Instant.now().toString());
        client.insertRecord(TABLE_USER, "USR006", "detail", "created_by", "Doyatama");

        client.insertRecord(TABLE_USER, "USR007", "main", "id", "USR007");
        client.insertRecord(TABLE_USER, "USR007", "main", "email", "citra.lestari@student.polinema.ac.id");
        client.insertRecord(TABLE_USER, "USR007", "main", "name", "Citra Lestari");
        client.insertRecord(TABLE_USER, "USR007", "main", "username", "citra_lestari");
        client.insertRecord(TABLE_USER, "USR007", "main", "password", DEFAULT_PASSWORD_HASH);
        client.insertRecord(TABLE_USER, "USR007", "study_program", "idSchool", "SP001");
        client.insertRecord(TABLE_USER, "USR007", "study_program", "nameSchool", "Teknik Informatika");
        client.insertRecord(TABLE_USER, "USR007", "main", "roles", "5");
        client.insertRecord(TABLE_USER, "USR007", "main", "created_at", Instant.now().toString());
        client.insertRecord(TABLE_USER, "USR007", "detail", "created_by", "Doyatama");

        client.insertRecord(TABLE_USER, "USR008", "main", "id", "USR008");
        client.insertRecord(TABLE_USER, "USR008", "main", "email", "dian.puspita@student.polinema.ac.id");
        client.insertRecord(TABLE_USER, "USR008", "main", "name", "Dian Puspita");
        client.insertRecord(TABLE_USER, "USR008", "main", "username", "dian_puspita");
        client.insertRecord(TABLE_USER, "USR008", "main", "password", DEFAULT_PASSWORD_HASH);
        client.insertRecord(TABLE_USER, "USR008", "study_program", "idSchool", "SP002");
        client.insertRecord(TABLE_USER, "USR008", "study_program", "nameSchool", "Sistem Informasi Bisnis");
        client.insertRecord(TABLE_USER, "USR008", "main", "roles", "5");
        client.insertRecord(TABLE_USER, "USR008", "main", "created_at", Instant.now().toString());
        client.insertRecord(TABLE_USER, "USR008", "detail", "created_by", "Doyatama");

        client.insertRecord(TABLE_USER, "USR012", "main", "id", "USR012");
        client.insertRecord(TABLE_USER, "USR012", "main", "email", "eka.saputra@student.polinema.ac.id");
        client.insertRecord(TABLE_USER, "USR012", "main", "name", "Eka Saputra");
        client.insertRecord(TABLE_USER, "USR012", "main", "username", "eka_saputra");
        client.insertRecord(TABLE_USER, "USR012", "main", "password", DEFAULT_PASSWORD_HASH);
        client.insertRecord(TABLE_USER, "USR012", "study_program", "idSchool", "SP001");
        client.insertRecord(TABLE_USER, "USR012", "study_program", "nameSchool", "Teknik Informatika");
        client.insertRecord(TABLE_USER, "USR012", "main", "roles", "5");
        client.insertRecord(TABLE_USER, "USR012", "main", "created_at", Instant.now().toString());
        client.insertRecord(TABLE_USER, "USR012", "detail", "created_by", "Doyatama");

        client.insertRecord(TABLE_USER, "USR013", "main", "id", "USR013");
        client.insertRecord(TABLE_USER, "USR013", "main", "email", "fitri.rahma@student.polinema.ac.id");
        client.insertRecord(TABLE_USER, "USR013", "main", "name", "Fitri Rahma");
        client.insertRecord(TABLE_USER, "USR013", "main", "username", "fitri_rahma");
        client.insertRecord(TABLE_USER, "USR013", "main", "password", DEFAULT_PASSWORD_HASH);
        client.insertRecord(TABLE_USER, "USR013", "study_program", "idSchool", "SP001");
        client.insertRecord(TABLE_USER, "USR013", "study_program", "nameSchool", "Teknik Informatika");
        client.insertRecord(TABLE_USER, "USR013", "main", "roles", "5");
        client.insertRecord(TABLE_USER, "USR013", "main", "created_at", Instant.now().toString());
        client.insertRecord(TABLE_USER, "USR013", "detail", "created_by", "Doyatama");

        client.insertRecord(TABLE_USER, "USR014", "main", "id", "USR014");
        client.insertRecord(TABLE_USER, "USR014", "main", "email", "galih.maulana@student.polinema.ac.id");
        client.insertRecord(TABLE_USER, "USR014", "main", "name", "Galih Maulana");
        client.insertRecord(TABLE_USER, "USR014", "main", "username", "galih_maulana");
        client.insertRecord(TABLE_USER, "USR014", "main", "password", DEFAULT_PASSWORD_HASH);
        client.insertRecord(TABLE_USER, "USR014", "study_program", "idSchool", "SP002");
        client.insertRecord(TABLE_USER, "USR014", "study_program", "nameSchool", "Sistem Informasi Bisnis");
        client.insertRecord(TABLE_USER, "USR014", "main", "roles", "5");
        client.insertRecord(TABLE_USER, "USR014", "main", "created_at", Instant.now().toString());
        client.insertRecord(TABLE_USER, "USR014", "detail", "created_by", "Doyatama");

        client.insertRecord(TABLE_USER, "USR015", "main", "id", "USR015");
        client.insertRecord(TABLE_USER, "USR015", "main", "email", "hana.oktaviani@student.polinema.ac.id");
        client.insertRecord(TABLE_USER, "USR015", "main", "name", "Hana Oktaviani");
        client.insertRecord(TABLE_USER, "USR015", "main", "username", "hana_oktaviani");
        client.insertRecord(TABLE_USER, "USR015", "main", "password", DEFAULT_PASSWORD_HASH);
        client.insertRecord(TABLE_USER, "USR015", "study_program", "idSchool", "SP002");
        client.insertRecord(TABLE_USER, "USR015", "study_program", "nameSchool", "Sistem Informasi Bisnis");
        client.insertRecord(TABLE_USER, "USR015", "main", "roles", "5");
        client.insertRecord(TABLE_USER, "USR015", "main", "created_at", Instant.now().toString());
        client.insertRecord(TABLE_USER, "USR015", "detail", "created_by", "Doyatama");
    }
}
