package de.hsa.oosd.timetracking.entities;

import de.hsa.oosd.timetracking.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    public void createProject(String name) throws Exception {
        final Project project = projectRepository.findProjectByName(name);
        if (project != null) throw new Exception("Projectname already in use");
        String OrgaUsername = securityService.getAuthenticatedUser().getUsername();
        CustomUser userforOrgname = customUserDetailsService.loadCustomUserByUsername(OrgaUsername);
        Project newProject = new Project(name, userforOrgname.getOrganization());
        try {
            projectRepository.save(newProject);
        } catch (Exception e) {
            throw new Exception(e.getCause());
        }
    }

    public List<Project> getAllProjectsFromOrganization() {

        String OrgaUsername = securityService.getAuthenticatedUser().getUsername();
        CustomUser userforOrgname = customUserDetailsService.loadCustomUserByUsername(OrgaUsername);
        return projectRepository.findByOrganization(userforOrgname.getOrganization());
    }

    public void delete(Project project) {
        projectRepository.delete(project);
    }

    public void save(Project project) {
        projectRepository.save(project);
    }
}