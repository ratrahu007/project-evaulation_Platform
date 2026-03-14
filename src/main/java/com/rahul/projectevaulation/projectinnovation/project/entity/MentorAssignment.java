package com.rahul.projectevaulation.projectinnovation.project.entity;

import com.rahul.projectevaulation.projectinnovation.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "MentorAssignment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MentorAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false)
    private LocalDateTime assignedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private User mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
    }
}
