
package com.campuscart.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "categories",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_categories_name",
        columnNames = "name"
    )
)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(length = 250)
    private String description;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
