// BoardMinutesRepository.java: Spring Data JPA repository for BoardMinutes entity.
// Provides paginated queries, soft-delete filtering, full-text search, and status stats.

package com.internship.tool.repository;

import com.internship.tool.entity.BoardMinutes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BoardMinutesRepository extends JpaRepository<BoardMinutes, UUID> {

    /**
     * Returns all non-deleted board minutes with pagination.
     * The @SQLRestriction on the entity already filters deleted_at IS NULL,
     * but this explicit method is kept for clarity and direct service use.
     */
    Page<BoardMinutes> findAllByDeletedAtIsNull(Pageable pageable);

    /**
     * Returns a single non-deleted board minute by id.
     */
    Optional<BoardMinutes> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Case-insensitive search by title or content, excluding soft-deleted records.
     */
    @Query("SELECT b FROM BoardMinutes b " +
           "WHERE b.deletedAt IS NULL " +
           "AND (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<BoardMinutes> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Returns count of board minutes grouped by status (excluding soft-deleted).
     * Result is a list of Object[] where [0]=status, [1]=count.
     */
    @Query("SELECT b.status, COUNT(b) FROM BoardMinutes b " +
           "WHERE b.deletedAt IS NULL " +
           "GROUP BY b.status")
    List<Object[]> countGroupedByStatus();

    /**
     * Returns all non-deleted records (no pagination) for CSV export.
     */
    @Query("SELECT b FROM BoardMinutes b WHERE b.deletedAt IS NULL ORDER BY b.meetingDate DESC")
    List<BoardMinutes> findAllForExport();
}
