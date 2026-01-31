package com.finance.tracker.controller;

import com.finance.tracker.model.Category;
import com.finance.tracker.model.User;
import com.finance.tracker.repository.CategoryRepository;
import com.finance.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Category> getAllCategories() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Category> categories = categoryRepository.findByUserId(user.getId());

        if (categories.isEmpty()) {
            createDefaultCategory("Makanan & Minuman", "EXPENSE", user.getId());
            createDefaultCategory("Transportasi", "EXPENSE", user.getId());
            createDefaultCategory("Belanja Bulanan", "EXPENSE", user.getId());
            createDefaultCategory("Gaji Utama", "INCOME", user.getId());
            createDefaultCategory("Bonus", "INCOME", user.getId());

            categories = categoryRepository.findByUserId(user.getId());
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