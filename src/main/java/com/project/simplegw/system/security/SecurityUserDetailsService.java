package com.project.simplegw.system.security;

import com.project.simplegw.member.entities.Member;
import com.project.simplegw.member.services.MemberService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MemberService memberService;
	
	@Autowired
	public SecurityUserDetailsService(MemberService memberService) {
		this.memberService = memberService;
	}

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = memberService.searchByUserId(userId);
        if(member.getId() == null) {
            logger.warn("User logged in failed. ID not exist. fail userId: {}", userId);
            throw new UsernameNotFoundException(userId);
        } else {
            return new SecurityUser(member);
        }
    }
}