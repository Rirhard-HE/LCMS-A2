package com.lcms.system.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lcms.system.entity.Cases;
import com.lcms.system.security.SecurityUtil;
import com.lcms.system.service.CasesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * @author Kian
 * @since 2025-09-27
 */
@RestController
@RequestMapping("/cases")
public class CasesController {

    @Autowired
    private CasesService casesService;

    @GetMapping
    public List<Cases> list(HttpServletRequest request) {
        Long lawyerId = SecurityUtil.getCurrentLawyerId(request);
        return casesService.lambdaQuery()
                .eq(Cases::getLawyerId, lawyerId)
                .list();
    }

    @GetMapping("/{id}")
    public Cases getById(@PathVariable Long id) {
        return casesService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody Cases cases) {
        return casesService.save(cases);
    }

    @PutMapping("/{id}")
    public boolean update(@PathVariable Long id, @RequestBody Cases cases) {
        cases.setId(id);
        return casesService.updateById(cases);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return casesService.removeById(id);
    }
}

