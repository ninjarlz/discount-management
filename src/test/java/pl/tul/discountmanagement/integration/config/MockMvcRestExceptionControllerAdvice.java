package pl.tul.discountmanagement.integration.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.tul.discountmanagement.exception.product.ProductNotFoundException;

import java.util.Map;

import static pl.tul.discountmanagement.util.constant.config.ApplicationProfiles.INTEGRATION_TEST_PROFILE;

/**
 * This advice is necessary because {@link MockMvc} is not a real servlet environment,
 * therefore it does not redirect error responses to {@link ErrorController}, 
 * so we need to provide this mechanism in integration tests.
 */
@ControllerAdvice
@Profile(INTEGRATION_TEST_PROFILE)
@RequiredArgsConstructor
public class MockMvcRestExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    private final BasicErrorController errorController;

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFoundException(HttpServletRequest request, Exception ex) {
        request.setAttribute("javax.servlet.error.request_uri", request.getPathInfo());
        request.setAttribute("javax.servlet.error.status_code", HttpStatus.NOT_FOUND.value());
        return errorController.error(request);
    }

}
