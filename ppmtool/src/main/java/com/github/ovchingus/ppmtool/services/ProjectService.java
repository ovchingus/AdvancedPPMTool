package com.github.ovchingus.ppmtool.services;

import com.github.ovchingus.ppmtool.domain.Project;
import com.github.ovchingus.ppmtool.exceptions.ProjectIdException;
import com.github.ovchingus.ppmtool.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
            return projectRepository.save(project);
        } catch (Exception e) {
            throw new ProjectIdException("Project ID '" +
                    project.getProjectIdentifier().toUpperCase() + "' already exists");
        }
    }

    public Project findByProjectIdentifier(String projectId) {
        Optional<Project> project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
        project.orElseThrow(() -> new ProjectIdException("Project ID '" + projectId + "' doesn`t exists"));
        return project.get();
    }

    public Iterable<Project> findAllProjects() {
        return projectRepository.findAll();
    }

    public void deleteProjectByIdentifier(String projectId) {
        Optional<Project> project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
        projectRepository.delete(project.orElseThrow(() ->
                new ProjectIdException("Can`t remove project with ID '" +
                        projectId + "'. The project does not exists")));
    }
}
