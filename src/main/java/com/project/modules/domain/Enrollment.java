package com.project.modules.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class Enrollment implements Comparable<Enrollment> {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    private LocalDateTime enrolledAt;

    private boolean accepted;

    private boolean attended;

    public Enrollment(Event event, Account account, LocalDateTime enrolledAt, boolean accepted, boolean attended) {
        this.event = event;
        this.account = account;
        this.enrolledAt = enrolledAt;
        this.accepted = accepted;
        this.attended = attended;
    }

    @Override
    public int compareTo(Enrollment o) {
        return enrolledAt.compareTo(o.enrolledAt);
    }
}
