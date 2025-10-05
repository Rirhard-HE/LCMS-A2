package com.lcms.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lcms.system.entity.Cases;
import com.lcms.system.entity.Evidence;
import com.lcms.system.security.SecurityUtil;
import com.lcms.system.service.CasesService;
import com.lcms.system.service.EvidenceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Kian
 * @since 2025-09-27
 */
@RestController
@RequestMapping("/evidence")
public class EvidenceController {

    @Autowired
    private EvidenceService evidenceService;

    @Autowired
    private CasesService casesService;

    @Autowired
    private SecurityUtil securityUtil;

    @GetMapping
    public List<Map<String, Object>> list(HttpServletRequest request,
                                          @RequestParam(value = "keyword", required = false) String keyword) {
        Long lawyerId = securityUtil.getCurrentLawyerId(request);
        if (lawyerId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }

        QueryWrapper<Evidence> qw = new QueryWrapper<>();
        qw.eq("uploaded_by", lawyerId);

        if (keyword != null && !keyword.trim().isEmpty()) {
            qw.and(q -> q.like("title", keyword));
        }

        List<Evidence> list = evidenceService.list(qw);

        return list.stream().map(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", e.getId());
            map.put("title", e.getTitle());
            map.put("description", e.getDescription());
            map.put("fileUrl", e.getFileUrl());
            map.put("caseId", e.getCaseId());

            Cases c = casesService.getById(e.getCaseId());
            map.put("caseNum", c != null ? c.getCaseNum() : null);

            map.put("createdAt", e.getCreatedAt());
            map.put("updatedAt", e.getUpdatedAt());
            return map;
        }).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Evidence getById(@PathVariable Long id) {
        return evidenceService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody Evidence evidence) {
        return evidenceService.save(evidence);
    }

    @PutMapping("/{id}")
    public boolean update(@PathVariable Long id, @RequestBody Evidence evidence) {
        evidence.setId(id);
        return evidenceService.updateById(evidence);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return evidenceService.removeById(id);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Evidence upload(HttpServletRequest request,@RequestParam("caseId") Long caseId,
                           @RequestPart("file") MultipartFile file,
                           @RequestParam(value = "title", required = false) String title,
                           @RequestParam(value = "description", required = false) String description) throws IOException {
        Long lawyerId = securityUtil.getCurrentLawyerId(request);

        if (caseId == null || file == null || file.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = getExt(original);
        if (!Arrays.asList("pdf", "doc", "docx").contains(ext)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        Path baseDir = Paths.get(System.getProperty("user.dir"), "backend", "uploads", "case-" + caseId);
        Files.createDirectories(baseDir);
        String storedName = UUID.randomUUID().toString().replace("-", "") + "-" + original;
        Path target = baseDir.resolve(storedName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        String fileUrl = "/uploads/case-" + caseId + "/" + storedName;

        Evidence ev = new Evidence();
        ev.setCaseId(caseId);
        ev.setTitle(title != null ? title : original);
        ev.setDescription(description);
        ev.setFileUrl(fileUrl);
        ev.setMimeType(Files.probeContentType(target));
        ev.setSizeBytes(file.getSize());
        ev.setUploadedBy(lawyerId);
        evidenceService.save(ev);
        return ev;
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Evidence update(@PathVariable Long id,
                           @RequestPart(value = "file", required = false) MultipartFile file,
                           @RequestParam(value = "title", required = false) String title,
                           @RequestParam(value = "description", required = false) String description
    ) throws IOException {
        Evidence ev = evidenceService.getById(id);
        if (ev == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        if (title != null) ev.setTitle(title);
        if (description != null) ev.setDescription(description);

        if (file != null && !file.isEmpty()) {
            String original = StringUtils.cleanPath(file.getOriginalFilename());
            String ext = getExt(original);
            if (!Arrays.asList("pdf", "doc", "docx").contains(ext)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

            Path baseDir = Paths.get(System.getProperty("user.dir"), "backend", "uploads", "case-" + ev.getCaseId());
            Files.createDirectories(baseDir);
            String storedName = UUID.randomUUID().toString().replace("-", "") + "-" + original;
            Path target = baseDir.resolve(storedName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            String fileUrl = "/uploads/case-" + ev.getCaseId() + "/" + storedName;

            ev.setFileUrl(fileUrl);
            ev.setMimeType(Files.probeContentType(target));
            ev.setSizeBytes(file.getSize());
        }

        evidenceService.updateById(ev);
        return ev;
    }

    private String getExt(String name) {
        int dot = name.lastIndexOf('.');
        return (dot >= 0) ? name.substring(dot + 1).toLowerCase() : "";
    }

    @GetMapping("/by-case/{caseId}")
    public List<Evidence> listByCase(@PathVariable Long caseId, HttpServletRequest request) {
        Long lawyerId = securityUtil.getCurrentLawyerId(request);
        if (lawyerId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }
        return evidenceService.lambdaQuery()
                .eq(Evidence::getCaseId, caseId)
                .eq(Evidence::getUploadedBy, lawyerId)
                .list();
    }
}

