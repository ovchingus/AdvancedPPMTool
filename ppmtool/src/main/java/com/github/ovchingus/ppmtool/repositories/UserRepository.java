package com.github.ovchingus.ppmtool.repositories;

import com.github.ovchingus.ppmtool.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> getById(Long id);

    Optional<User> findById(Long id);
}
