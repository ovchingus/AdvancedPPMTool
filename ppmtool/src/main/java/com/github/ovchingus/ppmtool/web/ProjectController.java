package com.github.ovchingus.ppmtool.web;

import com.github.ovchingus.ppmtool.domain.Project;
import com.github.ovchingus.ppmtool.services.MapValidationErrorService;
import com.github.ovchingus.ppmtool.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/project")
@CrossOrigin
public class ProjectController {

    private final ProjectService projectService;

    private final MapValidationErrorService mapValidationErrorService;

    @Autowired
    public ProjectController(ProjectService projectService,
                             MapValidationErrorService mapValidationErrorService) {
        this.projectService = projectService;
        this.mapValidationErrorService = mapValidationErrorService;
    }


    /**
     * Checks errors in project creation form and if there is no errors create or update
     * existing project.
     */
    @PostMapping("")
    public ResponseEntity<?> createNewProject(@Valid @RequestBody Project project,
                                              BindingResult result, Principal principal) {

        return mapValidationErrorService.MapValidationService(result).orElseGet(() -> {
            projectService.saveOrUpdate(project, principal.getName());
            return new ResponseEntity<>(project, HttpStatus.CREATED);
        });
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<?> getProjectById(@PathVariable final String projectId, Principal principal) {
        Project project = projectService.findByProjectIdentifier(projectId, principal.getName());
        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @GetMapping("/all")
    public Iterable<Project> getAllProjects(Principal principal) {
        return projectService.findAllProjects(principal.getName());
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable final String projectId, Principal principal) {
        projectService.deleteProjectByIdentifier(projectId, principal.getName());
        return new ResponseEntity<>("Project with ID: " + projectId
                + " successfully removed", HttpStatus.OK);
    }
}
