package com.TaskManagement1.Service;

import com.TaskManagement1.DTO.UserProfileUpdateDTO;
import com.TaskManagement1.Entity.UserProfileUpdate;
import com.TaskManagement1.Repository.UserProfileUpdateRepository;
import org.springframework.stereotype.Service;

@Service
public class UserProfileUpdateService {

    private final UserProfileUpdateRepository repository;

    public UserProfileUpdateService(UserProfileUpdateRepository repository) {
        this.repository = repository;
    }

    public UserProfileUpdate updateProfile(
            String email,
            UserProfileUpdateDTO dto
    ) {

        UserProfileUpdate profile = repository
                .findByUserOfficialEmail(email)
                .orElseGet(() -> {
                    UserProfileUpdate p = new UserProfileUpdate();
                    p.setUserOfficialEmail(email);
                    return p;
                });

        profile.setUserName(dto.getUserName());
        profile.setDepartment(dto.getDepartment());
        profile.setDesignation(dto.getDesignation());
        profile.setOrganizationName(dto.getOrganizationName());
        profile.setActive(dto.isActive());

        return repository.save(profile);
    }

    public UserProfileUpdate getProfile(String email) {
        return repository.findByUserOfficialEmail(email)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }
}
