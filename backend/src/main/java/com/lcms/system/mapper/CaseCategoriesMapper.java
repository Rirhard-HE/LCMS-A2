package com.lcms.system.mapper;

import com.lcms.system.entity.CaseCategories;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CaseCategoriesMapper extends BaseMapper<CaseCategories> {
    @Insert({
            "<script>",
            "INSERT IGNORE INTO case_categories (case_id, category_id) VALUES",
            "<foreach collection='categoryIds' item='cid' separator=','>",
            "(#{caseId}, #{cid})",
            "</foreach>",
            "</script>"
    })
    int insertBatchWithCategoryIds(@Param("caseId") Long caseId,
                          @Param("categoryIds") List<Long> categoryIds);

    @Delete("DELETE FROM case_categories WHERE case_id = #{caseId}")
    int deleteByCaseId(@Param("caseId") Long caseId);
}