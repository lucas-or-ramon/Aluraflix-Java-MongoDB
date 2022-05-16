package br.com.alura.aluraflix.services;

import br.com.alura.aluraflix.exception.RoleNotFoundException;
import br.com.alura.aluraflix.models.ERole;
import br.com.alura.aluraflix.models.Role;
import br.com.alura.aluraflix.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getRole(ERole eRole) {
        return roleRepository.findByName(eRole).orElseThrow(() -> new RoleNotFoundException("Error: Role is not found."));
    }
}
