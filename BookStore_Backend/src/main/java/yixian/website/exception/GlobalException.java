package yixian.website.exception;


import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import yixian.website.common.ErrorMessages;
import yixian.website.common.Result;

/**
 * 全局異常處理，捕獲抛出的異常，並返回統一格式的結果
 */
@ControllerAdvice
public class GlobalException {

    //400 Bad Request：請求有錯誤（例如資料格式或類型錯誤）
    @ExceptionHandler(InvalidDataException.class)
    @ResponseBody
    public Result invalidDataException(InvalidDataException e) {
        return Result.exceptionError(e.getCode(), e.getData(), e.getMessage());
    }

    //404 Not Found：查詢的資源不存在
    @ExceptionHandler(yixian.website.exception.EntityNotFoundException.class)
    @ResponseBody
    public Result entityNotFoundException(yixian.website.exception.EntityNotFoundException e) {
        return Result.exceptionError(e.getCode(), null, e.getMessage());
    }

    //500 伺服器錯誤
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public Result serviceException(ServiceException e) {
        return Result.exceptionError(e.getCode(), null, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result globalException(Exception e) {
        e.printStackTrace();
        return Result.exceptionError(500, null, ErrorMessages.SERVER_ERROR);
    }

}
