package com.sliit.echanneling.repository;

import com.sliit.echanneling.model.ComplaintCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComplaintCategoryRepository extends JpaRepository<ComplaintCategory, Long> {
    Optional<ComplaintCategory> findByNameIgnoreCase(String name);
}
