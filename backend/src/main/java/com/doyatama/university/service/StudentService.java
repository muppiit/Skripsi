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
import java.util.UUID;

@Service
public class StudentService {
    private StudentRepository studentRepository = new StudentRepository();
    private ReligionRepository religionRepository = new ReligionRepository();
    private StudyProgramRepository studyProgramRepository = new StudyProgramRepository();
    private KelasRepository kelasRepository = new KelasRepository();
    private TahunAjaranRepository tahunAjaranRepository = new TahunAjaranRepository();
    private UserRepository userRepository = new UserRepository();

    public PagedResponse<Student> getAllStudent(int page, int size) throws IOException {
        validatePageNumberAndSize(page, size);
        List<Student> studentResponse = studentRepository.findAll(size);
        return new PagedResponse<>(studentResponse, studentResponse.size(), "Successfully get data", 200);
    }

    public PagedResponse<Student> getStudentByUserId(int page, int size, String userId) throws IOException {
        validatePageNumberAndSize(page, size);
        Student student = studentRepository.findByUserId(userId);
        List<Student> studentResponse = student != null && student.isValid()
                ? java.util.Collections.singletonList(student)
                : java.util.Collections.emptyList();
        return new PagedResponse<>(studentResponse, studentResponse.size(), "Successfully get data", 200);
    }

    public Student createStudent(StudentRequest studentRequest) throws IOException {
        normalizeCreateRequest(studentRequest);
        validateUserRelation(studentRequest.getUser_id(), null);

        Kelas kelas = kelasRepository.findById(studentRequest.getIdKelas());
        StudyProgram studyProgram = resolveStudyProgram(studentRequest, kelas);
        Religion religionResponse = religionRepository
                .findById(studentRequest.getReligion_id() != null && !studentRequest.getReligion_id().isEmpty()
                        ? studentRequest.getReligion_id()
                        : "0");

        validateReferences(religionResponse, studyProgram, kelas);

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
        student.setUser_id(studentRequest.getUser_id());
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
        validateUserRelation(studentRequest.getUser_id(), studentId);

        Kelas kelas = kelasRepository.findById(studentRequest.getIdKelas());
        StudyProgram studyProgram = resolveStudyProgram(studentRequest, kelas);
        Religion religionResponse = religionRepository
                .findById(studentRequest.getReligion_id() != null && !studentRequest.getReligion_id().isEmpty()
                        ? studentRequest.getReligion_id()
                        : "0");

        validateReferences(religionResponse, studyProgram, kelas);

        TahunAjaran tahunAjaran = null;
        if (studentRequest.getIdTahunAjaran() != null && !studentRequest.getIdTahunAjaran().isEmpty()) {
            tahunAjaran = tahunAjaranRepository.findById(studentRequest.getIdTahunAjaran());
        }
        if (tahunAjaran == null && kelas.getTahunAjaran() != null && kelas.getTahunAjaran().getIdTahun() != null) {
            tahunAjaran = kelas.getTahunAjaran();
        }

        Student student = new Student();
        student.setId(studentRequest.getId());
        student.setUser_id(studentRequest.getUser_id());
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

    private StudyProgram resolveStudyProgram(StudentRequest studentRequest, Kelas kelas) throws IOException {
        if (studentRequest.getUser_id() != null && !studentRequest.getUser_id().isEmpty()) {
            User user = userRepository.findById(studentRequest.getUser_id());
            if (user != null && user.getSchool() != null && user.getSchool().getIdSchool() != null) {
                StudyProgram userStudyProgram = studyProgramRepository.findById(user.getSchool().getIdSchool());
                if (userStudyProgram != null && userStudyProgram.getId() != null) {
                    return userStudyProgram;
                }
            }
        }

        StudyProgram requestStudyProgram = studyProgramRepository.findById(studentRequest.getIdStudyProgram());
        if (requestStudyProgram != null && requestStudyProgram.getId() != null) {
            return requestStudyProgram;
        }

        if (kelas != null && kelas.getStudyProgram() != null && kelas.getStudyProgram().getId() != null) {
            return kelas.getStudyProgram();
        }

        return null;
    }

    private void normalizeCreateRequest(StudentRequest studentRequest) {
        if (studentRequest.getId() == null || studentRequest.getId().trim().isEmpty()) {
            studentRequest.setId(UUID.randomUUID().toString());
        }
    }

    private void validateReferences(Religion religion, StudyProgram studyProgram, Kelas kelas) {
        if (religion == null || religion.getId() == null || religion.getName() == null) {
            throw new BadRequestException("Agama tidak ditemukan.");
        }

        if (studyProgram == null || studyProgram.getId() == null) {
            throw new BadRequestException("Program Studi tidak ditemukan.");
        }

        if (kelas == null || kelas.getIdKelas() == null) {
            throw new BadRequestException("Kelas tidak ditemukan.");
        }
    }

    private void validateUserRelation(String userId, String currentStudentId) throws IOException {
        if (userId == null || userId.isEmpty()) {
            throw new BadRequestException("User login mahasiswa wajib dipilih.");
        }

        User user = userRepository.findById(userId);
        if (user == null || user.getId() == null) {
            throw new BadRequestException("User login tidak ditemukan.");
        }

        if (!isStudentRole(user.getRoles())) {
            throw new BadRequestException("User login harus memiliki role Mahasiswa.");
        }

        Student linkedStudent = studentRepository.findByUserId(userId);
        if (linkedStudent != null && linkedStudent.isValid()
                && (currentStudentId == null || !currentStudentId.equals(linkedStudent.getId()))) {
            throw new BadRequestException("User login sudah terhubung dengan mahasiswa lain.");
        }
    }

    private boolean isStudentRole(String role) {
        if (role == null) {
            return false;
        }

        String normalizedRole = role.toLowerCase();
        return normalizedRole.equals("5")
                || normalizedRole.equals("role_student")
                || normalizedRole.equals("student")
                || normalizedRole.equals("mahasiswa");
    }
}
