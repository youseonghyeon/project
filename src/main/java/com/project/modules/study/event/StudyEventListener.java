package com.project.modules.study.event;

import com.project.modules.domain.Study;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Async
@Transactional(readOnly = true)
@Component
public class StudyEventListener {

    @EventListener
    public void handleStudyCreateEvent(StudyCreateEvent studyCreateEvent) {
        Study study = studyCreateEvent.getStudy();
        log.info(study.getTitle() + "is created.");
        // TODO 이메일 보내거나, DB에 Notification 정보를 저장하면 된다.
        throw new RuntimeException();
    }
}
