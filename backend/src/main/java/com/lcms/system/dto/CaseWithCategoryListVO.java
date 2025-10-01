package com.lcms.system.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CaseWithCategoryListVO {
    private Long id;
    private String title;
    private String caseNum;
    private String description;
    private String status;
    private LocalDateTime hearingAt;
    private Long lawyerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<CategoryDTO> categories = new ArrayList<>();
}

