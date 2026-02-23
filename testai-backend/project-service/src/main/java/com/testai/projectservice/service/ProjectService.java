package com.testai.projectservice.service;

import com.testai.projectservice.dto.ProjectDTO;
import com.testai.projectservice.dto.UserDTO;
import com.testai.projectservice.entity.Project;
import com.testai.projectservice.exception.UserNotFoundException;
import com.testai.projectservice.feignclient.UserClient;
import com.testai.projectservice.repository.ProjectRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserClient userServiceClient;  // ⭐️ Feign Client injecté

    @Transactional
    public Project createProject(ProjectDTO request) {
        // ⭐️ ÉTAPE 1 : Vérifier que l'utilisateur existe via Feign
        log.info("🔍 Vérification de l'utilisateur avec ID : {}", request.getUserId());

        try {
            UserDTO user = userServiceClient.getUserById(request.getUserId());
            log.info("✅ Utilisateur trouvé : {} ({})", user.getName(), user.getEmail());

            // ⭐️ Vérifier que l'utilisateur est actif
            if (user.getIsActive() == null || !user.getIsActive()) {
                throw new RuntimeException("L'utilisateur n'est pas actif");
            }

        } catch (FeignException.NotFound e) {
            log.error("❌ Utilisateur non trouvé : {}", request.getUserId());
            throw new UserNotFoundException(request.getUserId().toString());

        } catch (FeignException e) {
            log.error("❌ Erreur lors de la communication avec user-service : {}", e.getMessage());
            throw new RuntimeException("Impossible de vérifier l'utilisateur. User-service indisponible.");
        }

        // ÉTAPE 2 : Gérer la documentation (URL ou fichier)
        String docPath = "";
        if(request.getDocSubmitMode().equals("url")) {
            docPath = request.getDocUrl();
        } else {
            docPath = fileStorageService.store(request.getDocFile(), request.getName());
        }

        // ÉTAPE 3 : Créer le projet
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setProjectUrl(request.getProjectUrl());
        project.setAuthType(request.getAuthType());
        project.setDocUrl(docPath);
        project.setUserId(request.getUserId());
        project.setDocMode(request.getDocMode());

        Project savedProject = projectRepository.save(project);
        log.info("✅ Projet créé avec succès : {} (ID: {})", savedProject.getName(), savedProject.getId());

        return savedProject;
    }

    public Project getProjectById(UUID projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    /**
     * ⭐️ NOUVEAU : Récupérer les projets d'un utilisateur spécifique
     */
    public List<Project> getProjectsByUserId(UUID userId) {
        // Vérifier que l'utilisateur existe
        try {
            userServiceClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException(userId.toString());
        }

        return projectRepository.findAll().stream()
                .filter(p -> p.getUserId().equals(userId))
                .toList();
    }

    public List<Project> getProjectsByAuthType(Project.AuthType authType) {
        return projectRepository.findByAuthType(authType);
    }

    public List<Project> getProjectsByDocMode(Project.DocsMode docMode) {
        return projectRepository.findByDocMode(docMode);
    }

    public String deleteProjectById(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        String docUrl = project.getDocUrl();
        try {
            if(!docUrl.startsWith("http")){
                fileStorageService.delete(docUrl);
            }
            projectRepository.delete(project);
            log.info("✅ Projet supprimé : {}", projectId);
            return "Project with id '" + projectId + "' deleted successfully";
        } catch(Exception e) {
            log.error("❌ Erreur lors de la suppression du projet : {}", e.getMessage());
            return "Failed to delete project: " + e.getMessage();
        }
    }
}