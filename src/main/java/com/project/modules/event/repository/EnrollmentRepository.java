package com.project.modules.event.repository;

import com.project.modules.domain.Account;
import com.project.modules.domain.Enrollment;
import com.project.modules.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Enrollment findByEventAndAccount(Event event, Account account);
}
