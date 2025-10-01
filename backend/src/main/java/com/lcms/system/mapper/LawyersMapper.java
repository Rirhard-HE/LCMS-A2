package com.lcms.system.mapper;

import com.lcms.system.entity.Lawyers;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author Kian
 * @since 2025-09-30
 */
public interface LawyersMapper extends BaseMapper<Lawyers> {
    @Select("SELECT * FROM lawyers WHERE email = #{email} LIMIT 1")
    Lawyers findByEmail(@Param("email") String email);
}
