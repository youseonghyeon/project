package com.project.event.service;

import com.project.domain.Account;
import com.project.domain.Enrollment;
import com.project.domain.Event;
import com.project.domain.EventType;
import com.project.event.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;


    public Enrollment enroll(Event event, Account account) {
        boolean accepted = false;

        // FCFS
        if (isFCFS(event)) {
            if (event.joinableCnt() > 0) {
                accepted = true;
            }
        }
        // CONFIRMATIVE
        if (isCONFIRMATIVE(event)) {
            accepted = false;
        }

        return enrollmentRepository.save(new Enrollment(event, account, LocalDateTime.now(), accepted, false));
    }

    private boolean isFCFS(Event event) {
        return event.getEventType().equals(EventType.FCFS);
    }

    private boolean isCONFIRMATIVE(Event event) {
        return event.getEventType().equals(EventType.CONFIRMATIVE);
    }
}
