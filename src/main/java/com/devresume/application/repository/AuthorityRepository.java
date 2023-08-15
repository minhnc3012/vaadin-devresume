package com.devresume.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devresume.application.data.entity.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
