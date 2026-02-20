package com.testai.projectservice.repository;

import com.testai.projectservice.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByAuthType(Project.AuthType authType);

    List<Project> findByDocMode(Project.DocsMode docMode);
}
