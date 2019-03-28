package com.github.ovchingus.ppmtool.services;

import com.github.ovchingus.ppmtool.domain.Backlog;
import com.github.ovchingus.ppmtool.domain.ProjectTask;
import com.github.ovchingus.ppmtool.repositories.BacklogRepository;
import com.github.ovchingus.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectTaskService {

    private final BacklogRepository backlogRepository;

    private final ProjectTaskRepository projectTaskRepository;

    @Autowired
    public ProjectTaskService(BacklogRepository backlogRepository, ProjectTaskRepository projectTaskRepository) {
        this.backlogRepository = backlogRepository;
        this.projectTaskRepository = projectTaskRepository;
    }

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {

        //Exception: Project not found

        //PTs to be added to a specific project, project != null, BL exist
        Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);
        //set the bl to pt
        projectTask.setBacklog(backlog);

        // we want our project sequence to be like this: IDPRO-1 IDPRO-2 ...100 101
        Integer backlogSequence = backlog.getPTSequence();
        // Update the BL SEQUENCE
        backlogSequence++;
        backlog.setPTSequence(backlogSequence);

        //Add sequence to Project Task
        projectTask.setProjectSequence(projectIdentifier + "-" + backlogSequence);
        projectTask.setProjectIdentifier(projectIdentifier);

        // INITIAL priority when priority null
        if (projectTask.getPriority() == null) {
            projectTask.setPriority(3);
        }

        //INITIAL status when status is null
        if (projectTask.getStatus().equals("") || projectTask.getStatus() == null) {
            projectTask.setStatus("TO_DO");
        }

        return projectTaskRepository.save(projectTask);
    }
}
