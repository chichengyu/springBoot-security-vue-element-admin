package com.site.common.exception;

import com.site.common.R;
import com.site.common.enums.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 全局异常
 *
 * 启动应用后，被 @ExceptionHandler、@InitBinder、@ModelAttribute 注解的方法，都会作用在 被 @RequestMapping 注解的方法上。
 */
@RestControllerAdvice
public class GlobalControllerAdvice {

    private static Logger logger = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    /**
     * 应用到所有@RequestMapping注解方法，在其执行之前初始化数据绑定器
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder){}

    /**
     * 把值绑定到Model中，使全局@RequestMapping可以获取到该值
     * @param model
     * 在Model上设置的值，对于所有被 @RequestMapping 注解的方法中，都可以通过 ModelMap 获取 (ModelMap modelMap)，modelMap.get(key)
     */
    @ModelAttribute
    public void addAttributes(Model model){}

    /**
     * 参数验证异常
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<String> handleMethodArgumentException(MethodArgumentNotValidException e){
        logger.error("参数异常,{}",e);
        BindingResult bindingResult = e.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        return R.error(fieldError.getDefaultMessage());
    }

    /**
     * Security 无权限访问资源时异常
     * @param e
     * @return
     */
    @ExceptionHandler(AccessDeniedException.class)
    public R<String> handleAccessDeniedException(AccessDeniedException e){
        logger.error("无权限访问，{}",e);
        return R.error(ResponseCode.NOT_PERMISSION.getCode(),ResponseCode.NOT_PERMISSION.getMessage());
    }

    /**
     * 业务异常
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public R<String> handleBusinessException(BusinessException e){
        logger.error("业务异常,{}",e);
        return R.error(e.getCode(),e.getMessage());
    }

    /**
     * 系统错误
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<String> handleException(Exception e){
        if (e instanceof MaxUploadSizeExceededException){
            logger.error("上传异常：{}",e);
            return R.error("上传文件太大");
        }
        if (e instanceof HttpRequestMethodNotSupportedException){
            logger.error("请求方式异常：{}",e);
            return R.error("请求方式异常："+((HttpRequestMethodNotSupportedException) e).getSupportedHttpMethods());
        }
        logger.error("接口出现严重异常：{}",e);
        return R.error("系统异常");
    }
}
