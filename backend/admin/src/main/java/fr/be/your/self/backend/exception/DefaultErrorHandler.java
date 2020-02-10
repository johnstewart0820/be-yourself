package fr.be.your.self.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.be.your.self.common.StatusCode;
import fr.be.your.self.dto.StatusResponse;
import fr.be.your.self.exception.InvalidException;
import fr.be.your.self.exception.ValidationException;
import fr.be.your.self.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class DefaultErrorHandler {

    @ResponseBody
    @ExceptionHandler(value = ValidationException.class)
    public ResponseEntity<?> handleException(ValidationException exception) {
    	final StatusResponse response = new StatusResponse(false);
    	response.setCode(exception.getCode().getValue());
    	response.setMessage(exception.getMessage());
    	
    	final String data = exception.getData();
    	if (!StringUtils.isNullOrSpace(data)) {
    		response.addData("data", data);
    	}
    	
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ResponseBody
    @ExceptionHandler(value = InvalidException.class)
    public ResponseEntity<?> handleException(InvalidException exception) {
    	final StatusResponse response = new StatusResponse(false);
    	response.setCode(exception.getCode().getValue());
    	response.setMessage("Invalid parameter");
    	
    	final String parameter = exception.getParameter();
    	if (!StringUtils.isNullOrSpace(parameter)) {
    		response.addData("parameter", parameter);
    	}
    	
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> handleException(Exception exception) {
    	final StatusResponse response = new StatusResponse(false);
    	
    	if (exception.getCause() instanceof BadCredentialsException) {
    		response.setCode(StatusCode.INVALID_CREDENTIALS.getValue());
    		response.setMessage(exception.getCause().getMessage());
    	} else {
    		response.setCode(StatusCode.PROCESSING_ERROR.getValue());
    		response.setMessage(exception.getMessage());
    	}
    	
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
