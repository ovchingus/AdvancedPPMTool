package com.github.ovchingus.ppmtool.services;

import com.github.ovchingus.ppmtool.domain.Backlog;
import com.github.ovchingus.ppmtool.domain.ProjectTask;
import com.github.ovchingus.ppmtool.exceptions.ProjectNotFoundException;
import com.github.ovchingus.ppmtool.repositories.BacklogRepository;
import com.github.ovchingus.ppmtool.repositories.ProjectRepository;
import com.github.ovchingus.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectTaskService {

    private final BacklogRepository backlogRepository;

    private final ProjectTaskRepository projectTaskRepository;

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectTaskService(BacklogRepository backlogRepository,
                              ProjectTaskRepository projectTaskRepository,
                              ProjectRepository projectRepository) {
        this.backlogRepository = backlogRepository;
        this.projectTaskRepository = projectTaskRepository;
        this.projectRepository = projectRepository;
    }

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {

        //Exception: Project not found
        try {
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
            if (projectTask.getStatus() == null || projectTask.getStatus().equals("")) {
                projectTask.setStatus("TO_DO");
            }

            return projectTaskRepository.save(projectTask);
        } catch (Exception e) {
            throw new ProjectNotFoundException("Project not found");
        }

    }

    public Iterable<ProjectTask> findBacklogById(String id) {

        projectRepository.findByProjectIdentifier(id).orElseThrow(() ->
                new ProjectNotFoundException("Project with ID: '" + id + "' does not exist"));

        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

    public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id) {
        //make sure to search on the right backlog
        projectRepository.findByProjectIdentifier(backlog_id).orElseThrow(() ->
                new ProjectNotFoundException("Project with ID: '" + backlog_id + "' does not exist"));

        //make sure that task exist
        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id).orElseThrow(() ->
                new ProjectNotFoundException("Project Task '" + pt_id + "' not found"));

        //make sure that backlog/project id in the path corresponds to the right project
        if (!projectTask.getProjectIdentifier().equals(backlog_id))
            throw new ProjectNotFoundException("Project Task '" + pt_id +
                    "' does not exist in project: '" + backlog_id + "'");

        return projectTask;
    }

    public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog_id, String pt_id){
        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id).get();

        projectTask = updatedTask;

        return projectTaskRepository.save(projectTask);

    };
}
