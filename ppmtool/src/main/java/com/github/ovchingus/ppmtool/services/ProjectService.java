package com.github.ovchingus.ppmtool.services;

import com.github.ovchingus.ppmtool.domain.Project;
import com.github.ovchingus.ppmtool.exceptions.CustomResponseEntityExceptionHandler;
import com.github.ovchingus.ppmtool.exceptions.ProjectIdException;
import com.github.ovchingus.ppmtool.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project saveOrUpdate(Project project) {
        try {
            return projectRepository.save(project);
        } catch (Exception e) {
            throw new ProjectIdException("ProjectId '" +
                    project.getProjectIdentifier().toUpperCase() + "' already exists");
        }
    }


}
