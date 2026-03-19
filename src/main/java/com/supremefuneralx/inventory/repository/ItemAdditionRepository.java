package com.supremefuneralx.inventory.repository;

import com.supremefuneralx.inventory.entity.ItemAddition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemAdditionRepository extends JpaRepository<ItemAddition, Long> {

    List<ItemAddition> findByItemId(Long itemId);

    List<ItemAddition> findBySupplierId(Long supplierId);

    @Query("SELECT a FROM ItemAddition a WHERE a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    List<ItemAddition> findAdditionsBetweenDates(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM ItemAddition a ORDER BY a.createdAt DESC LIMIT 10")
    List<ItemAddition> findRecentAdditions();
}
