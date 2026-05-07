package com.internship.tool.repository;

import com.internship.tool.entity.MeetingMinutes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MeetingMinutesRepository extends JpaRepository<MeetingMinutes, Long> {

    // Find all non-deleted records (paginated)
    Page<MeetingMinutes> findAllByIsDeletedFalse(Pageable pageable);

    // Find by ID — only non-deleted
    Optional<MeetingMinutes> findByIdAndIsDeletedFalse(Long id);

    // Search by title or content (case-insensitive)
    @Query("SELECT m FROM MeetingMinutes m WHERE m.isDeleted = false AND " +
           "(LOWER(m.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.content) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<MeetingMinutes> searchByTitleOrContent(@Param("query") String query, Pageable pageable);

    // Filter by status
    Page<MeetingMinutes> findAllByStatusAndIsDeletedFalse(String status, Pageable pageable);

    // Filter by date range
    Page<MeetingMinutes> findAllByMeetingDateBetweenAndIsDeletedFalse(
            LocalDate startDate, LocalDate endDate, Pageable pageable);

    // Count by status (for dashboard KPIs)
    long countByStatusAndIsDeletedFalse(String status);

    // Count all non-deleted
    long countByIsDeletedFalse();
}
