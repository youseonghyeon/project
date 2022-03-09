package com.project.modules.study.event;

import com.project.modules.account.repository.AccountRepository;
import com.project.modules.domain.*;
import com.project.modules.study.repository.StudyRepository;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.project.modules.domain.QAccount.account;
import static com.project.modules.domain.QTag.tag;
import static com.project.modules.domain.QZone.zone;

@Slf4j
@Async
@Transactional(readOnly = true)
@Component
@RequiredArgsConstructor
public class StudyEventListener {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final JPAQueryFactory queryFactory;

    @EventListener
    public void handleStudyCreateEvent(StudyCreatedEvent studyCreateEvent) {
        Study study = studyRepository.findStudyWithTagsAndZonesById(studyCreateEvent.getStudy().getId());
//        Set<Zone> zones = study.getZones();
//         study.getTags().stream().forEach(Tag::getTitle);
//        List<Account> accounts = queryFactory.select(account)
//                .from(account)
//                .join(tag)
//                .join(zone)
//                .where(tag.title.in(tags).or(zone.in(zones))).fetch();
//
//        accounts.forEach(acc -> {
//            if (account.isStudyCreatedfdsf)
//        });

    }
}
