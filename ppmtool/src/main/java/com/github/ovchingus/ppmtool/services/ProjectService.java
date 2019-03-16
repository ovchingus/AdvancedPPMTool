package com.github.ovchingus.ppmtool.services;

import com.github.ovchingus.ppmtool.domain.Project;
import com.github.ovchingus.ppmtool.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public Project saveOrUpdate(Project project) {



        return projectRepository.save(project);
    }

}
