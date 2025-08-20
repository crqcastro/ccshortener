package unit.br.com.cesarcastro.apis.ccshortener.controller;


import br.com.cesarcastro.apis.ccshortener.controller.ExceptionHandlerController;
import br.com.cesarcastro.apis.ccshortener.domain.exceptions.BusinessException;
import br.com.cesarcastro.apis.ccshortener.domain.exceptions.NotAuthorizedException;
import br.com.cesarcastro.apis.ccshortener.domain.exceptions.ResourceNotFoundException;
import br.com.cesarcastro.apis.ccshortener.domain.model.dto.error.ErrorDTO;
import br.com.cesarcastro.apis.ccshortener.domain.model.dto.request.ShortenedURLRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.UnexpectedTypeException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

class ExceptionHandlerControllerTest {

    private AutoCloseable closeable;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ExceptionHandlerController controller;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    /* ############################################ 4XX CLIENT EXCEPTION ############################################ */

    @Test
    @DisplayName("Must handle BusinessException and return BAD_REQUEST")
    void testHandleBusinessException() {
        BusinessException exception = new BusinessException(BAD_REQUEST, "Erro de negócio");
        ResponseEntity<ErrorDTO> expected = ResponseEntity.status(BAD_REQUEST)
                .body(ErrorDTO.builder().message("Erro de negócio").build());

        ResponseEntity<ErrorDTO> result = controller.handleBusinessException(exception, request);
        assertThat(result.getStatusCode()).isEqualTo(expected.getStatusCode());
        assertEquals(expected.getBody().getMessage(), result.getBody().getMessage());
    }

    @Test
    @DisplayName("Must handle ResourceNotFoundException and return NOT_FOUND")
    void testHandleRecursoNaoEncontradoPixException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");
        ResponseEntity<ErrorDTO> expected = ResponseEntity.status(NOT_FOUND).body(ErrorDTO.builder().build());

        ResponseEntity<ErrorDTO> result = controller.handleRecursoNaoEncontradoPixException(exception, request);
        assertThat(result.getStatusCode()).isEqualTo(expected.getStatusCode());
    }

    @Test
    @DisplayName("Must handle HttpRequestMethodNotSupportedException and return METHOD_NOT_ALLOWED")
    void testHandleHttpRequestMethodNotSupportedException() {
        HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException("Método não permitido");
        ResponseEntity<ErrorDTO> expected = ResponseEntity.status(METHOD_NOT_ALLOWED).body(ErrorDTO.builder().build());

        ResponseEntity<ErrorDTO> result = controller.handleHttpRequestMethodNotSupportedException(exception, request);
        assertThat(result.getStatusCode()).isEqualTo(expected.getStatusCode());
    }

    @Test
    @DisplayName("Must handle UnexpectedTypeException and return UNPROCESSABLE_ENTITY")
    void testHandleUnexpectedTypeException() {
        UnexpectedTypeException exception = new UnexpectedTypeException("Tipo inesperado");
        ResponseEntity<ErrorDTO> expected = ResponseEntity.status(UNPROCESSABLE_ENTITY).body(ErrorDTO.builder().build());

        ResponseEntity<ErrorDTO> result = controller.handleUnexpectedTypeException(exception, request);
        assertThat(result.getStatusCode()).isEqualTo(expected.getStatusCode());
    }

    @Test
    @DisplayName("Must handle MethodArgumentNotValidException and return BAD_REQUEST")
    void testTratarErroViolacaoConstraint() {
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);

        when(violation.getMessage()).thenReturn("Erro de violação de constraint");

        Path propertyPath = mock(Path.class);
        when(violation.getPropertyPath()).thenReturn(propertyPath);
        when(propertyPath.toString()).thenReturn("campoExemplo");

        violations.add(violation);

        ConstraintViolationException exception = new ConstraintViolationException(violations);
        ResponseEntity<ErrorDTO> expected = ResponseEntity.status(UNPROCESSABLE_ENTITY).body(ErrorDTO.builder().build());
        ResponseEntity<ErrorDTO> result = controller.handleConstraintViolationException(exception, request);

        assertThat(result.getStatusCode()).isEqualTo(expected.getStatusCode());
    }

    @Test
    @DisplayName("Must handle IllegalArgumentException and return BAD_REQUEST")
    void testHandleIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Argumento inválido");
        ResponseEntity<ErrorDTO> expected = ResponseEntity.status(BAD_REQUEST).body(ErrorDTO.builder().build());

        ResponseEntity<ErrorDTO> result = controller.handleIllegalArgumentException(exception, request);
        assertThat(result.getStatusCode()).isEqualTo(expected.getStatusCode());
    }

    @Test
    @DisplayName("Must handle ConstraintViolationException and return UNPROCESSABLE_ENTITY")
    void testMethodArgumentNotValidExceptionComRecord() throws Exception {
        FieldError error1 = new FieldError("obj", "originalUrl", "URL must start with http:// or https://");
        List<FieldError> fieldErrors = Arrays.asList(error1);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletPath()).thenReturn("/api/teste");

        Constructor<ShortenedURLRequestDTO> constructor =
                ShortenedURLRequestDTO.class.getDeclaredConstructor(
                        String.class);

        MethodParameter methodParameter = new MethodParameter(constructor, 0); // 0 para o primeiro campo

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                methodParameter, bindingResult
        );
        ResponseEntity<ErrorDTO> result = controller.tratarErroArgumentoInvalido(exception, request);

        assertThat(result.getStatusCode()).isEqualTo(BAD_REQUEST);
    }


    @Test
    @DisplayName("Must handle NotAuthorizedException and return UNAUTHORIZED")
    void testNotAuthorizedException() {
        NotAuthorizedException exception = new NotAuthorizedException("Não autorizado");
        ResponseEntity<ErrorDTO> expected = ResponseEntity.status(UNAUTHORIZED).body(ErrorDTO.builder().build());

        ResponseEntity<ErrorDTO> result = controller.handleNotAuthorizedException(exception, request);
        assertThat(result.getStatusCode()).isEqualTo(expected.getStatusCode());
    }

    /* ############################################ 5XX SERVER EXCEPTION ############################################ */

    @Test
    @DisplayName("Must handle Exception and return INTERNAL_SERVER_ERROR")
    void testHandleException() {

        Exception exception = new Exception("Erro interno do servidor. Entre em contato com o suporte.");

        ResponseEntity<ErrorDTO> expected = ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                ErrorDTO.builder().message("Erro interno do servidor. Entre em contato com o suporte.").build()
        );

        ResponseEntity<ErrorDTO> result = controller.handleException(exception, request);

        assertThat(result.getStatusCode()).isEqualTo(expected.getStatusCode());
        assertThat(result.getBody().getMessage()).isEqualTo(expected.getBody().getMessage());
    }

}
