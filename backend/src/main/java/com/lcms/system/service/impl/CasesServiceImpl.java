package com.lcms.system.service.impl;

import com.lcms.system.dto.CaseWithCategoryListVO;
import com.lcms.system.dto.CategoryDTO;
import com.lcms.system.entity.Cases;
import com.lcms.system.mapper.CaseCategoriesMapper;
import com.lcms.system.mapper.CasesMapper;
import com.lcms.system.service.CasesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lcms.system.vo.CaseRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Kian
 * @since 2025-09-30
 */
@Service
public class CasesServiceImpl extends ServiceImpl<CasesMapper, Cases> implements CasesService {
    @Autowired
    private CasesMapper casesMapper;

    @Autowired
    private CaseCategoriesMapper caseCategoriesMapper;


    @Override
    public List<CaseRow> listCases(Long lawyerId, Long categoryId, String keyword,int pageNo, int pageSize) {
        int safePageNo   = Math.max(pageNo, 1);
        int safePageSize = Math.max(pageSize, 1);
        int offset = (safePageNo - 1) * safePageSize;
        return casesMapper.listCases(lawyerId, categoryId, keyword,offset, safePageSize);
    }

    @Transactional
    @Override
    public boolean saveWithCategories(Cases cases, List<Long> categoryIds) {
        boolean ok = this.save(cases);
        if (!ok) return false;

        if (categoryIds != null && !categoryIds.isEmpty()) {
            caseCategoriesMapper.insertBatchWithCategoryIds(cases.getId(), categoryIds);
        }
        return true;
    }

    @Transactional
    @Override
    public boolean updateWithCategories(Cases cases, List<Long> categoryIds) {
        boolean ok = this.updateById(cases);
        if (!ok) return false;

        caseCategoriesMapper.deleteByCaseId(cases.getId());
        if (categoryIds != null && !categoryIds.isEmpty()) {
            caseCategoriesMapper.insertBatchWithCategoryIds(cases.getId(), categoryIds);
        }
        return true;
    }

    public CaseWithCategoryListVO getCaseDetail(Long id) {
        CaseWithCategoryListVO vo = casesMapper.getCaseById(id);
        if (vo != null) {
            List<CategoryDTO> cats = casesMapper.getCategoriesByCaseId(id);
            vo.setCategories(cats);
        }
        return vo;
    }

}
