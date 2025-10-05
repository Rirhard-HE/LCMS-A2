package com.lcms.system.mapper;

import com.lcms.system.dto.CaseWithCategoryListVO;
import com.lcms.system.dto.CategoryDTO;
import com.lcms.system.entity.Cases;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lcms.system.vo.CaseRow;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CasesMapper extends BaseMapper<Cases> {
    @Select("""
    SELECT
      c.id,
      c.title,
      c.case_num      AS caseNum,
      c.description,
      c.status,
      c.hearing_at    AS hearingAt,
      c.lawyer_id     AS lawyerId,
      c.created_at    AS createdAt,
      c.updated_at    AS updatedAt,
      GROUP_CONCAT(DISTINCT cat.name ORDER BY cat.name SEPARATOR ',') AS categoryNames
    FROM cases c
    LEFT JOIN case_categories cc ON cc.case_id = c.id
    LEFT JOIN categories cat ON cat.id = cc.category_id
    WHERE (#{lawyerId} IS NULL OR c.lawyer_id = #{lawyerId})
      AND (#{categoryId} IS NULL OR EXISTS (
            SELECT 1 FROM case_categories x
            WHERE x.case_id = c.id AND x.category_id = #{categoryId}
          ))
      AND (#{keyword} IS NULL 
           OR c.title LIKE CONCAT('%', #{keyword}, '%'))
    GROUP BY c.id
    ORDER BY c.updated_at DESC
    LIMIT #{offset}, #{limit}
""")
    List<CaseRow> listCases(@Param("lawyerId") Long lawyerId,
                            @Param("categoryId") Long categoryId,
                            @Param("keyword") String keyword,
                            @Param("offset") int offset,
                            @Param("limit") int limit);


    @Select("""
    SELECT
      c.id,
      c.title,
      c.case_num   AS caseNum,
      c.description,
      c.status,
      c.hearing_at AS hearingAt,
      c.lawyer_id  AS lawyerId,
      c.created_at AS createdAt,
      c.updated_at AS updatedAt
    FROM cases c
    WHERE c.id = #{id}
    """)
    CaseWithCategoryListVO getCaseById(@Param("id") Long id);

    @Select("""
    SELECT cat.id, cat.name, cat.description, cat.created_at AS createdAt, cat.updated_at AS updatedAt
    FROM categories cat
    INNER JOIN case_categories cc ON cc.category_id = cat.id
    WHERE cc.case_id = #{caseId}
    """)
    List<CategoryDTO> getCategoriesByCaseId(@Param("caseId") Long caseId);

}
