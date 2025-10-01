package com.lcms.system.service.impl;

import com.lcms.system.entity.Lawyers;
import com.lcms.system.mapper.LawyersMapper;
import com.lcms.system.service.LawyersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LawyersServiceImpl extends ServiceImpl<LawyersMapper, Lawyers> implements LawyersService {
    @Autowired
    private LawyersMapper lawyersMapper;

    @Override
    public Lawyers findByEmail(String email) {
        return lawyersMapper.findByEmail(email);
    }
}
