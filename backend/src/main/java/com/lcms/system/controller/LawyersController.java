package com.lcms.system.controller;

import com.lcms.system.entity.Lawyers;
import com.lcms.system.service.LawyersService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Kian
 * @since 2025-09-30
 */
@RestController
@RequestMapping("/lawyers")
public class LawyersController {

    @Autowired
    private LawyersService lawyersService;

    @GetMapping
    public List<Lawyers> list() {
        return lawyersService.list();
    }

    @GetMapping("/{id}")
    public Lawyers getById(@PathVariable Long id) {
        return lawyersService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody Lawyers lawyers) {
        return lawyersService.save(lawyers);
    }

    @PutMapping("/{id}")
    public boolean update(@PathVariable Long id, @RequestBody Lawyers lawyers) {
        lawyers.setId(id);
        return lawyersService.updateById(lawyers);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return lawyersService.removeById(id);
    }
}

