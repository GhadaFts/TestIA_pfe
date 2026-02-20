package com.testai.projectservice.controller;

import com.testai.projectservice.dto.ProjectDTO;
import com.testai.projectservice.entity.Project;
import com.testai.projectservice.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

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

}
