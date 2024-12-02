package com.hackathon.finservice.Repositories;

import com.hackathon.finservice.Entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

  Optional<User> findByEmail(String email);

  User getUserByEmail(String email);
}
