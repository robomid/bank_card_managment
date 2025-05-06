package org.example.bankcardmanagement.repository;

import org.example.bankcardmanagement.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Set<Role> findByRoleNameIn(Set<String> roleNames);
}
