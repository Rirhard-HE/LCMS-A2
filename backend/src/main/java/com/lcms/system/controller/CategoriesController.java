package com.lcms.system.controller;

import com.lcms.system.entity.Categories;
import com.lcms.system.service.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * @author Kian
 * @since 2025-09-27
 */
@RestController
@RequestMapping("/categories")
public class CategoriesController {

    @Autowired
    private CategoriesService categoriesService;

    @GetMapping
    public List<Categories> list(@RequestParam(required = false) String name) {
        if (name != null && !name.isEmpty()) {
            return categoriesService.lambdaQuery()
                    .like(Categories::getName, name)
                    .list();
        } else {
            return categoriesService.list();
        }
    }

    @GetMapping("/{id}")
    public Categories getById(@PathVariable Long id) {
        return categoriesService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody Categories categories) {
        return categoriesService.save(categories);
    }

    @PutMapping("/{id}")
    public boolean update(@PathVariable Long id, @RequestBody Categories categories) {
        categories.setId(id);
        return categoriesService.updateById(categories);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return categoriesService.removeById(id);
    }
}

