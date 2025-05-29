package org.serratec.projetofinal_api_g4.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler  extends ResponseEntityExceptionHandler {

  //Se houver algum erro de email ou senha, retorna o erro com o status 422 (Unprocessable Entity) e a mensagem de erro para o cliente.
  @ExceptionHandler({EmailException.class, SenhaException.class})
	protected ResponseEntity<Object> handleEmailESenhaException(RuntimeException ex) {
		return ResponseEntity.unprocessableEntity().body(ex.getMessage());
	}

  //Este e para erros de validação, como campos obrigatórios, inválidos, E-mail com formato inválido, Nome com mais de 50 caracteres, etc.
  @Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
        @NonNull MethodArgumentNotValidException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
        //cria uma lista de erros
		List<String>erros= new ArrayList<>();
    // percorre os erros de validação e adiciona na lista de erros
		for(FieldError er : ex.getBindingResult().getFieldErrors()) {
			erros.add(er.getField() + ": " + er.getDefaultMessage());
		}
		return super.handleExceptionInternal(ex, erros, headers, status, request);
	}
	
  //Este trata erros de enumeração inválida, como um JSON mal formatado ou um valor de enumeração que não existe.
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
        @NonNull HttpMessageNotReadableException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
		
    //Adiciona uma mensagem de erro específica para enumerações inválidas
		List<String> erros =  new ArrayList<>();
		erros.add("Valor de enumeração inválido: " + ex.getMostSpecificCause().getMessage());
		
		 ErroResposta  erroResposta = new ErroResposta(status.value(),"Existem campos inválidos ,confira o preenchimento ",LocalDateTime.now(),erros);
      // Retorna a resposta com o erro formatado
		return super.handleExceptionInternal(ex,erroResposta, headers, status, request);
	}
}
