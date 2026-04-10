package com.TaskManagement1.Service;

import com.TaskManagement1.DTO.ProjectDTO;
import com.TaskManagement1.Entity.Project;
import java.util.List;

public interface ProjectService {
    Project createProject(ProjectDTO projectDTO);
    List<Project> getAllProjects();
    Project getProjectById(Long id);
    Project updateProject(Long id, ProjectDTO projectDTO);
    void deleteProject(Long id);
}
