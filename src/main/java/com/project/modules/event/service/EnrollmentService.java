package com.project.modules.event.service;

import com.project.modules.domain.Account;
import com.project.modules.domain.Enrollment;
import com.project.modules.domain.Event;
import com.project.modules.domain.EventType;
import com.project.modules.event.repository.EnrollmentRepository;
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
            if (event.getNumberOfAcceptedEnrollments() > 0) {
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
