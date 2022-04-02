package de.hsa.oosd.timetracking.entities;

import de.hsa.oosd.timetracking.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private CustomUserRepository customUserRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private SecurityService securityService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final CustomUser customUser = customUserRepository.findCustomUserByUsername(username);
        if (customUser == null) throw new UsernameNotFoundException(username);
        return User.withUsername(customUser.getUsername()).password(customUser.getPassword()).authorities(customUser.getRole()).build();
    }

    public CustomUser loadCustomUserByUsername(String username) throws UsernameNotFoundException {
        final CustomUser customUser = customUserRepository.findCustomUserByUsername(username);
        if (customUser == null) throw new UsernameNotFoundException(username);
        return customUser;
    }

    public void createUser(String username, String password, String Role, String organame)throws Exception{
        final CustomUser customUser = customUserRepository.findCustomUserByUsername(username);
        if (customUser != null) throw new Exception("username already in use");
        final Organization orga = organizationRepository.findOrganizationByName(organame);
        if (orga == null) throw new Exception("Something went Wrong with your Organization");
        PasswordEncoder enc = new BCryptPasswordEncoder();
        String encpw = enc.encode(password);
        CustomUser User = new CustomUser(username,encpw,Role, orga);
        try {
            customUserRepository.save(User);
            Employee employee = new Employee(User);
            employeeRepository.save(employee);
        }catch (Exception e ){
            throw new Exception(e.getCause());
        }

    }

    public List<CustomUser> getAllUsersFromOrganization() {

        String OrgaUsername = securityService.getAuthenticatedUser().getUsername();
        CustomUser userforOrgname = loadCustomUserByUsername(OrgaUsername);
        return customUserRepository.findByOrganization(userforOrgname.getOrganization());
    }
    public void delete(CustomUser user){
        customUserRepository.delete(user);
    }
    public void save(CustomUser user){
        customUserRepository.save(user);
    }
}
