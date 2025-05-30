package org.serratec.projetofinal_api_g4.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    // Tratamento para exceções de email e senha
    @ExceptionHandler({EmailException.class, SenhaException.class})
    protected ResponseEntity<Object> handleEmailESenhaException(RuntimeException ex) {
        ErroResposta erroResposta = new ErroResposta(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            ex.getMessage(),
            LocalDateTime.now(),
            List.of(ex.getMessage())
        );
        return ResponseEntity.unprocessableEntity().body(erroResposta);
    }

    // Tratamento para ClienteNotFoundException
    @ExceptionHandler(ClienteNotFoundException.class)
    protected ResponseEntity<Object> handleClienteNotFoundException(ClienteNotFoundException ex) {
        ErroResposta erroResposta = new ErroResposta(
            HttpStatus.NOT_FOUND.value(),
            "Cliente não encontrado",
            LocalDateTime.now(),
            List.of(ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erroResposta);
    }

    // Tratamento para IllegalArgumentException (CPF/Email duplicado, validações customizadas)
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErroResposta erroResposta = new ErroResposta(
            HttpStatus.BAD_REQUEST.value(),
            "Dados inválidos",
            LocalDateTime.now(),
            List.of(ex.getMessage())
        );
        return ResponseEntity.badRequest().body(erroResposta);
    }

    // Tratamento para violação de integridade do banco (unique constraints)
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String mensagem = "Erro de integridade dos dados";
        List<String> erros = new ArrayList<>();
        
        // Identificar o tipo de violação baseado na mensagem da exceção
        String exceptionMessage = ex.getMostSpecificCause().getMessage().toLowerCase();
        
        if (exceptionMessage.contains("cpf") || exceptionMessage.contains("uk_cpf")) {
            mensagem = "CPF já cadastrado";
            erros.add("CPF já existe no sistema");
        } else if (exceptionMessage.contains("email") || exceptionMessage.contains("uk_email")) {
            mensagem = "Email já cadastrado";
            erros.add("Email já existe no sistema");
        } else {
            erros.add("Violação de constraint de integridade dos dados");
        }
        
        ErroResposta erroResposta = new ErroResposta(
            HttpStatus.CONFLICT.value(),
            mensagem,
            LocalDateTime.now(),
            erros
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erroResposta);
    }

    // Tratamento para erros de validação de campos
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        
        List<String> erros = new ArrayList<>();
        
        // Percorre os erros de validação e adiciona na lista de erros
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            erros.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
        }
        
        ErroResposta erroResposta = new ErroResposta(
            status.value(),
            "Existem campos inválidos, confira o preenchimento",
            LocalDateTime.now(),
            erros
        );
        
        return ResponseEntity.status(status).body(erroResposta);
    }
    
    // Tratamento para JSON mal formatado ou valores de enumeração inválidos
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            @NonNull HttpMessageNotReadableException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        
        List<String> erros = new ArrayList<>();
        String mensagemEspecifica = ex.getMostSpecificCause().getMessage();
        
        // Personalizar mensagem baseada no tipo de erro
        if (mensagemEspecifica.contains("enum")) {
            erros.add("Valor de enumeração inválido: " + mensagemEspecifica);
        } else if (mensagemEspecifica.contains("JSON")) {
            erros.add("Formato JSON inválido: " + mensagemEspecifica);
        } else {
            erros.add("Erro na leitura dos dados: " + mensagemEspecifica);
        }
        
        ErroResposta erroResposta = new ErroResposta(
            status.value(),
            "Dados mal formatados ou inválidos",
            LocalDateTime.now(),
            erros
        );
        
        return ResponseEntity.status(status).body(erroResposta);
    }

    // Tratamento genérico para RuntimeException não tratadas especificamente
    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        ErroResposta erroResposta = new ErroResposta(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro interno do servidor",
            LocalDateTime.now(),
            List.of("Ocorreu um erro inesperado. Tente novamente mais tarde.")
        );
        
        // Log do erro para debug (em produção, usar um logger apropriado)
        System.err.println("Erro não tratado: " + ex.getMessage());
        ex.printStackTrace();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erroResposta);
    }

    // Tratamento para Exception genérica (fallback)
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleGenericException(Exception ex) {
        ErroResposta erroResposta = new ErroResposta(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro interno do servidor",
            LocalDateTime.now(),
            List.of("Ocorreu um erro inesperado. Contate o suporte.")
        );
        
        // Log do erro para debug
        System.err.println("Erro genérico não tratado: " + ex.getMessage());
        ex.printStackTrace();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erroResposta);
    }
}
