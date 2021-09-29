package br.com.alura.aluraflix.repository;

import br.com.alura.aluraflix.models.ERole;
import br.com.alura.aluraflix.models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole name);
}
