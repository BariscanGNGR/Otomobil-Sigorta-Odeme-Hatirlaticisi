package com.bariscan.sigorta_hatirlatici.service;

import com.bariscan.sigorta_hatirlatici.entity.Privilege;
import com.bariscan.sigorta_hatirlatici.entity.Role;
import com.bariscan.sigorta_hatirlatici.exceptions.NotFoundException;
import com.bariscan.sigorta_hatirlatici.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class RoleService {
    private final RoleRepository roleRepository;

    public Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {
        Optional<Role> role = findRoleByName(name);
        if (role.isEmpty()) {
            role = Optional.ofNullable(Role.builder()
                    .name(name)
                    .privileges(privileges)
                    .build());
            roleRepository.save(role.get());
            log.log(Level.TRACE, name + " role created");
        }
        return role.get();
    }

    public String deleteRole(String name) throws NotFoundException {
        Optional<Role> role = roleRepository.findByName(name);
        if (role.isPresent()) {
            roleRepository.delete(role.get());
            log.log(Level.TRACE, name + " role deleted");
            return "Role deleted.";
        }
        log.log(Level.TRACE, name + " role is not found");
        throw new NotFoundException("Role is not found.");
    }

    public Optional<Role> findRoleByName(String name) {
        return roleRepository.findByName(name);
    }
}
