package cn.geobeans.encrypt.config;


import cn.geobeans.encrypt.common.JsonResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandleConfig extends ResponseEntityExceptionHandler {


    @ExceptionHandler(Exception.class)
    @ResponseBody
    public JsonResponse handle(Exception ex, WebRequest request) {
        ex.printStackTrace();
        return new JsonResponse(ex.getMessage());
    }


}
