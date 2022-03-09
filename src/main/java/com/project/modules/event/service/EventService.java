package com.project.modules.event.service;

import com.project.modules.domain.*;
import com.project.modules.event.form.EventForm;
import com.project.modules.event.repository.EnrollmentRepository;
import com.project.modules.event.repository.EventRepository;
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

    public void fetchEvent(Event ev) {
        Event event = eventRepository.findById(ev.getId()).orElseThrow();
        if (isFCFS(event)) {
            long cnt = event.getNumberOfAcceptedEnrollments();
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

    public void cancelEnrollment(Event event, Account account) {
        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);
        if (!enrollment.isAttended()) {
            event.removeEnrollment(enrollment);
            enrollmentRepository.delete(enrollment);
            event.acceptNextWaitingEnrollment();
        }
    }

    public void acceptEnrollment(Event event, Enrollment enrollment) {
        event.accept(enrollment);
    }

    public void rejectEnrollment(Event event, Enrollment enrollment) {
        event.reject(enrollment);
    }

    public void checkInEnrollment(Enrollment enrollment) {
        enrollment.setAttended(true);
    }

    public void cancelCheckInEnrollment(Enrollment enrollment) {
        enrollment.setAttended(false);
    }

}
