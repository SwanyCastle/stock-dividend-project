package com.dividend.service;

import com.dividend.exception.impl.AlreadyExistUserException;
import com.dividend.exception.impl.DoseNotMatchedPasswordException;
import com.dividend.exception.impl.UserNotFoundException;
import com.dividend.model.Auth;
import com.dividend.persist.MemberRepository;
import com.dividend.persist.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public MemberEntity register(Auth.SignUp member) {
        boolean exists = memberRepository.existsByUsername(member.getUsername());
        if (exists) {
            throw new AlreadyExistUserException();
        }

        member.setPassword(passwordEncoder.encode(member.getPassword()));
        return memberRepository.save(member.toEntity());
    }

    public MemberEntity authenticate(Auth.SignIn member) {
        MemberEntity user = memberRepository.findByUsername(member.getUsername())
                .orElseThrow(() -> new UserNotFoundException(member.getUsername()));

        if (!passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            throw new DoseNotMatchedPasswordException();
        }

        return user;
    }
}
