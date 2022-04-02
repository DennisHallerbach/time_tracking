package de.hsa.oosd.timetracking.entities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class OrganizationService {
        @Autowired
        private OrganizationRepository organizationRepository;

        public void createOrga(String name)throws Exception{
            final Organization orga = organizationRepository.findOrganizationByName(name);
            if (orga != null) throw new Exception("organization already in use");
            Organization neworga = new Organization(name);
            try {
                organizationRepository.save(neworga);
            }catch (Exception e ){
                throw new Exception(e.getCause());
            }

        }
        public Organization getOrgbyName ( String name){
            return organizationRepository.findOrganizationByName(name);
        }
    }

