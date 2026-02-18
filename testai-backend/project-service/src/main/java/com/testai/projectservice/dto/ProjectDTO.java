package com.testai.projectservice.dto;

import com.testai.projectservice.entity.Project;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class ProjectDTO {
    private String name;
    private String description;
    private String projectUrl;
    private String docSubmitMode;
    private String docUrl;
    private Project.DocsMode docMode;
    private MultipartFile docFile;
    private UUID userId;
    private Project.AuthType authType;

    public String getDocSubmitMode() {
        return docSubmitMode;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public void setDocSubmitMode(String docSubmitMode) {
        this.docSubmitMode = docSubmitMode;
    }

    public String getDocUrl() {
        return docUrl;
    }

    public void setDocUrl(String docUrl) {
        this.docUrl = docUrl;
    }

    public Project.AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(Project.AuthType authType) {
        this.authType = authType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Project.DocsMode getDocMode() {
        return docMode;
    }

    public void setDocMode(Project.DocsMode docMode) {
        this.docMode = docMode;
    }

    public MultipartFile getDocFile() {
        return docFile;
    }

    public void setDocFile(MultipartFile docFile) {
        this.docFile = docFile;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
