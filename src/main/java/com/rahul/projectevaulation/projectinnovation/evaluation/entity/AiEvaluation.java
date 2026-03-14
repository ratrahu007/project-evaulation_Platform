package com.rahul.projectevaulation.projectinnovation.evaluation.entity;

import com.rahul.projectevaulation.projectinnovation.idea.entity.ProjectIdea;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "AiEvaluation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double score;

    @Lob
    private String feedback;

    @Column(updatable = false)
    private LocalDateTime evaluatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_idea_id")
    private ProjectIdea projectIdea;

    @PrePersist
    protected void onCreate() {
        evaluatedAt = LocalDateTime.now();
    }
}
