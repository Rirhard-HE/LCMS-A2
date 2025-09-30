package com.lcms.system.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lcms.system.entity.Lawyers;
import com.lcms.system.service.LawyersService;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class LawyerUserDetailsService implements UserDetailsService {

    private final LawyersService lawyersService;

    public LawyerUserDetailsService(LawyersService lawyersService) {
        this.lawyersService = lawyersService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Lawyers l = lawyersService.getOne(new LambdaQueryWrapper<Lawyers>().eq(Lawyers::getEmail, email));
        if (l == null) throw new UsernameNotFoundException("User not found");
        boolean enabled = !"DISABLED".equalsIgnoreCase(String.valueOf(l.getStatus()));
        return User.withUsername(l.getEmail())
                .password(l.getPasswordHash())
//                .authorities("ROLE_LAWYER")
                .disabled(!enabled)
                .build();
    }
}
