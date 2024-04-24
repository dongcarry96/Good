package com.dong.good.Member.controller;

import com.dong.good.Member.domain.Member;
import com.dong.good.Member.dto.MemberFormDto;
import com.dong.good.Member.dto.PasswordFormDto;
import com.dong.good.Member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping(value = "/new")
    public String memberForm(Model model){
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/signup";
    }

    @PostMapping(value = "/new")
    public String newMember(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model){

        if(bindingResult.hasErrors()){
            return "member/signup";
        }

        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "member/signup";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/login")
    public String loginMember(){
        return "/member/login";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "/main";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response,
                SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }

    // 마이페이지 보기
    @GetMapping("/mypage")
    public String myPage(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberService.getMemberByEmail(email);
        log.info("Controller ➜ myPage : "+ member);
        log.info("Controller ➜ 이름 : "+ member.getName());
        log.info("Controller ➜ 주소 : "+ member.getAddress());
        log.info("Controller ➜ 폰번호 : "+ member.getPhone());
        model.addAttribute("member", member);
        return "member/mypage";
    }
    // 회원 정보 수정 페이지 보기
    @GetMapping("/update")
    public String updateMember(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberService.getMemberByEmail(email);
        model.addAttribute("member", member);
        return "member/mypageUpdate";
    }

    // 회원 정보 수정
    @PostMapping("/update")
    public String updateMember(@ModelAttribute Member member, Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        member.setEmail(email);
        memberService.updateMember(member.getEmail(), member.getPhone(), member.getAddress());
        return "redirect:/members/mypage";
    }

    // 비밀번호 수정 페이지 보기
    @GetMapping("/updatePassword")
    public String updatePassword(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("email", email);
        model.addAttribute("passwordFormDto", new PasswordFormDto());
        return "member/mypagePwUpdate";
    }

    // 비밀번호 수정
    @PostMapping("/updatePassword")
    public String updatePassword(@Valid PasswordFormDto passwordFormDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "member/mypagePwUpdate";
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberService.getMemberByEmail(email);
        if (passwordEncoder.matches(passwordFormDto.getCurrentPassword(), member.getPassword())) {
            memberService.updatePassword(email, passwordFormDto.getNewPassword());
            return "redirect:/members/mypage";
        } else {
            model.addAttribute("errorMessage", "현재 비밀번호가 일치하지 않습니다.");
            return "member/mypagePwUpdate";
        }
    }
}