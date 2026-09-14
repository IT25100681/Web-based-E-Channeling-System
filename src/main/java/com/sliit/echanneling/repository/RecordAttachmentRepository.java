package com.sliit.echanneling.repository;

import com.sliit.echanneling.model.RecordAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordAttachmentRepository extends JpaRepository<RecordAttachment, Long> {
    List<RecordAttachment> findByRecord_RecordId(Long recordId);
}
