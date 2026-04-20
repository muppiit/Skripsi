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
        seedStudent(client);
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
        client.insertRecord(TABLE_USER, "USR004", "main", "email", "guru@gmail.com");
        client.insertRecord(TABLE_USER, "USR004", "main", "name", "Guru SMK Rowokangkung");
        client.insertRecord(TABLE_USER, "USR004", "main", "username", "gurusmk1");
        client.insertRecord(TABLE_USER, "USR004", "main", "password", DEFAULT_PASSWORD_HASH);
        client.insertRecord(TABLE_USER, "USR004", "study_program", "idSchool", "SP001");
        client.insertRecord(TABLE_USER, "USR004", "study_program", "nameSchool", "Teknik Informatika");
        client.insertRecord(TABLE_USER, "USR004", "main", "roles", "3");
        client.insertRecord(TABLE_USER, "USR004", "main", "created_at", Instant.now().toString());
        client.insertRecord(TABLE_USER, "USR004", "detail", "created_by", "Doyatama");
    }

    private void seedStudent(HBaseCustomClient client) throws IOException {
        client.insertRecord(TABLE_USER, "USR005", "main", "id", "USR005");
        client.insertRecord(TABLE_USER, "USR005", "main", "email", "murid@gmail.com");
        client.insertRecord(TABLE_USER, "USR005", "main", "name", "Murid SMK Rowokangkung");
        client.insertRecord(TABLE_USER, "USR005", "main", "username", "muridsmk1");
        client.insertRecord(TABLE_USER, "USR005", "main", "password", DEFAULT_PASSWORD_HASH);
        client.insertRecord(TABLE_USER, "USR005", "study_program", "idSchool", "SP001");
        client.insertRecord(TABLE_USER, "USR005", "study_program", "nameSchool", "Teknik Informatika");
        client.insertRecord(TABLE_USER, "USR005", "main", "roles", "5");
        client.insertRecord(TABLE_USER, "USR005", "main", "created_at", Instant.now().toString());
        client.insertRecord(TABLE_USER, "USR005", "detail", "created_by", "Doyatama");
    }
}
