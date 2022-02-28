package com.project.event.service;

import com.project.domain.*;
import com.project.event.form.EventForm;
import com.project.event.repository.EnrollmentRepository;
import com.project.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EnrollmentRepository enrollmentRepository;

    public Event createEvent(Event event, Study study, Account account) {
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setStudy(study);
        return eventRepository.save(event);
    }

    public void updateEvent(Event event, EventForm eventForm) {
        modelMapper.map(eventForm, event);
        fetchEvent(event);
        // TODO 테스트 코드를 작성하자
    }

    public void cancelEnrollment(Event event, Account account) {
        List<Enrollment> enrollments = event.getEnrollments();

        for (Enrollment enrollment : enrollments) {
            if (enrollment.getAccount().equals(account)) {
//                enrollments.remove(enrollment);
                enrollmentRepository.delete(enrollment);
            }
        }
        fetchEvent(event);
    }

    public void fetchEvent(Event ev) {
        Event event = eventRepository.findById(ev.getId()).orElseThrow();
        if (isFCFS(event)) {
            int cnt = event.joinableCnt();
            if (cnt <= 0) return;
            List<Enrollment> enrollments = event.getEnrollments();
            Collections.sort(enrollments);
            for (Enrollment e : enrollments) {
                if (cnt == 0) break;
                if (!e.isAccepted()) {
                    e.setAccepted(true);
                    enrollmentRepository.save(e);
                    cnt--;
                }
            }
        }
    }

    private boolean isFCFS(Event event) {
        return event.getEventType().equals(EventType.FCFS);
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }


}
