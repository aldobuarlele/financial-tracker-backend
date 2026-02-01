package com.finance.tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;

    @Column(name = "user_id")
    private Long userId;


    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonIgnoreProperties({"parent", "hibernateLazyInitializer", "handler"})
    private Category parent;


    public Category getParent() { return parent; }
    public void setParent(Category parent) { this.parent = parent; }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}