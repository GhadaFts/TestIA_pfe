package com.testai.projectservice.controller;

import com.testai.projectservice.dto.EndpointDTO;
import com.testai.projectservice.dto.ProjectDTO;
import com.testai.projectservice.dto.ScanSwaggerResponse;
import com.testai.projectservice.entity.Project;
import com.testai.projectservice.exception.UserNotFoundException;
import com.testai.projectservice.feignclient.UserServiceClient;
import com.testai.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserServiceClient userServiceClient;

    private boolean isInvalidLink(String url) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return !response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return true; // failed → invalid
        }
    }


    @PostMapping(path = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProject(@ModelAttribute ProjectDTO request) {
        try {
            if(userServiceClient.getUserById(request.getUserId()) == null){
                return ResponseEntity.badRequest().body("User does not exist");
            }
            if (isInvalidLink(request.getProjectUrl())){
                return ResponseEntity.badRequest().body("Invalid Service URL !!");
            }
            if(request.getDocSubmitMode().equals("url") && isInvalidLink(request.getDocUrl())){
                return ResponseEntity.badRequest().body("Invalid Documentation URL !!");
            }
            Project newProject = projectService.createProject(request);
            return ResponseEntity.ok(newProject);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @GetMapping("/auth_type/{authtype}")
    public ResponseEntity<List<Project>> getProjectsByAuthtype(@PathVariable Project.AuthType authtype) {
        return ResponseEntity.ok(projectService.getProjectsByAuthType(authtype));
    }

    @GetMapping("/doc_mode/{docMode}")
    public ResponseEntity<List<Project>> getProjectsByDocMode(@PathVariable Project.DocsMode docMode) {
        return ResponseEntity.ok(projectService.getProjectsByDocMode(docMode));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable UUID id) {
        String msg = projectService.deleteProjectById(id);
        return ResponseEntity.ok(msg);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Project>> getProjectsByUserId(@PathVariable UUID userId) {
        try {
            List<Project> projects = projectService.getProjectsByUserId(userId);
            return ResponseEntity.ok(projects);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/{projectId}/scan-endpoints")
    public ResponseEntity<?> scanProjectEndpoints(@PathVariable UUID projectId) {
        log.info("🔍 Demande de scan des endpoints pour le projet {}", projectId);

        try {
            ScanSwaggerResponse response = projectService.scanEndpoints(projectId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Erreur lors du scan : {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * ⭐️ NOUVEAU : Récupérer les endpoints d'un projet
     * GET /api/projects/{projectId}/endpoints
     */
    @GetMapping("/{projectId}/endpoints")
    public ResponseEntity<?> getProjectEndpoints(@PathVariable UUID projectId) {
        log.info("📋 Récupération des endpoints du projet {}", projectId);

        try {
            List<EndpointDTO> endpoints = projectService.getProjectEndpoints(projectId);
            return ResponseEntity.ok(endpoints);

        } catch (Exception e) {
            log.error("❌ Erreur : {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * ⭐️ NOUVEAU : Compter les endpoints d'un projet
     * GET /api/projects/{projectId}/endpoints/count
     */
    @GetMapping("/{projectId}/endpoints/count")
    public ResponseEntity<?> countProjectEndpoints(@PathVariable UUID projectId) {
        log.info("🔢 Comptage des endpoints du projet {}", projectId);

        try {
            Map<String, Object> count = projectService.countProjectEndpoints(projectId);
            return ResponseEntity.ok(count);

        } catch (Exception e) {
            log.error("❌ Erreur : {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

}
