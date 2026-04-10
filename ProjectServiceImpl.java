package com.TaskManagement1.Service;

import com.TaskManagement1.DTO.ProjectDTO;
import com.TaskManagement1.Entity.Project;
import com.TaskManagement1.Repository.ProjectRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Project createProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        return projectRepository.save(project);
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    @Override
    public Project updateProject(Long id, ProjectDTO projectDTO) {
        Project project = getProjectById(id);
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        return projectRepository.save(project);
    }

    @Override
    public void deleteProject(Long id) {
        Project project = getProjectById(id);
        projectRepository.delete(project);
    }
}
