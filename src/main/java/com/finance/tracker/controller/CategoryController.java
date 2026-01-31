package com.finance.tracker.controller;

import com.finance.tracker.model.Category;
import com.finance.tracker.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public List<Category> getAllCategories() {
        Long userId = 1L;
        List<Category> categories = categoryRepository.findByUserId(userId);

        if (categories.isEmpty()) {
            createDefaultCategory("Makanan & Minuman", "EXPENSE", userId);
            createDefaultCategory("Transportasi", "EXPENSE", userId);
            createDefaultCategory("Belanja Bulanan", "EXPENSE", userId);
            createDefaultCategory("Gaji Utama", "INCOME", userId);
            createDefaultCategory("Bonus", "INCOME", userId);

            categories = categoryRepository.findByUserId(userId);
        }

        return categories;
    }

    private void createDefaultCategory(String name, String type, Long userId) {
        Category c = new Category();
        c.setName(name);
        c.setType(type);
        c.setUserId(userId);
        categoryRepository.save(c);
    }
}