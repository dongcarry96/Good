package com.dong.good.config;

import com.dong.good.Member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Log4j2
public class SecurityConfig {

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
//                .requestMatchers(toH2Console())
                .requestMatchers(new AntPathRequestMatcher("/static/**"));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                                log.info("filterChain() -> 필터체인 접근");
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/css/**"),
                                new AntPathRequestMatcher("/js/**"),
                                new AntPathRequestMatcher("/images/**"),
                                new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/main"),
                                new AntPathRequestMatcher("/admin/**"),
                                new AntPathRequestMatcher("/members/**"),
                                new AntPathRequestMatcher("/admin/**"),
                                new AntPathRequestMatcher("/item/**")
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/members/login") // 로그인 페이지 URL을 설정
                        .defaultSuccessUrl("/") // 로그인 성공 시 이동할 URL
                        .usernameParameter("email") // 로그인 시 사용할 파라미터 이름으로 email을 지정
                        .failureUrl("/members/login/error") // 로그인 실패시 이동할 url
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))// 로그아웃 url 설정
                        .logoutSuccessUrl("/") // 로그아웃 성공시 이동 할 url 설정
                                .invalidateHttpSession(true) // 로그아웃 이후에 세션을 전체 삭제할지 여부
                )
                .build();
    }

    // 인증되지 않은 사용자가 리소스에 접근하면 핸들러 발동
    // admin 계정으로만 입장 가능한 주소 접근시
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    // 패스워드 인코더로 사용할 빈 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}