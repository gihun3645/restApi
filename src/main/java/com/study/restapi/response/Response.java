package com.study.restapi.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@AllArgsConstructor
// Response<T> 는 뭔지 잘 모르겠어 => Generic 문법임, 타입을 지정시키기 위해서 사용함
public class Response<T> {

    private String success;
    private String message;
    private T data;
}
