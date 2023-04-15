package com.bariscan.sigorta_hatirlatici.repository;

import com.bariscan.sigorta_hatirlatici.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
}
