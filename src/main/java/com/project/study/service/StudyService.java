package com.project.study.service;

import com.project.account.util.UserAccount;
import com.project.domain.Account;
import com.project.domain.Study;
import com.project.domain.Tag;
import com.project.domain.Zone;
import com.project.study.form.StudyDescriptionForm;
import com.project.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;

    public Study createNewStudy(Study study, Account account) {
        Study newStudy = studyRepository.save(study);
        newStudy.addManager(account);
        return newStudy;
    }

    public Study getStudyToUpdateTag(Account account, String path) {
        Study study = studyRepository.findStudyWithTagsByPath(path);
        checkIfExistingStudy(path, study);
        checkIfManager(account, study);
        return study;
    }

    public Study getStudyToUpdateZone(Account account, String path) {
        Study study = studyRepository.findStudyWithZonesByPath(path);
        checkIfExistingStudy(path, study);
        checkIfManager(account, study);
        return study;
    }

    public Study getStudyToUpdateStatus(Account account, String path) {
        Study study = studyRepository.findStudyWithStatusByPath(path);
        checkIfExistingStudy(path, study);
        checkIfManager(account, study);
        return study;
    }

    private void checkIfManager(Account account, Study study) {
        if (!study.isManagerOf(account)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    private void checkIfExistingStudy(String path, Study study) {
        if (study == null) {
            throw new IllegalArgumentException(path + "에 해당하는 스터디가 없습니다.");
        }
    }

    public Study getStudyToUpdate(Account account, String path) {
        Study study = this.getStudy(path);
        if (!account.isManagerOf(study)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }

        return study;
    }

    public Study getStudy(String path) {
        Study study = this.studyRepository.findByPath(path);
        if (study == null) {
            throw new IllegalArgumentException(path + "에 해당하는 스터디가 없습니다.");
        }

        return study;
    }

    public void updateStudyDescription(Study study, StudyDescriptionForm studyDescriptionForm) {
        modelMapper.map(studyDescriptionForm, study);
    }

    public void updateStudyImage(Study study, String image) {
        study.setImage(image);
    }

    public void enableStudyBanner(Study study) {
        study.setUseBanner(true);
    }

    public void disableStudyBanner(Study study) {
        study.setUseBanner(false);
    }

    public Set<Tag> getTags(String path) {
        Study study = studyRepository.findByPath(path);
        return study.getTags();
    }

    public void addTag(Study study, Tag tag) {
        study.getTags().add(tag);
    }

    public void removeTag(Study study, Tag tag) {
        study.getTags().remove(tag);
    }

    public void addZone(Study study, Zone zone) {
        study.getZones().add(zone);
    }

    public void removeZone(Study study, Zone zone) {
        study.getZones().remove(zone);
    }

    public void publish(Study study) {
        study.publish();
    }

    public void close(Study study) {
        study.close();
    }

    public void startRecruit(Study study) {
        study.startRecruit();
    }

    public void stopRecruit(Study study) {
        study.stopRecruit();
    }

    public void updateStudyPath(Study study, String newPath) {
        study.setPath(newPath);
    }

    public void updateStudyTitle(Study study, String newTitle) {
        if (studyRepository.existsByTitle(newTitle)) {
            throw new IllegalArgumentException(newTitle + "은(는) 이미 사용중입니다.");
        }
        study.setTitle(newTitle);
    }

    public boolean isValidPath(String newPath) {
        return !studyRepository.existsByPath(newPath);
    }

    public boolean isValidTitle(String newTitle) {
        return !studyRepository.existsByTitle(newTitle);
    }

    public boolean isRemovable(Study study) {
        return study.isRemovable();
    }

    public void removeStudy(Study study) {
        studyRepository.delete(study);
    }

    public void addMember(Study study, Account account) {
        if (study.isMemberOf(account) || study.isManagerOf(account)) {
            throw new IllegalArgumentException("이미 등록된 스터디입니다.");
        }
        study.getMembers().add(account);
    }

    public void removeMember(Study study, Account account) {
        if (!study.isMemberOf(account)) {
            throw new IllegalArgumentException("등록되지 않은 회원입니다.");
        }
        study.getMembers().remove(account);
    }


}
