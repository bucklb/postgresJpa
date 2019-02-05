package com.example.postgresdemo.repository;

import com.example.postgresdemo.model.BirthCaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BirthRepository extends JpaRepository<BirthCaseEntity, Long> {
}
