package com.Kalakriti.Kalakriti.repository;

import com.Kalakriti.Kalakriti.entity.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NewsletterRepository extends JpaRepository<Newsletter, Long> {

    boolean existsByEmail(String email);

    Optional<Newsletter> findByEmail(String email);
}