package com.github.ovchingus.ppmtool.web;

import com.github.ovchingus.ppmtool.domain.Project;
import com.github.ovchingus.ppmtool.services.MapValidationErrorService;
import com.github.ovchingus.ppmtool.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;

    private final MapValidationErrorService mapValidationErrorService;

    @Autowired
    public ProjectController(ProjectService projectService, MapValidationErrorService mapValidationErrorService) {
        this.projectService = projectService;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PostMapping("")
    public ResponseEntity<?> createNewProject(@Valid @RequestBody Project project, BindingResult result) {

        Optional<ResponseEntity<?>> errorMap = mapValidationErrorService.MapValidationService(result);

        // returns error responce if present, otherwise returns good responce
        // optional is bad choice here
        if (errorMap.isPresent()) return errorMap.get();
        projectService.saveOrUpdate(project);
        return new ResponseEntity<>(project, HttpStatus.CREATED);
    }
}
