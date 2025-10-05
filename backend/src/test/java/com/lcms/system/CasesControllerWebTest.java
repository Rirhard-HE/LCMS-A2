package com.lcms.system;

import com.lcms.system.controller.CasesController;
import com.lcms.system.service.CasesService;
import com.lcms.system.vo.CaseRow;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;


import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CasesController.class)
class CasesControllerWebTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CasesService casesService;

    @Test
    void listCases_shouldReturn200() throws Exception {
        CaseRow row = new CaseRow();
        row.setId(1L);
        row.setTitle("Test Case");
        row.setCaseNum("CASE-001");
        row.setDescription("desc");
        row.setStatus("OPEN");
        row.setHearingAt(LocalDateTime.parse("2025-09-30T10:00"));
        row.setLawyerId(1L);
        row.setCreatedAt(LocalDateTime.parse("2025-09-30T10:00"));
        row.setUpdatedAt(LocalDateTime.parse("2025-09-30T10:00"));
        row.setCategoryNames("Civil");
        List<CaseRow> mock = List.of(row);


        Mockito.when(casesService.listCases(any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(mock);

        mvc.perform(get("/cases").param("pageNo","1").param("pageSize","10"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content().string(Matchers.containsString("Test Case")));
    }
}
