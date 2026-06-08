package com.doyatama.university.controller;

import com.doyatama.university.model.Student;
import com.doyatama.university.exception.BadRequestException;
import com.doyatama.university.payload.*;
import com.doyatama.university.repository.StudentRepository;
import com.doyatama.university.service.StudentService;
import com.doyatama.university.util.AppConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    private StudentService studentService = new StudentService();

    StudentRepository studentRepository;

    @GetMapping
    public PagedResponse<Student> getStudents(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "userID", defaultValue = "*") String userID) throws IOException {
        if (userID != null && !userID.equalsIgnoreCase("*")) {
            return studentService.getStudentByUserId(page, size, userID);
        }

        return studentService.getAllStudent(page, size);
    }

    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody Map<String, Object> payload) throws IOException {
        try {
            StudentRequest studentRequest = toStudentRequest(payload);
            Student student = studentService.createStudent(studentRequest);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{studentId}")
                    .buildAndExpand(student.getId()).toUri();

            return ResponseEntity.created(location)
                    .body(new ApiResponse(true, "Student Created Successfully"));
        } catch (BadRequestException | IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Terjadi kesalahan sistem: " + e.getMessage()));
        }
    }

    @GetMapping("/{studentId}")
    public DefaultResponse<Student> getStudentById(@PathVariable String studentId) throws IOException {
        return studentService.getStudentById(studentId);
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<?> updateStudent(@PathVariable String studentId,
            @RequestBody Map<String, Object> payload) throws IOException {
        try {
            StudentRequest studentRequest = toStudentRequest(payload);
            Student student = studentService.updateStudent(studentId, studentRequest);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{studentId}")
                    .buildAndExpand(student.getId()).toUri();

            return ResponseEntity.created(location)
                    .body(new ApiResponse(true, "Student Updated Successfully"));
        } catch (BadRequestException | IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Terjadi kesalahan sistem: " + e.getMessage()));
        }
    }

    private StudentRequest toStudentRequest(Map<String, Object> payload) {
        StudentRequest request = new StudentRequest();
        request.setId(asString(payload.get("id")));
        request.setUser_id(asString(payload.get("user_id")));
        request.setNisn(asString(payload.get("nisn")));
        request.setName(asString(payload.get("name")));
        request.setGender(asString(payload.get("gender")));
        request.setPhone(asString(payload.get("phone")));
        request.setReligion_id(asString(payload.get("religion_id")));
        request.setIdStudyProgram(asString(payload.get("idStudyProgram")));
        request.setIdKelas(asString(payload.get("idKelas")));
        request.setIdTahunAjaran(asString(payload.get("idTahunAjaran")));
        request.setBirth_date(asString(payload.get("birth_date")));
        request.setPlace_born(asString(payload.get("place_born")));
        request.setAddress(asString(payload.get("address")));
        return request;
    }

    private String asString(Object value) {
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }

    @DeleteMapping("/{studentId}")
    public HttpStatus deleteStudent(@PathVariable(value = "studentId") String studentId) throws IOException {
        studentService.deleteStudentById(studentId);
        return HttpStatus.FORBIDDEN;
    }
}
