package com.doyatama.university.service;

import com.doyatama.university.exception.BadRequestException;
import com.doyatama.university.exception.ResourceNotFoundException;
import com.doyatama.university.model.*;
import com.doyatama.university.model.Student;
import com.doyatama.university.payload.DefaultResponse;
import com.doyatama.university.payload.StudentRequest;
import com.doyatama.university.payload.PagedResponse;
import com.doyatama.university.repository.*;
import com.doyatama.university.repository.StudentRepository;
import com.doyatama.university.util.AppConstants;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;

@Service
public class StudentService {
    private StudentRepository studentRepository = new StudentRepository();
    private ReligionRepository religionRepository = new ReligionRepository();
    private StudyProgramRepository studyProgramRepository = new StudyProgramRepository();
    private KelasRepository kelasRepository = new KelasRepository();
    private TahunAjaranRepository tahunAjaranRepository = new TahunAjaranRepository();

    public PagedResponse<Student> getAllStudent(int page, int size) throws IOException {
        validatePageNumberAndSize(page, size);
        List<Student> studentResponse = studentRepository.findAll(size);
        return new PagedResponse<>(studentResponse, studentResponse.size(), "Successfully get data", 200);
    }

    public Student createStudent(StudentRequest studentRequest) throws IOException {
        StudyProgram studyProgram = studyProgramRepository.findById(studentRequest.getIdStudyProgram());
        Kelas kelas = kelasRepository.findById(studentRequest.getIdKelas());
        Religion religionResponse = religionRepository
                .findById(studentRequest.getReligion_id() != null && !studentRequest.getReligion_id().isEmpty()
                        ? studentRequest.getReligion_id()
                        : "0");

        if (religionResponse == null || religionResponse.getName() == null
                || studyProgram == null || studyProgram.getId() == null
                || kelas == null || kelas.getIdKelas() == null) {
            return null;
        }

        // Ambil tahunAjaran: utamakan dari request, fallback ke tahunAjaran di Kelas
        TahunAjaran tahunAjaran = null;
        if (studentRequest.getIdTahunAjaran() != null && !studentRequest.getIdTahunAjaran().isEmpty()) {
            tahunAjaran = tahunAjaranRepository.findById(studentRequest.getIdTahunAjaran());
        }
        // Jika tidak dari request, gunakan tahunAjaran yang sudah di-embed di Kelas
        if (tahunAjaran == null && kelas.getTahunAjaran() != null && kelas.getTahunAjaran().getIdTahun() != null) {
            tahunAjaran = kelas.getTahunAjaran();
        }

        Student student = new Student();
        student.setId(studentRequest.getId());
        student.setNisn(studentRequest.getNisn());
        student.setName(studentRequest.getName());
        student.setPlace_born(studentRequest.getPlace_born());
        student.setBirth_date(studentRequest.getBirth_date());
        student.setGender(studentRequest.getGender());
        student.setPhone(studentRequest.getPhone());
        student.setAddress(studentRequest.getAddress());
        student.setReligion(religionResponse);
        student.setStudyProgram(studyProgram);
        student.setKelas(kelas);
        student.setTahunAjaran(tahunAjaran);

        return studentRepository.save(student);
    }

    public DefaultResponse<Student> getStudentById(String studentId) throws IOException {
        Student studentResponse = studentRepository.findById(studentId);
        return new DefaultResponse<>(studentResponse != null && studentResponse.isValid() ? studentResponse : null,
                studentResponse != null && studentResponse.isValid() ? 1 : 0, "Successfully get data");
    }

    public Student updateStudent(String studentId, StudentRequest studentRequest) throws IOException {
        StudyProgram studyProgram = studyProgramRepository.findById(studentRequest.getIdStudyProgram());
        Kelas kelas = kelasRepository.findById(studentRequest.getIdKelas());
        Religion religionResponse = religionRepository
                .findById(studentRequest.getReligion_id() != null && !studentRequest.getReligion_id().isEmpty()
                        ? studentRequest.getReligion_id()
                        : "0");

        if (religionResponse == null || religionResponse.getName() == null
                || studyProgram == null || studyProgram.getId() == null
                || kelas == null || kelas.getIdKelas() == null) {
            return null;
        }

        TahunAjaran tahunAjaran = null;
        if (studentRequest.getIdTahunAjaran() != null && !studentRequest.getIdTahunAjaran().isEmpty()) {
            tahunAjaran = tahunAjaranRepository.findById(studentRequest.getIdTahunAjaran());
        }
        if (tahunAjaran == null && kelas.getTahunAjaran() != null && kelas.getTahunAjaran().getIdTahun() != null) {
            tahunAjaran = kelas.getTahunAjaran();
        }

        Student student = new Student();
        student.setId(studentRequest.getId());
        student.setNisn(studentRequest.getNisn());
        student.setName(studentRequest.getName());
        student.setPlace_born(studentRequest.getPlace_born());
        student.setBirth_date(studentRequest.getBirth_date());
        student.setGender(studentRequest.getGender());
        student.setPhone(studentRequest.getPhone());
        student.setAddress(studentRequest.getAddress());
        student.setReligion(religionResponse);
        student.setStudyProgram(studyProgram);
        student.setKelas(kelas);
        student.setTahunAjaran(tahunAjaran);

        return studentRepository.update(studentId, student);
    }

    public void deleteStudentById(String studentId) throws IOException {
        Student studentResponse = studentRepository.findById(studentId);
        if (studentResponse != null && studentResponse.isValid()) {
            studentRepository.deleteById(studentId);
        } else {
            throw new ResourceNotFoundException("Student", "id", studentId);
        }
    }

    private void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }
        if (size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }
}
