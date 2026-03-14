package com.rahul.projectevaulation.projectinnovation.project.entity;

import com.rahul.projectevaulation.projectinnovation.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ProjectStatusHistory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String status;

    private String comments;

    @Column(updatable = false)
    private LocalDateTime changedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id")
    private User changedBy;

    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }
}
