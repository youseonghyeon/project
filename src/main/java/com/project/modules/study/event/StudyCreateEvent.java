package com.project.modules.study.event;

import com.project.modules.domain.Study;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class StudyCreateEvent {

    private Study study;

    public StudyCreateEvent(Study study) {
        this.study = study;
    }
}
