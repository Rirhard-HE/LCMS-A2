package com.example.backend.service.impl;

import com.example.backend.entity.Categories;
import com.example.backend.mapper.CategoriesMapper;
import com.example.backend.service.CategoriesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Kian
 * @since 2025-09-30
 */
@Service
public class CategoriesServiceImpl extends ServiceImpl<CategoriesMapper, Categories> implements CategoriesService {

}
