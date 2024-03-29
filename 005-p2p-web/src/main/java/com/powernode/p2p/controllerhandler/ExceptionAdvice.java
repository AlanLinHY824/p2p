package com.powernode.p2p.controllerhandler;

import com.powernode.p2p.exception.ResultException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 异常处理类
 * @Author AlanLin
 * @Description
 * @Date 2020/10/13
 */
@ControllerAdvice
@Slf4j
public class ExceptionAdvice {

    @ExceptionHandler(value = Exception.class)
    public String handlerExceptionAdvice(Exception e, Model model){
        e.printStackTrace();
        if (e instanceof ResultException){
            ResultException exception = (ResultException) e;
            model.addAttribute("error_msg", exception.getMessage());
            return "exception";
        }
        model.addAttribute("error_msg", "服务器挤爆啦，请稍后再试！");
        return "exception";
    }
}
//        log.error(Arrays.deepToString(e.getStackTrace()));
