package com.doyatama.university.service;

import com.doyatama.university.exception.BadRequestException;
import com.doyatama.university.exception.ResourceNotFoundException;
import com.doyatama.university.model.School;
import com.doyatama.university.model.StudyProgram;
import com.doyatama.university.model.TahunAjaran;
import com.doyatama.university.payload.TahunAjaranRequest;
import com.doyatama.university.payload.DefaultResponse;
import com.doyatama.university.payload.PagedResponse;
import com.doyatama.university.repository.StudyProgramRepository;
import com.doyatama.university.repository.TahunAjaranRepository;
import com.doyatama.university.util.AppConstants;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TahunAjaranService {
    private TahunAjaranRepository tahunRepository = new TahunAjaranRepository();
    private StudyProgramRepository studyProgramRepository = new StudyProgramRepository();

    public PagedResponse<TahunAjaran> getAllTahunAjaran(int page, String studyProgramId, int size) throws IOException {
        validatePageNumberAndSize(page, size);

        // Retrieve Polls
        List<TahunAjaran> tahunResponse;

        if (studyProgramId.equalsIgnoreCase("*")) {
            tahunResponse = tahunRepository.findAll(size);
        } else {
            tahunResponse = tahunRepository.findTahunAjaranByStudyProgram(studyProgramId, size);
        }

        return new PagedResponse<>(tahunResponse, tahunResponse.size(), "Successfully get data", 200);
    }

    public TahunAjaran createTahunAjaran(TahunAjaranRequest tahunRequest) throws IOException {

        if (tahunRequest.getIdTahun() == null) {
            tahunRequest.setIdTahun(UUID.randomUUID().toString());
        }

        if (tahunRepository.existsById(tahunRequest.getIdTahun())) {
            throw new IllegalArgumentException("Tahun Ajaran already exist");
        }

        StudyProgram studyProgramResponse = studyProgramRepository.findById(tahunRequest.getEffectiveStudyProgramId());

        if (studyProgramResponse == null || studyProgramResponse.getId() == null) {
            throw new IllegalArgumentException("Program Studi tidak ditemukan");
        }

        // Konversi StudyProgram ke School agar cocok dengan field HBase (idSchool, nameSchool)
        School school = new School();
        school.setIdSchool(studyProgramResponse.getId());
        school.setNameSchool(studyProgramResponse.getName());

        TahunAjaran tahun = new TahunAjaran();
        tahun.setIdTahun(tahunRequest.getIdTahun());
        tahun.setTahunAjaran(tahunRequest.getTahunAjaran());
        tahun.setStudyProgram(school);

        return tahunRepository.save(tahun);
    }

    public DefaultResponse<TahunAjaran> getTahunAjaranById(String tahunId) throws IOException {
        TahunAjaran tahunResponse = tahunRepository.findTahunById(tahunId);
        return new DefaultResponse<>(tahunResponse.isValid() ? tahunResponse : null, tahunResponse.isValid() ? 1 : 0,
                "Successfully get data");
    }

    public TahunAjaran updateTahunAjaran(String tahunId, TahunAjaranRequest tahunRequest) throws IOException {

        TahunAjaran tahun = new TahunAjaran();

        StudyProgram studyProgramResponse = studyProgramRepository.findById(tahunRequest.getEffectiveStudyProgramId());

        if (studyProgramResponse != null && studyProgramResponse.getId() != null) {
            // Konversi StudyProgram ke School agar cocok dengan field HBase (idSchool, nameSchool)
            School school = new School();
            school.setIdSchool(studyProgramResponse.getId());
            school.setNameSchool(studyProgramResponse.getName());

            tahun.setTahunAjaran(tahunRequest.getTahunAjaran());
            tahun.setStudyProgram(school);

            return tahunRepository.update(tahunId, tahun);
        } else {
            throw new IllegalArgumentException("Program Studi tidak ditemukan");
        }
    }

    public void deleteTahunAjaranById(String tahunId) throws IOException {
        TahunAjaran tahunResponse = tahunRepository.findTahunById(tahunId);
        if (tahunResponse.isValid()) {
            tahunRepository.deleteById(tahunId);
        } else {
            throw new ResourceNotFoundException("TahunAjaran", "id", tahunId);
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
