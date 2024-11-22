package com.hackathon.finservice.Repositories;

import com.hackathon.finservice.Entities.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  User findByEmail(String email);
}
