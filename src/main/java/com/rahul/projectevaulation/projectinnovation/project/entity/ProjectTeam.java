package com.rahul.projectevaulation.projectinnovation.project.entity;

import com.rahul.projectevaulation.projectinnovation.academic.entity.Semester;
import com.rahul.projectevaulation.projectinnovation.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "ProjectTeam")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String teamName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private User mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @OneToMany(mappedBy = "projectTeam", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectTeamMember> members;
}
