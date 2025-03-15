package com.senyu.common.exeptions;

import com.senyu.common.ResponseStatusEnum;

public class GraceException {

    public static void display(ResponseStatusEnum statusEnum) {
        throw new MyCustomException(statusEnum);
    }

}
