package com.doyatama.university.service;

import com.doyatama.university.exception.BadRequestException;
import com.doyatama.university.exception.ResourceNotFoundException;
import com.doyatama.university.model.*;
import com.doyatama.university.payload.DefaultResponse;
import com.doyatama.university.payload.LectureRequest;
import com.doyatama.university.payload.PagedResponse;
import com.doyatama.university.repository.*;
import com.doyatama.university.util.AppConstants;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;

@Service
public class LectureService {
    private static final int LOOKUP_SIZE = 1000;

    private LectureRepository lectureRepository = new LectureRepository();
    private ReligionRepository religionRepository = new ReligionRepository();
    private StudyProgramRepository studyProgramRepository = new StudyProgramRepository();
    private UserRepository userRepository = new UserRepository();

    // private static final Logger logger =
    // LoggerFactory.getLogger(LectureService.class);

    public PagedResponse<Lecture> getAllLecture(int page, int size) throws IOException {
        validatePageNumberAndSize(page, size);

        // Retrieve Lecture
        List<Lecture> lectureResponse = lectureRepository.findAll(size);
        return new PagedResponse<>(lectureResponse, lectureResponse.size(), "Successfully get data", 200);
    }

    public Lecture createLecture(LectureRequest lectureRequest) throws IOException {
        User user = validateUserRelation(lectureRequest.getUser_id(), null);
        StudyProgram studyProgram = resolveStudyProgram(lectureRequest, user);
        Religion religionResponse = resolveReligion(lectureRequest);
        Lecture lecture = new Lecture();
        if (religionResponse.getName() != null && studyProgram.getId() != null) {
            lecture.setUser_id(lectureRequest.getUser_id());
            lecture.setNip(lectureRequest.getNip());
            lecture.setName(lectureRequest.getName());
            lecture.setPlace_born(lectureRequest.getPlace_born());
            lecture.setDate_born(lectureRequest.getDate_born());
            lecture.setGender(lectureRequest.getGender());
            lecture.setStatus(lectureRequest.getStatus());
            lecture.setPhone(lectureRequest.getPhone());
            lecture.setAddress(lectureRequest.getAddress());
            lecture.setReligion(religionResponse);
            lecture.setStudyProgram(studyProgram);
            Lecture savedLecture = lectureRepository.save(lecture);
            syncUserStudyProgram(lectureRequest.getUser_id(), studyProgram);
            return savedLecture;
        } else {
            return null;
        }
    }

    public DefaultResponse<Lecture> getLectureById(String lectureId) throws IOException {
        // Retrieve Lecture
        Lecture lectureResponse = lectureRepository.findById(lectureId);
        return new DefaultResponse<>(lectureResponse.isValid() ? lectureResponse : null,
                lectureResponse.isValid() ? 1 : 0, "Successfully get data");
    }

    public Lecture updateLecture(String lectureId, LectureRequest lectureRequest) throws IOException {
        User user = validateUserRelation(lectureRequest.getUser_id(), lectureId);
        StudyProgram studyProgram = resolveStudyProgram(lectureRequest, user);
        Religion religionResponse = resolveReligion(lectureRequest);
        Lecture lecture = new Lecture();
        if (religionResponse.getName() != null && studyProgram.getId() != null) {
            lecture.setUser_id(lectureRequest.getUser_id());
            lecture.setNip(lectureRequest.getNip());
            lecture.setName(lectureRequest.getName());
            lecture.setPlace_born(lectureRequest.getPlace_born());
            lecture.setDate_born(lectureRequest.getDate_born());
            lecture.setGender(lectureRequest.getGender());
            lecture.setStatus(lectureRequest.getStatus());
            lecture.setPhone(lectureRequest.getPhone());
            lecture.setAddress(lectureRequest.getAddress());
            lecture.setReligion(religionResponse);
            lecture.setStudyProgram(studyProgram);
            Lecture updatedLecture = lectureRepository.update(lectureId, lecture);
            syncUserStudyProgram(lectureRequest.getUser_id(), studyProgram);
            return updatedLecture;
        } else {
            return null;
        }
    }

    public void deleteLectureById(String lectureId) throws IOException {
        Lecture lectureResponse = lectureRepository.findById(lectureId);
        if (lectureResponse.isValid()) {
            lectureRepository.deleteById(lectureId);
        } else {
            throw new ResourceNotFoundException("Lecture", "id", lectureId);
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

    private User validateUserRelation(String userId, String currentLectureId) throws IOException {
        if (userId == null || userId.trim().isEmpty()) {
            throw new BadRequestException("User login dosen wajib dipilih.");
        }

        User user = userRepository.findById(userId);
        if (user == null || user.getId() == null) {
            throw new BadRequestException("User login tidak ditemukan.");
        }

        if (!"3".equals(user.getRoles())) {
            throw new BadRequestException("User login harus memiliki role guru.");
        }

        Lecture linkedLecture = lectureRepository.findByUserId(userId);
        if (linkedLecture != null && linkedLecture.getId() != null
                && (currentLectureId == null || !currentLectureId.equals(linkedLecture.getId()))) {
            throw new BadRequestException("User login sudah terhubung dengan data dosen lain.");
        }

        return user;
    }

    private StudyProgram resolveStudyProgram(LectureRequest lectureRequest, User user) throws IOException {
        String studyProgramId = lectureRequest.getIdStudyProgram();
        if ((studyProgramId == null || studyProgramId.trim().isEmpty()) && user.getSchool() != null) {
            studyProgramId = user.getSchool().getIdSchool();
        }

        StudyProgram studyProgram = studyProgramRepository.findById(studyProgramId);
        if ((studyProgram == null || studyProgram.getId() == null) && lectureRequest.getStudy_program_name() != null) {
            studyProgram = findStudyProgramByName(lectureRequest.getStudy_program_name());
        }

        if (studyProgram == null || studyProgram.getId() == null) {
            throw new BadRequestException("Program studi tidak ditemukan.");
        }

        return studyProgram;
    }

    private Religion resolveReligion(LectureRequest lectureRequest) throws IOException {
        String religionId = lectureRequest.getReligion_id();
        Religion religion = religionRepository.findById(religionId != null && !religionId.trim().isEmpty()
                ? religionId
                : "0");

        if ((religion == null || religion.getId() == null) && lectureRequest.getReligion_name() != null) {
            religion = findReligionByName(lectureRequest.getReligion_name());
        }

        if (religion == null || religion.getId() == null) {
            throw new BadRequestException("Agama tidak ditemukan.");
        }

        return religion;
    }

    private Religion findReligionByName(String religionName) throws IOException {
        if (religionName == null || religionName.trim().isEmpty()) {
            return null;
        }

        List<Religion> religions = religionRepository.findAll(LOOKUP_SIZE);
        for (Religion religion : religions) {
            if (religionName.equalsIgnoreCase(religion.getName())) {
                return religion;
            }
        }
        return null;
    }

    private StudyProgram findStudyProgramByName(String studyProgramName) throws IOException {
        if (studyProgramName == null || studyProgramName.trim().isEmpty()) {
            return null;
        }

        List<StudyProgram> studyPrograms = studyProgramRepository.findAll(LOOKUP_SIZE);
        for (StudyProgram studyProgram : studyPrograms) {
            if (studyProgramName.equalsIgnoreCase(studyProgram.getName())) {
                return studyProgram;
            }
        }
        return null;
    }

    private void syncUserStudyProgram(String userId, StudyProgram studyProgram) throws IOException {
        if (userId == null || studyProgram == null || studyProgram.getId() == null) {
            return;
        }

        User user = new User();
        user.setSchool(new School(studyProgram.getId(), studyProgram.getName(), ""));
        userRepository.update(userId, user);
    }

}
