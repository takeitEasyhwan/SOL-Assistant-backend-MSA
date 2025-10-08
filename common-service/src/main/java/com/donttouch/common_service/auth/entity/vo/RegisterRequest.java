package com.donttouch.common_service.auth.entity.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegisterRequest {
    private String authId;
    private String password;
    private String phone;
    private InvestmentType investmentType;
    private String name;
}
