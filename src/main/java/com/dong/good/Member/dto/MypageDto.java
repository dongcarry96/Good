package com.dong.good.Member.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MypageDto {
    private String name;
    private String email;
    private String password;
    private String address;
    private String phone;

    @QueryProjection
    public MypageDto(String name, String email, String password, String address, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.phone = phone;
    }
}
