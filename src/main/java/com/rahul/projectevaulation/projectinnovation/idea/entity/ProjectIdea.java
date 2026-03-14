package com.rahul.projectevaulation.projectinnovation.idea.entity;

import com.rahul.projectevaulation.projectinnovation.project.entity.Project;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ProjectIdea")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectIdea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    private String status; // e.g., "PENDING", "APPROVED", "REJECTED"

    @Column(updatable = false)
    private LocalDateTime submittedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }
}
