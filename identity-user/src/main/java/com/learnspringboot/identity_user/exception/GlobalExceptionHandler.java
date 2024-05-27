package com.learnspringboot.identity_user.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.learnspringboot.identity_user.dto.response.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    

    // Trường hợp không rơi vào các exception đã được định nghĩa
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
        ApiResponse apiResponse = new ApiResponse<>();
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // Xử lý các trường hợp ngoại lệ 
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        ApiResponse apiResponse = new ApiResponse<>();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // Xử lý những ngoại lệ đối số không hợp lệ
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {

        String enumKey = exception.getFieldError().getDefaultMessage(); // Lấy ra đoạn chuỗi, ví dụ "INVALID_USERNAME"
        ErrorCode errorCode = ErrorCode.INVALID_MESSAGE;
        ApiResponse apiResponse = new ApiResponse<>();

        // Thử xem enumKey có nằm trong danh sách enum của ErrorCode không, 
        // nếu có thì gán enum mới cho biến errorCode,
        // nếu không thì errorCode vẫn giữ giá trị là INVALID_MESSAGE
        try {
            errorCode = ErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException e) {
            
        }

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }
}
