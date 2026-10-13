
package com.campuscart.config;

import com.campuscart.entity.Category;
import com.campuscart.repository.CategoryRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoryDataInitializer {

    @Bean
    CommandLineRunner initializeCategories(CategoryRepository repository) {
        return args -> {
            List<Category> defaults = List.of(
                category("Calculators and Electronics",
                    "Calculators and useful electronic devices"),
                category("Books and Study Materials",
                    "Textbooks, notes, and reference materials"),
                category("Lab Equipment",
                    "Lab coats, goggles, and laboratory supplies"),
                category("Engineering Tools",
                    "Engineering instruments and technical tools"),
                category("Stationery",
                    "Pens, notebooks, and everyday stationery"),
                category("Hostel Essentials",
                    "Items for hostel and dormitory living"),
                category("Sports Equipment",
                    "Sports gear and fitness equipment"),
                category("Bags and Accessories",
                    "Backpacks, bags, and student accessories"),
                category("Other",
                    "Other university-related items")
            );

            for (Category category : defaults) {
                if (!repository.existsByNameIgnoreCase(category.getName())) {
                    repository.save(category);
                }
            }
        };
    }

    private Category category(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return category;
    }
}
