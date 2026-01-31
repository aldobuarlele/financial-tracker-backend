package com.finance.tracker.controller;

import com.finance.tracker.dto.CategoryRequest;
import com.finance.tracker.model.Category;
import com.finance.tracker.model.User;
import com.finance.tracker.repository.CategoryRepository;
import com.finance.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Category> getAllCategories(@RequestParam(required = false) String type) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Category> categories;

        if (type != null && !type.isEmpty()) {
            categories = categoryRepository.findByUserIdAndType(user.getId(), type);
        } else {
            categories = categoryRepository.findByUserId(user.getId());
        }

        if (categories.isEmpty() && categoryRepository.findByUserId(user.getId()).isEmpty()) {
            createDefaultCategory("Makanan & Minuman", "EXPENSE", user.getId());
            createDefaultCategory("Transportasi", "EXPENSE", user.getId());
            createDefaultCategory("Gaji Utama", "INCOME", user.getId());
            if (type != null) {
                categories = categoryRepository.findByUserIdAndType(user.getId(), type);
            } else {
                categories = categoryRepository.findByUserId(user.getId());
            }
        }

        return categories;
    }

    @PostMapping
    public Category createCategory(@RequestBody CategoryRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = new Category();
        category.setName(request.getName());
        category.setType(request.getType());
        category.setUserId(user.getId());

        return categoryRepository.save(category);
    }

    private void createDefaultCategory(String name, String type, Long userId) {
        Category c = new Category();
        c.setName(name);
        c.setType(type);
        c.setUserId(userId);
        categoryRepository.save(c);
    }
}