package com.project.simplegw.system.security;

import com.project.simplegw.member.entities.Member;
import com.project.simplegw.member.services.MemberLoginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberLoginService memberLoginService;
	
	@Autowired
	public UserDetailsServiceImpl(MemberLoginService memberLoginService) {
		this.memberLoginService = memberLoginService;
        log.info("Component '" + this.getClass().getName() + "' has been created.");
	}

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = memberLoginService.getMember(userId);
        if(member.getId() == null) {
            log.warn("User logged in failed. ID not exist. fail userId: {}", userId);
            throw new UsernameNotFoundException(userId);
        
        } else {
            return new LoginUser(member);
        }
    }
}