package com.supremefuneralx.inventory.repository;

import com.supremefuneralx.inventory.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByName(String name);

    @Query("SELECT s FROM Supplier s WHERE s.name LIKE %:searchTerm% OR s.email LIKE %:searchTerm% OR s.phoneNumber LIKE %:searchTerm%")
    List<Supplier> searchSuppliers(@Param("searchTerm") String searchTerm);
}
