package com.dong.good.Member.service;

import com.dong.good.Member.domain.Member;
import com.dong.good.Member.repository.MemberRepository;
import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class MemberService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member){
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }


    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException { // 로그인 할 유저의 email을 파라미터로 html에서 전달 받음

        Member member = memberRepository.findByEmail(email);

        if(member == null){
            throw new UsernameNotFoundException(email);
        }

        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .build();
    }

    // 회원 정보 업데이트
    public Member updateMember(String email, String phone, String address) {
        Member member = memberRepository.findByEmail(email);
        member.setPhone(phone);
        member.setAddress(address);
        return memberRepository.save(member);
    }

    // 비밀번호 업데이트
    public Member updatePassword(String email, String newPassword) {
        Member member = memberRepository.findByEmail(email);
        member.setPassword(passwordEncoder.encode(newPassword));
        return memberRepository.save(member);
    }

    // 회원 정보 조회
    public Member getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        return member;
    }


}