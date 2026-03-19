package com.supremefuneralx.inventory.repository;

import com.supremefuneralx.inventory.entity.ItemIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemIssueRepository extends JpaRepository<ItemIssue, Long> {

    List<ItemIssue> findByItemId(Long itemId);

    @Query("SELECT i FROM ItemIssue i WHERE i.createdAt BETWEEN :startDate AND :endDate ORDER BY i.createdAt DESC")
    List<ItemIssue> findIssuesBetweenDates(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT i FROM ItemIssue i ORDER BY i.createdAt DESC LIMIT 10")
    List<ItemIssue> findRecentIssues();
}
