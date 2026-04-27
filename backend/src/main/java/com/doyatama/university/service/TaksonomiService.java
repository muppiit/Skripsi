package com.doyatama.university.service;

import com.doyatama.university.exception.BadRequestException;
import com.doyatama.university.exception.ResourceNotFoundException;
import com.doyatama.university.model.Taksonomi;
import com.doyatama.university.model.School;
import com.doyatama.university.model.StudyProgram;
import com.doyatama.university.payload.TaksonomiRequest;
import com.doyatama.university.payload.DefaultResponse;
import com.doyatama.university.payload.PagedResponse;
import com.doyatama.university.repository.TaksonomiRepository;
import com.doyatama.university.repository.StudyProgramRepository;
import com.doyatama.university.util.AppConstants;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class TaksonomiService {

    private TaksonomiRepository taksonomiRepository = new TaksonomiRepository();
    private StudyProgramRepository studyProgramRepository = new StudyProgramRepository();

    public PagedResponse<Taksonomi> getAllTaksonomi(int page, int size, String schoolID) throws IOException {
        validatePageNumberAndSize(page, size);

        List<Taksonomi> taksonomiResponse;

        if (schoolID == null || schoolID.trim().isEmpty() || schoolID.equalsIgnoreCase("*")) {
            taksonomiResponse = taksonomiRepository.findAll(size);
        } else {
            taksonomiResponse = taksonomiRepository.findTaksonomiBySekolah(schoolID, size);
        }

        return new PagedResponse<>(taksonomiResponse, taksonomiResponse.size(), "Successfully get data", 200);
    }

    private void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if (size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }

    public Taksonomi createTaksonomi(TaksonomiRequest taksonomiRequest) throws IOException {

        if (taksonomiRequest.getIdTaksonomi() == null) {
            taksonomiRequest.setIdTaksonomi(UUID.randomUUID().toString());
        }

        if (taksonomiRepository.existById(taksonomiRequest.getIdTaksonomi())) {
            throw new IllegalArgumentException("Taksonomi with id already exists");
        }

        if (taksonomiRequest.getIdSekolah() == null || taksonomiRequest.getIdSekolah().trim().isEmpty()) {
            throw new IllegalArgumentException("Program studi wajib dipilih");
        }

        StudyProgram studyProgramResponse = studyProgramRepository.findById(taksonomiRequest.getIdSekolah());
        if (studyProgramResponse == null || studyProgramResponse.getId() == null) {
            throw new IllegalArgumentException("Program studi tidak ditemukan");
        }

        School schoolResponse = new School();
        schoolResponse.setIdSchool(studyProgramResponse.getId());
        schoolResponse.setNameSchool(studyProgramResponse.getName());
        Taksonomi taksonomi = new Taksonomi();

        taksonomi.setIdTaksonomi(taksonomiRequest.getIdTaksonomi());
        taksonomi.setNamaTaksonomi(taksonomiRequest.getNamaTaksonomi());
        taksonomi.setDeskripsiTaksonomi(taksonomiRequest.getDeskripsiTaksonomi());
        taksonomi.setSchool(schoolResponse);

        return taksonomiRepository.save(taksonomi);
    }

    public DefaultResponse<Taksonomi> getTaksonomiById(String taksonomiId) throws IOException {
        Taksonomi taksonomiResponse = taksonomiRepository.findById(taksonomiId);
        return new DefaultResponse<>(taksonomiResponse.isValid() ? taksonomiResponse : null,
                taksonomiResponse.isValid() ? 1 : 0,
                "Successfully get data");
    }

    public Taksonomi updateTaksonomi(String taksonomiId, TaksonomiRequest taksonomiRequest) throws IOException {

        Taksonomi taksonomi = new Taksonomi();
        StudyProgram studyProgramResponse = studyProgramRepository.findById(taksonomiRequest.getIdSekolah());
        if (studyProgramResponse == null || studyProgramResponse.getId() == null) {
            throw new IllegalArgumentException("Program studi tidak ditemukan");
        }

        School schoolResponse = new School();
        schoolResponse.setIdSchool(studyProgramResponse.getId());
        schoolResponse.setNameSchool(studyProgramResponse.getName());

        taksonomi.setNamaTaksonomi(taksonomiRequest.getNamaTaksonomi());
        taksonomi.setDeskripsiTaksonomi(taksonomiRequest.getDeskripsiTaksonomi());
        taksonomi.setSchool(schoolResponse);

        return taksonomiRepository.update(taksonomiId, taksonomi);
    }

    public void deleteTaksonomiById(String taksonomiId) throws IOException {
        if (taksonomiId == null || taksonomiId.trim().isEmpty()) {
            throw new IllegalArgumentException("Id taksonomi wajib diisi");
        }

        if (!taksonomiRepository.existById(taksonomiId)) {
            throw new ResourceNotFoundException("Taksonomi", "id", taksonomiId);
        }

        taksonomiRepository.deleteById(taksonomiId);
    }
}
