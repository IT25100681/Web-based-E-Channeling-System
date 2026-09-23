package com.sliit.echanneling.features.hospitalmanagement.repository;

import com.sliit.echanneling.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository Pattern: this interface hides persistence details for Hospital records
 * from the controller and service layers.
 */
public interface HospitalManagementHospitalRepository extends JpaRepository<Hospital, Long> {
    List<Hospital> findAllByOrderByNameAsc();
    List<Hospital> findByActiveTrueOrderByNameAsc();
    boolean existsByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCaseAndHospitalIdNot(String code, Long hospitalId);

    @Query("""
            select h from Hospital h
            where lower(h.name) like lower(concat('%', :query, '%'))
               or lower(coalesce(h.code, '')) like lower(concat('%', :query, '%'))
               or lower(coalesce(h.contactNo, '')) like lower(concat('%', :query, '%'))
            order by h.name asc
            """)
    List<Hospital> search(@Param("query") String query);
}
