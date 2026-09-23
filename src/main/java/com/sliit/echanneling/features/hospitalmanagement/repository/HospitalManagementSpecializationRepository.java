package com.sliit.echanneling.features.hospitalmanagement.repository;

import com.sliit.echanneling.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository Pattern: this interface abstracts Specialization database access
 * behind a narrow module-specific contract.
 */
public interface HospitalManagementSpecializationRepository extends JpaRepository<Specialization, Long> {
    List<Specialization> findAllByOrderByNameAsc();
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndSpecializationIdNot(String name, Long specializationId);
    boolean existsByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCaseAndSpecializationIdNot(String code, Long specializationId);

    @Query("""
            select s from Specialization s
            where lower(s.name) like lower(concat('%', :query, '%'))
               or lower(coalesce(s.code, '')) like lower(concat('%', :query, '%'))
               or lower(coalesce(s.description, '')) like lower(concat('%', :query, '%'))
            order by s.name asc
            """)
    List<Specialization> search(@Param("query") String query);
}
