package com.rahul.projectevaulation.projectinnovation.evaluation.entity;

import com.rahul.projectevaulation.projectinnovation.auth.entity.User;
import com.rahul.projectevaulation.projectinnovation.idea.entity.ProjectIdea;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "FacultyDecision")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacultyDecision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String decision; // e.g., "APPROVED", "REJECTED"

    @Lob
    private String comments;

    @Column(updatable = false)
    private LocalDateTime decidedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_idea_id")
    private ProjectIdea projectIdea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private User mentor;

    @PrePersist
    protected void onCreate() {
        decidedAt = LocalDateTime.now();
    }
}
