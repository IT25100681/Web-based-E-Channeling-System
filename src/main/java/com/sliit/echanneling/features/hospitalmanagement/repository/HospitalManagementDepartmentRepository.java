package com.sliit.echanneling.features.hospitalmanagement.repository;

import com.sliit.echanneling.model.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository Pattern: this interface provides a focused persistence boundary for
 * Department management queries.
 */
public interface HospitalManagementDepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findAllByOrderByNameAsc();
    boolean existsByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCaseAndDepartmentIdNot(String code, Long departmentId);

    @Override
    @EntityGraph(attributePaths = "hospital")
    Page<Department> findAll(Pageable pageable);

    @Query("""
            select d from Department d
            join fetch d.hospital h
            where lower(d.name) like lower(concat('%', :query, '%'))
               or lower(coalesce(d.code, '')) like lower(concat('%', :query, '%'))
               or lower(h.name) like lower(concat('%', :query, '%'))
            order by h.name asc, d.name asc
            """)
    List<Department> search(@Param("query") String query);

    @Query("select d from Department d join fetch d.hospital h order by h.name asc, d.name asc")
    List<Department> findAllWithHospital();
}
