package com.doyatama.university.service;

import com.doyatama.university.exception.BadRequestException;
import com.doyatama.university.exception.ResourceNotFoundException;
import com.doyatama.university.model.Kelas;
import com.doyatama.university.model.Lecture;
import com.doyatama.university.model.Season;
import com.doyatama.university.model.Semester;
import com.doyatama.university.model.StudyProgram;
import com.doyatama.university.model.Student;
import com.doyatama.university.model.Subject;
import com.doyatama.university.model.TahunAjaran;
import com.doyatama.university.payload.SeasonRequest;
import com.doyatama.university.payload.DefaultResponse;
import com.doyatama.university.payload.PagedResponse;
import com.doyatama.university.repository.KelasRepository;
import com.doyatama.university.repository.LectureRepository;
import com.doyatama.university.repository.SeasonRepository;
import com.doyatama.university.repository.SemesterRepository;
import com.doyatama.university.repository.StudyProgramRepository;
import com.doyatama.university.repository.StudentRepository;
import com.doyatama.university.repository.SubjectRepository;
import com.doyatama.university.repository.TahunAjaranRepository;
import com.doyatama.university.util.AppConstants;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SeasonService {
    private SeasonRepository seasonRepository = new SeasonRepository();
    private StudyProgramRepository studyProgramRepository = new StudyProgramRepository();
    private KelasRepository kelasRepository = new KelasRepository();
    private SemesterRepository semesterRepository = new SemesterRepository();
    private TahunAjaranRepository tahunRepository = new TahunAjaranRepository();
    private StudentRepository studentRepository = new StudentRepository();
    private SubjectRepository subjectRepository = new SubjectRepository();
    private LectureRepository lectureRepository = new LectureRepository();

    public PagedResponse<Season> getAllSeason(int page, int size) throws IOException {
        validatePageNumberAndSize(page, size);

        // Retrieve Polls
        List<Season> seasonResponse = seasonRepository.findAll(size);
        return new PagedResponse<>(seasonResponse, seasonResponse.size(), "Successfully get data", 200);
    }

    public Season createSeason(SeasonRequest seasonRequest) throws IOException {
        if (seasonRequest.getIdSeason() == null) {
            seasonRequest.setIdSeason(UUID.randomUUID().toString());
        }

        StudyProgram studyProgram = studyProgramRepository.findById(seasonRequest.getStudyProgram_id());
        Kelas kelas = kelasRepository.findById(seasonRequest.getKelas_id());
        Semester semester = semesterRepository.findById(seasonRequest.getSemester_id());
        Lecture lecture = lectureRepository.findById(seasonRequest.getLecture_id());
        TahunAjaran tahun = tahunRepository.findById(seasonRequest.getTahunAjaran_id());
        List<Student> student = studentRepository.findAllById(seasonRequest.getStudent_id());
        List<Subject> subject = subjectRepository.findAllById(seasonRequest.getSubject_id());

        Season season = new Season();
        season.setIdSeason(seasonRequest.getIdSeason());
        season.setStudyProgram(studyProgram);
        season.setKelas(kelas);
        season.setSemester(semester);
        season.setLecture(lecture);
        season.setTahunAjaran(tahun);
        season.setStudent(student);
        season.setSubject(subject);

        return seasonRepository.save(season);
    }

    public DefaultResponse<Season> getSeasonById(String seasonId) throws IOException {
        // Retrieve Season
        Season season = seasonRepository.findById(seasonId);
        return new DefaultResponse<>(season.isValid() ? season : null, season.isValid() ? 1 : 0,
                "Successfully get data");
    }

    public void deleteSeasonById(String seasonId) throws IOException {
        Season seasonResponse = seasonRepository.findById(seasonId);
        if (seasonResponse.isValid()) {
            seasonRepository.deleteById(seasonId);
        } else {
            throw new ResourceNotFoundException("Season", "id", seasonId);
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
