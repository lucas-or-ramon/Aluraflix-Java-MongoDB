package br.com.devcanoa.aluraflix.repository;

import br.com.devcanoa.aluraflix.models.ERole;
import br.com.devcanoa.aluraflix.models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole name);
}
