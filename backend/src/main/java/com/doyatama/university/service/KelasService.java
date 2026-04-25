package com.doyatama.university.service;

import com.doyatama.university.exception.BadRequestException;
import com.doyatama.university.exception.ResourceNotFoundException;
import com.doyatama.university.model.Kelas;
import com.doyatama.university.model.StudyProgram;
import com.doyatama.university.model.TahunAjaran;
import com.doyatama.university.payload.KelasRequest;
import com.doyatama.university.payload.DefaultResponse;
import com.doyatama.university.payload.PagedResponse;
import com.doyatama.university.repository.KelasRepository;
import com.doyatama.university.repository.StudyProgramRepository;
import com.doyatama.university.repository.TahunAjaranRepository;
import com.doyatama.university.util.AppConstants;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class KelasService {
    private KelasRepository kelasRepository = new KelasRepository();
    private StudyProgramRepository studyProgramRepository = new StudyProgramRepository();
    private TahunAjaranRepository tahunAjaranRepository = new TahunAjaranRepository();

    public PagedResponse<Kelas> getAllKelas(int page, int size, String studyProgramId) throws IOException {
        validatePageNumberAndSize(page, size);

        List<Kelas> kelasResponse;

        if (studyProgramId.equalsIgnoreCase("*")) {
            kelasResponse = kelasRepository.findAll(size);
        } else {
            kelasResponse = kelasRepository.findKelasByStudyProgram(studyProgramId, size);
        }

        return new PagedResponse<>(kelasResponse, kelasResponse.size(), "Successfully get data", 200);
    }

    public Kelas createKelas(KelasRequest kelasRequest) throws IOException {

        if (kelasRequest.getIdKelas() == null) {
            kelasRequest.setIdKelas(UUID.randomUUID().toString());
        }

        if (kelasRepository.existsById(kelasRequest.getIdKelas())) {
            throw new IllegalArgumentException("Kelas already exist");
        }

        StudyProgram studyProgramResponse = studyProgramRepository.findById(kelasRequest.getIdStudyProgram());
        if (studyProgramResponse == null || studyProgramResponse.getId() == null) {
            throw new IllegalArgumentException("Program Studi tidak ditemukan");
        }

        TahunAjaran tahunAjaranResponse = tahunAjaranRepository.findById(kelasRequest.getIdTahunAjaran());
        if (tahunAjaranResponse == null || tahunAjaranResponse.getIdTahun() == null) {
            throw new IllegalArgumentException("Tahun Ajaran tidak ditemukan");
        }

        Kelas kelas = new Kelas();
        kelas.setIdKelas(kelasRequest.getIdKelas());
        kelas.setNamaKelas(kelasRequest.getNamaKelas());
        kelas.setStudyProgram(studyProgramResponse);
        kelas.setTahunAjaran(tahunAjaranResponse);

        return kelasRepository.save(kelas);
    }

    public DefaultResponse<Kelas> getKelasById(String kelasId) throws IOException {
        Kelas kelas = kelasRepository.findById(kelasId);
        return new DefaultResponse<>(kelas != null && kelas.isValid() ? kelas : null,
                kelas != null && kelas.isValid() ? 1 : 0, "Successfully get data");
    }

    public Kelas updateKelas(String kelasId, KelasRequest kelasRequest) throws IOException {

        Kelas kelas = new Kelas();

        StudyProgram studyProgramResponse = studyProgramRepository.findById(kelasRequest.getIdStudyProgram());
        if (studyProgramResponse != null && studyProgramResponse.getId() != null) {
            kelas.setStudyProgram(studyProgramResponse);
        }

        TahunAjaran tahunAjaranResponse = tahunAjaranRepository.findById(kelasRequest.getIdTahunAjaran());
        if (tahunAjaranResponse != null && tahunAjaranResponse.getIdTahun() != null) {
            kelas.setTahunAjaran(tahunAjaranResponse);
        } else {
            throw new IllegalArgumentException("Tahun Ajaran tidak ditemukan");
        }

        kelas.setNamaKelas(kelasRequest.getNamaKelas());

        return kelasRepository.update(kelasId, kelas);
    }

    public void deleteKelasById(String kelasId) throws IOException {
        Kelas kelasResponse = kelasRepository.findById(kelasId);
        if (kelasResponse != null && kelasResponse.isValid()) {
            kelasRepository.deleteById(kelasId);
        } else {
            throw new ResourceNotFoundException("Kelas", "id", kelasId);
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
