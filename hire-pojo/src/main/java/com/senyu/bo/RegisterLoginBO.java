package com.senyu.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegisterLoginBO {

    @NotBlank(message = "手机号不能为空")
    private String mobile;
    @NotBlank(message = "验证码不能为空")
    private String smsCode;

}
