package com.bariscan.sigorta_hatirlatici.service;

import com.bariscan.sigorta_hatirlatici.entity.Privilege;
import com.bariscan.sigorta_hatirlatici.exceptions.NotFoundException;
import com.bariscan.sigorta_hatirlatici.repository.PrivilegeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class PrivilegeService {
    private final PrivilegeRepository privilegeRepository;

    public Privilege createPrivilegeIfNotFound(String name) {
        Optional<Privilege> privilege = privilegeRepository.findByName(name);
        if (privilege.isEmpty()) {
            privilege = Optional.of(Privilege.builder()
                    .name(name)
                    .build());
            privilege = Optional.ofNullable(privilegeRepository.save(privilege.get()));
            log.log(Level.TRACE,name+" privilege created");
        }
        return privilege.get();
    }

    public String deletePrivilege(String name) throws NotFoundException {
        Optional<Privilege> privilege = privilegeRepository.findByName(name);
        if(privilege.isPresent()){
            privilegeRepository.delete(privilege.get());
            log.log(Level.TRACE,name+" privilege deleted");
            return "Privilege deleted.";
        }
        log.log(Level.TRACE,name+" privilege is not found");
        throw new NotFoundException("Privilege is not found.");
    }

    public Optional<Privilege> findPrivilegeByName(String name) {
        return privilegeRepository.findByName(name);
    }
}
