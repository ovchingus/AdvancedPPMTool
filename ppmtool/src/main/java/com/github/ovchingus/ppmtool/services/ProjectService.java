package com.github.ovchingus.ppmtool.services;

import com.github.ovchingus.ppmtool.domain.Backlog;
import com.github.ovchingus.ppmtool.domain.Project;
import com.github.ovchingus.ppmtool.domain.User;
import com.github.ovchingus.ppmtool.exceptions.ProjectIdException;
import com.github.ovchingus.ppmtool.exceptions.ProjectNotFoundException;
import com.github.ovchingus.ppmtool.repositories.BacklogRepository;
import com.github.ovchingus.ppmtool.repositories.ProjectRepository;
import com.github.ovchingus.ppmtool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final BacklogRepository backlogRepository;

    private final UserRepository userRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository,
                          BacklogRepository backlogRepository,
                          UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.backlogRepository = backlogRepository;
        this.userRepository = userRepository;
    }

    public Project saveOrUpdate(Project project, String username) {

        if (project.getId() != null) {

            Project existingProject = projectRepository.findByProjectIdentifier(project.getProjectIdentifier())
                    .orElseThrow(() -> new ProjectNotFoundException("Project with ID: '"
                            + project.getProjectIdentifier() + "' cannot be updated because it doesn't exist"));

            if (!existingProject.getProjectLeader().equals(username)) {
                throw new ProjectNotFoundException("Project not found in your account");
            }
        }


        try {

            String pId = project.getProjectIdentifier().toUpperCase();

            // safe beacause of security
            User user = userRepository.findByUsername(username).get();
            project.setUser(user);
            project.setProjectLeader(user.getUsername());
            project.setProjectIdentifier(pId);
             /*  Create Backlog object when Project creates
             or update Backlog id when project updates  */
            if (project.getId() == null) {
                Backlog backlog = new Backlog();
                project.setBacklog(backlog);
                backlog.setProject(project);
                backlog.setProjectIdentifier(pId);
            } else
                project.setBacklog(backlogRepository.findByProjectIdentifier(pId));

            return projectRepository.save(project);
        } catch (Exception e) {
            throw new ProjectIdException("Project ID '" +
                    project.getProjectIdentifier().toUpperCase() + "' already exists");
        }
    }

    public Project findByProjectIdentifier(String projectId, String username) {

        //Only want to return the project if the user looking for it is the owner
        Optional<Project> project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
        project.orElseThrow(() -> new ProjectIdException("Project ID '" + projectId + "' doesn`t exists"));
        if (!project.get().getProjectLeader().equals(username))
            throw new ProjectNotFoundException("Project not found in your account");
        return project.get();
    }

    public Iterable<Project> findAllProjects(String username) {
        return projectRepository.findAllByProjectLeader(username);
    }

    public void deleteProjectByIdentifier(String projectId, String username) {
        projectRepository.delete(findByProjectIdentifier(projectId, username));
    }
}
