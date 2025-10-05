package com.lcms.system;

import com.lcms.system.entity.Cases;
import com.lcms.system.service.CasesService;
import com.lcms.system.vo.CaseRow;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("Cases CRUD Function Tests by SpringBootTest")
class CasesServiceCrudTest {

    @Autowired
    private CasesService casesService;

    private Cases newCase(String title, String caseNum) {
        Cases c = new Cases();
        c.setTitle(title);
        c.setCaseNum(caseNum);
        c.setStatus("OPEN");
        c.setLawyerId(1L);
        return c;
    }

    // -------------------- Create --------------------
    @Nested
    @DisplayName("Create Function Test")
    class CreateTests {

        @Test
        @DisplayName("✓ should create a new case successfully")
        void shouldCreateCaseSuccessfully() {
            Cases c = newCase("My Case", "CASE-001");
            boolean ok = casesService.save(c);
            assertTrue(ok);
            assertNotNull(c.getId());
        }

        @Test
        @DisplayName("✓ should create multiple cases and get different IDs")
        void shouldCreateMultipleCasesWithDifferentIds() {
            Cases a = newCase("A", "CASE-A");
            Cases b = newCase("B", "CASE-B");
            assertTrue(casesService.save(a));
            assertTrue(casesService.save(b));
            assertNotNull(a.getId());
            assertNotNull(b.getId());
            assertNotEquals(a.getId(), b.getId());
        }
    }

    // -------------------- Read (Get/List) --------------------
    @Nested
    @DisplayName("Get/List Function Test")
    class ReadTests {

        @Test
        @DisplayName("✓ should get case by id successfully")
        void shouldGetCaseById() {
            Cases c = newCase("Case C", "CASE-GET-1");
            casesService.save(c);

            Cases found = casesService.getById(c.getId());
            assertNotNull(found);
            assertEquals("Case C", found.getTitle());
            assertEquals("CASE-GET-1", found.getCaseNum());
        }

        @Test
        @DisplayName("✓ should list cases for lawyer with optional filters")
        void shouldListCasesSuccessfully() {
            // 插入 3 条数据
            for (int i = 1; i <= 3; i++) {
                casesService.save(newCase("List-" + i, "CASE-LIST-" + i));
            }
            // 仅按律师ID拉取
            List<CaseRow> rows = casesService.listCases(1L, null, null, 1, 10);
            assertNotNull(rows);
            assertTrue(rows.size() >= 3);
        }

        @Test
        @DisplayName("✓ should return null when id not found")
        void shouldReturnNullWhenNotFound() {
            Cases missing = casesService.getById(999999L);
            assertNull(missing);
        }
    }

    // -------------------- Update --------------------
    @Nested
    @DisplayName("Update Function Test")
    class UpdateTests {

        @Test
        @DisplayName("✓ should update case successfully")
        void shouldUpdateCaseSuccessfully() {
            Cases c = newCase("Old", "CASE-UP-1");
            casesService.save(c);

            c.setDescription("Updated description");
            boolean ok = casesService.updateById(c);
            assertTrue(ok);

            Cases after = casesService.getById(c.getId());
            assertEquals("Updated description", after.getDescription());
        }

        @Test
        @DisplayName("✓ should return false when updating non-existing id")
        void shouldNotUpdateNonExisting() {
            Cases c = newCase("X", "CASE-UP-404");
            c.setId(987654321L); // 并未保存
            boolean ok = casesService.updateById(c);
            assertFalse(ok);
        }
    }

    // -------------------- Delete --------------------
    @Nested
    @DisplayName("Delete Function Test")
    class DeleteTests {

        @Test
        @DisplayName("✓ should delete a case successfully")
        void shouldDeleteCaseSuccessfully() {
            Cases c = newCase("ToDelete", "CASE-DEL-1");
            casesService.save(c);

            boolean ok = casesService.removeById(c.getId());
            assertTrue(ok);
            assertNull(casesService.getById(c.getId()));
        }

        @Test
        @DisplayName("✓ should return false if case id not found")
        void shouldReturnFalseWhenNotFound() {
            boolean ok = casesService.removeById(111222333L);
            assertFalse(ok);
        }
    }
}
