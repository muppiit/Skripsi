# Seeder for Testing

Seeder ini memindahkan data seeding dari HBaseClientStructure.java ke folder seeder agar bisa dijalankan terpisah.

## Seeder files

- SeederRunner.java
- UserSeeder.java
- StudyProgramSeeder.java
- DepartmentSeeder.java
- SubjectGroupSeeder.java
- SubjectSeeder.java
- AcademicSeeder.java
- TahunAjaranSeeder.java
- SemesterSeeder.java
- KelasSeeder.java
- ReferenceDataSeeder.java
- LectureSeeder.java
- StudentSeeder.java
- SeasonSeeder.java
- RpsSeeder.java
- RpsDetailSeeder.java
- SoalUjianSeeder.java
- BankSoalSeeder.java

## Commands

Jalankan dari folder backend:

1. Seeder user saja:

mvn -q exec:java -Dexec.mainClass=seeder.SeederRunner -Dexec.args=user

2. Seeder study program saja:

mvn -q exec:java -Dexec.mainClass=seeder.SeederRunner -Dexec.args=study-program

3. Seeder semua data testing (study program + user):

mvn -q exec:java -Dexec.mainClass=seeder.SeederRunner -Dexec.args=all

4. Seeder master data saja (department, study program, subject group, subject, tahun ajaran, semester, kelas, taksonomi, referensi, lecture, student, season):

mvn -q exec:java -Dexec.mainClass=seeder.SeederRunner -Dexec.args=master

5. Seeder academic inti saja (tahun ajaran, semester, kelas, taksonomi):

mvn -q exec:java -Dexec.mainClass=seeder.SeederRunner -Dexec.args=academic

6. Seeder student + season saja:

mvn -q exec:java -Dexec.mainClass=seeder.SeederRunner -Dexec.args=season

7. Seeder RPS dan RPS detail saja:

mvn -q exec:java -Dexec.mainClass=seeder.SeederRunner -Dexec.args=rps

8. Seeder data ujian saja (soal ujian + bank soal):

mvn -q exec:java -Dexec.mainClass=seeder.SeederRunner -Dexec.args=exam

## Notes

- UserSeeder memakai data yang sama dengan seeder user di HBaseClientStructure.java (USR001 sampai USR005).
- StudentSeeder menyimpan relasi `study_program`, `kelas`, `angkatan`, dan referensi season di `detail`.
- SeasonSeeder menyimpan kombinasi TA/Semester/Kelas/Subject serta daftar student per season.
- Mode all menjalankan master data + user + RPS + RPS detail + soal ujian + bank soal.
