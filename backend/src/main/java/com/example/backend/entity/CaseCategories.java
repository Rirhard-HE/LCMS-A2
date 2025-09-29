package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author Kian
 * @since 2025-09-30
 */
@Getter
@Setter
@TableName("case_categories")
public class CaseCategories implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableField
    private Long caseId;
    @TableField
    private Long categoryId;
}
