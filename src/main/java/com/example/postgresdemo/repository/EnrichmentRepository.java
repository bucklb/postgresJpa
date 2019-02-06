package com.example.postgresdemo.repository;

import com.example.postgresdemo.model.EnrichmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrichmentRepository extends JpaRepository<EnrichmentEntity, Long> {
    List<EnrichmentEntity> findByBirthId(Long birthId);

}
