package com.project.event.repository;

import com.project.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {

}
