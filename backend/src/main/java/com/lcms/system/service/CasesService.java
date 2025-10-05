package com.lcms.system.service;

import com.lcms.system.dto.CaseWithCategoryListVO;
import com.lcms.system.entity.Cases;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lcms.system.vo.CaseRow;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CasesService extends IService<Cases> {
    List<CaseRow> listCases(Long lawyerId, Long categoryId, String keyword,int pageNo, int pageSize);

    boolean saveWithCategories(Cases cases, List<Long> categoryIds);

    boolean updateWithCategories(Cases cases, List<Long> categoryIds);

    CaseWithCategoryListVO getCaseDetail(Long id);
}
