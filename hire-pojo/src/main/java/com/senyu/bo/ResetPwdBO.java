package com.senyu.bo;

import com.senyu.ar.AdminAR;
import com.senyu.common.ResponseStatusEnum;
import com.senyu.common.exeptions.GraceException;
import com.senyu.common.utils.MD5Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResetPwdBO {

    private String adminId;
    private String password;
    private String rePassword;

    private void validate() {
        checkPwd();
        checkAdminId();
    }

    private void checkAdminId() {
        if (StringUtils.isEmpty(adminId)) {
            GraceException.display(ResponseStatusEnum.ADMIN_USERNAME_NULL_ERROR);
        }
        AdminAR adminAR = new AdminAR();
        adminAR.setId(adminId);
        adminAR = adminAR.selectById();
        if (adminAR == null) {
            GraceException.display(ResponseStatusEnum.ADMIN_USERNAME_NULL_ERROR);
        }
    }

    private void checkPwd() {
        if (StringUtils.isEmpty(password)) {
            GraceException.display(ResponseStatusEnum.ADMIN_PASSWORD_NULL_ERROR);
        }
        if (StringUtils.isEmpty(rePassword)) {
            GraceException.display(ResponseStatusEnum.ADMIN_PASSWORD_NULL_ERROR);
        }
        if (!password.equals(rePassword)) {
            GraceException.display(ResponseStatusEnum.ADMIN_PASSWORD_ERROR);
        }
    }

    public void modifyPwd() {
        // 校验
        validate();
        // 重置密码
        String slat = (int)((Math.random() * 9 + 1) * 100000) + "";
        String pwd = MD5Utils.encrypt(password, slat);
        AdminAR adminAR = new AdminAR();
        adminAR.setId(adminId);
        adminAR.setPassword(pwd);
        adminAR.setSlat(slat);
        adminAR.setUpdatedTime(LocalDateTime.now());

        adminAR.updateById();
    }
}
