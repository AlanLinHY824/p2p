package com.powernode.p2p.controllerhandler;

import com.powernode.p2p.exception.ResultException;
import com.powernode.p2p.myutils.Result;
import com.powernode.p2p.myutils.ResultEnum;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常处理类
 * @Author AlanLin
 * @Description
 * @Date 2020/10/13
 */
@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result handlerExceptionAdvice(Exception e){
        if (e instanceof ResultException){
            ResultException exception = (ResultException) e;
            return Result.FAIL(exception);
        }
        return Result.FAIL(new ResultException(ResultEnum.INTERNAL_ERRO));
    }
}
