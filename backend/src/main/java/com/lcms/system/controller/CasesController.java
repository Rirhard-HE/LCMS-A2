package com.lcms.system.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lcms.system.dto.CaseWithCategoryListVO;
import com.lcms.system.entity.Cases;
import com.lcms.system.security.SecurityUtil;
import com.lcms.system.service.CasesService;
import com.lcms.system.vo.CaseRow;
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

    @Autowired
    private SecurityUtil securityUtil;


    @GetMapping
    public List<CaseRow> list(HttpServletRequest request,
                              @RequestParam(required = false) Long categoryId,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(defaultValue = "1") int pageNo,
                              @RequestParam(defaultValue = "10") int pageSize,
                              @RequestParam(defaultValue = "false") boolean all) {
        Long lawyerId = securityUtil.getCurrentLawyerId(request);
        return casesService.listCases(lawyerId, categoryId,keyword, pageNo, pageSize);
    }

    @GetMapping("/{id}")
    public CaseWithCategoryListVO getById(@PathVariable Long id) {
        return casesService.getCaseDetail(id);
    }

    @PostMapping
    public boolean save(HttpServletRequest request,
                        @RequestBody Cases cases,
                        @RequestParam(name = "categoryId", required = false) List<Long> categoryIds) {
        Long lawyerId = securityUtil.getCurrentLawyerId(request);
        cases.setLawyerId(lawyerId);
        return casesService.saveWithCategories(cases, categoryIds);
    }

    @PutMapping("/{id}")
    public boolean update(@PathVariable Long id,
                          @RequestBody Cases cases,
                          @RequestParam(name = "categoryId", required = false) List<Long> categoryIds) {
        cases.setId(id);
        return casesService.updateWithCategories(cases, categoryIds);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return casesService.removeById(id);
    }
}

