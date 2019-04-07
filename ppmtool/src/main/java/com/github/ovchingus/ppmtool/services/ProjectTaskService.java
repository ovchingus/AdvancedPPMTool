package com.github.ovchingus.ppmtool.services;

import com.github.ovchingus.ppmtool.domain.Backlog;
import com.github.ovchingus.ppmtool.domain.ProjectTask;
import com.github.ovchingus.ppmtool.exceptions.ProjectNotFoundException;
import com.github.ovchingus.ppmtool.repositories.BacklogRepository;
import com.github.ovchingus.ppmtool.repositories.ProjectRepository;
import com.github.ovchingus.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectTaskService {

    private final BacklogRepository backlogRepository;

    private final ProjectTaskRepository projectTaskRepository;

    private final ProjectRepository projectRepository;

    private ProjectService projectService;

    @Autowired
    public ProjectTaskService(BacklogRepository backlogRepository,
                              ProjectTaskRepository projectTaskRepository,
                              ProjectRepository projectRepository, ProjectService projectService) {
        this.backlogRepository = backlogRepository;
        this.projectTaskRepository = projectTaskRepository;
        this.projectRepository = projectRepository;
        this.projectService = projectService;
    }

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask,
                                      String username) {

        //Exception: Project not found
        try {
            //PTs to be added to a specific project, project != null, BL exist
            Backlog backlog = projectService.findByProjectIdentifier(projectIdentifier, username).getBacklog();
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
            /*
            if (projectTask.getPriority() == null || projectTask.getPriority() == 0) {
                projectTask.setPriority(3);
            }*/


            //INITIAL status when status is null
            if (projectTask.getStatus() == null || projectTask.getStatus().equals("")) {
                projectTask.setStatus("TO_DO");
            }

            return projectTaskRepository.save(projectTask);
        } catch (Exception e) {
            throw new ProjectNotFoundException("Project not found");
        }

    }

    public Iterable<ProjectTask> findBacklogById(String id, String username) {

        projectService.findByProjectIdentifier(id, username);

        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

    public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id, String username) {
        //make sure to search on the right backlog
        projectService.findByProjectIdentifier(backlog_id, username);

        //make sure that task exist
        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id).orElseThrow(() ->
                new ProjectNotFoundException("Project Task '" + pt_id + "' not found"));

        //make sure that backlog/project id in the path corresponds to the right project
        if (!projectTask.getProjectIdentifier().equals(backlog_id))
            throw new ProjectNotFoundException("Project Task '" + pt_id +
                    "' does not exist in project: '" + backlog_id + "'");

        return projectTask;
    }

    @SuppressWarnings("UnusedAssignment")
    public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog_id, String pt_id,
                                               String username) {
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);
        projectTask = updatedTask;
        return projectTaskRepository.save(projectTask);
    }

    public void deletePTByProjectSequence(String backlog_id, String pt_id, String username) {
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);
        projectTaskRepository.delete(projectTask);
    }
}
