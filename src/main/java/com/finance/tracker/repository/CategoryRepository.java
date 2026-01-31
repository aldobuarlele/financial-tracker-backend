package com.finance.tracker.repository;

import com.finance.tracker.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Mencari kategori berdasarkan User ID
    List<Category> findByUserId(Long userId);

    // Mencari kategori berdasarkan User ID dan Type (misal: ambil yg EXPENSE saja)
    List<Category> findByUserIdAndType(Long userId, String type);
}