package com.dong.good.Member.domain;

import com.dong.good.Member.dto.MemberFormDto;
import com.dong.good.config.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;


@Table(name = "member")
@Getter
@Setter
@Entity
@ToString
public class Member extends BaseEntity { // UserDetails를 상속받아 인증 객체로 사용

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="member_id")
    private Long id;

    @Column(unique =true)
    private String email;

    private String name;

    private String password;

    private String phone;

    private String address;


    // 회원가입 => member
    public static Member createMember(MemberFormDto memberFormDto,
                                      PasswordEncoder passwordEncoder) {
        Member member = new Member();
        member.setEmail(memberFormDto.getEmail());
        member.setPassword(passwordEncoder.encode(memberFormDto.getPassword()));
        member.setName(memberFormDto.getName());
        member.setPhone(memberFormDto.getPhone());
        member.setAddress(memberFormDto.getAddress());
        return member;
    }
}