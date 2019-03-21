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
import java.util.Optional;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;

    private final MapValidationErrorService mapValidationErrorService;

    @Autowired
    public ProjectController(ProjectService projectService,
                             MapValidationErrorService mapValidationErrorService) {
        this.projectService = projectService;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PostMapping("")
    public ResponseEntity<?> createNewProject(@Valid @RequestBody Project project,
                                              BindingResult result) {

        Optional<ResponseEntity<?>> errorMap =
                mapValidationErrorService.MapValidationService(result);

        // returns error responce if present, otherwise returns good responce
        // optional is bad choice here
        if (errorMap.isPresent()) return errorMap.get();
        projectService.saveOrUpdate(project);
        return new ResponseEntity<>(project, HttpStatus.CREATED);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<?> getProjectById(@PathVariable final String projectId) {
        Project project = projectService.findByProjectIdentifier(projectId);
        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @GetMapping("/all")
    public Iterable<Project> getAllProjects() {
        return projectService.findAllProjects();
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable final String projectId) {
        projectService.deleteProjectByIdentifier(projectId);
        return new ResponseEntity<>("Project with ID: " + projectId
                + " successfully removed", HttpStatus.OK);
    }
}
