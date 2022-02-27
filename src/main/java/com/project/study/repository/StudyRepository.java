package com.project.study.repository;

import com.project.domain.Account;
import com.project.domain.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {

    boolean existsByPath(String path);

    @EntityGraph(value = "Study.withAll", type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

    @EntityGraph(value = "Study.withTagsAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithTagsByPath(String path);

    @EntityGraph(value = "Study.withZonesAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithZonesByPath(String path);

    @EntityGraph(value = "Study.withStatusAndManagers", type= EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithStatusByPath(String path);

    @EntityGraph(value = "Study.withMembersAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithMemberByPath(String path);

    @EntityGraph(value = "Study.withStudy", type = EntityGraph.EntityGraphType.FETCH)
    boolean existsByTitle(String newTitle);

}
