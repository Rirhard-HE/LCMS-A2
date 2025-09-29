package com.example.backend.service.impl;

import com.example.backend.entity.CaseCategories;
import com.example.backend.mapper.CaseCategoriesMapper;
import com.example.backend.service.CaseCategoriesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CaseCategoriesServiceImpl extends ServiceImpl<CaseCategoriesMapper, CaseCategories> implements CaseCategoriesService {

}
