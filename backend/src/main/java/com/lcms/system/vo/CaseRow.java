package com.lcms.system.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public class CaseRow {
    private Long id;
    private String title;
    private String caseNum;
    private String description;
    private String status;            // OPEN / CLOSED
    private LocalDateTime hearingAt;
    private Long lawyerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonIgnore
    private String categoryNames;

    @JsonProperty("categories")
    public List<String> getCategories() {
        if (categoryNames == null || categoryNames.isEmpty()) return Collections.emptyList();
        return Arrays.stream(categoryNames.split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).distinct().toList();
    }
}

