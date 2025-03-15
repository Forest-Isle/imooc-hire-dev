package com.senyu.common.exeptions.handler;

import com.senyu.common.GraceJSONResult;
import com.senyu.common.ResponseStatusEnum;
import com.senyu.common.exeptions.MyCustomException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GraceExceptionHandler {

    @ExceptionHandler(MyCustomException.class)
    @ResponseBody
    public GraceJSONResult returnMyCustomException(MyCustomException e) {
        return GraceJSONResult.exception(e.getResponseStatusEnum());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public GraceJSONResult returnNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errors = getErrors(bindingResult);
        return GraceJSONResult.errorMap(errors);
    }

    @ExceptionHandler({SignatureException.class,
            ExpiredJwtException.class,
            UnsupportedJwtException.class,
            MalformedJwtException.class,
    io.jsonwebtoken.security.SignatureException.class,})
    @ResponseBody
    public GraceJSONResult returnJWTException(SignatureException e) {
        e.printStackTrace();
        return GraceJSONResult.exception(ResponseStatusEnum.JWT_SIGNATURE_ERROR);
    }

    public Map<String, String> getErrors(BindingResult result) {
        Map<String, String> map = new HashMap<>();
        List<FieldError> fieldErrors = result.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            String field = fieldError.getField(); // 错误对应的字段名
            String defaultMessage = fieldError.getDefaultMessage(); // 错误信息
            map.put(field, defaultMessage);
        }
        return map;
    }

}
