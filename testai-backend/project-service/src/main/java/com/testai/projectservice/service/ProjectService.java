package com.testai.projectservice.service;

import com.testai.projectservice.dto.ProjectDTO;
import com.testai.projectservice.entity.Project;
import com.testai.projectservice.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private FileStorageService fileStorageService;

    @Transactional
    public Project createProject(ProjectDTO request) {
        String docPath = "";

        if(request.getDocSubmitMode().equals("url")) {
            docPath = request.getDocUrl();
        }
        else{
            docPath = fileStorageService.store(request.getDocFile(), request.getName());
        }

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setProjectUrl(request.getProjectUrl());
        project.setAuthType(request.getAuthType());
        project.setDocUrl(docPath);
        project.setUserId(request.getUserId());
        project.setDocMode(request.getDocMode());
        return projectRepository.save(project);
    }

    public Project getProjectById(UUID projectId) {
        return projectRepository.findById(projectId).orElseThrow();
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getProjectsByAuthType(Project.AuthType authType) {
        return projectRepository.findByAuthType(authType);
    }

    public List<Project> getProjectsByDocMode(Project.DocsMode docMode) {
        return projectRepository.findByDocMode(docMode);
    }

    public String deleteProjectById(UUID projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        String docUrl = project.getDocUrl();
        try{
            if(!docUrl.startsWith("http")){
                fileStorageService.delete(docUrl);
            }
            projectRepository.delete(project);
            return "Project with id '" + projectId + "' deleted successfully";
        }
        catch(Exception e){
            return "Failed to delete project: " + e.getMessage();
        }
    }

}
