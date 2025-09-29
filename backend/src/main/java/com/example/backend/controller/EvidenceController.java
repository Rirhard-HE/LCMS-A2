package com.example.backend.controller;

import com.example.backend.entity.Evidence;
import com.example.backend.service.EvidenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Kian
 * @since 2025-09-27
 */
@RestController
@RequestMapping("/evidence")
public class EvidenceController {

    @Autowired
    private EvidenceService evidenceService;

    @GetMapping
    public List<Evidence> list() {
        return evidenceService.list();
    }

    @GetMapping("/{id}")
    public Evidence getById(@PathVariable Long id) {
        return evidenceService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody Evidence evidence) {
        return evidenceService.save(evidence);
    }

    @PutMapping("/{id}")
    public boolean update(@PathVariable Long id, @RequestBody Evidence evidence) {
        evidence.setId(id);
        return evidenceService.updateById(evidence);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return evidenceService.removeById(id);
    }
}

