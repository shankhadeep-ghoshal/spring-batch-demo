package com.shankhadeepghoshal.springbatch.winnerdecider;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author <a href="mailto:shankhadeepghoshal1996@gmail.com">Shankhadeep Ghoshal</a>
 * @since 1.0
 */
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@RestController
@RestControllerAdvice
@CrossOrigin("*")
@Validated
public class WinnerDeciderController {
  public static final String DATE_VALIDATION_MESSAGE =
      """
      Please enter a date in the form yyyy-MM-dd. The date should be in between today and 10 years ago.
      """;

  WinnerDeciderService service;

  @GetMapping("/{date}")
  public ResponseEntity<String> winnerOfTheWeek(
      @DateCheck(message = DATE_VALIDATION_MESSAGE) @PathVariable("date") final String date) {
    log.info("Winner check request for date {}", date);
    final var toResponse = service.findWinnerOfTheWeek(date);
    if (toResponse == null || toResponse.isBlank() || toResponse.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(toResponse);
  }

  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  @ExceptionHandler(ConstraintViolationException.class)
  public String handleConstraintViolation(ConstraintViolationException ex) {
    log.debug("Constraint violation exception encountered: {}", ex.getConstraintViolations(), ex);
    return buildValidationErrors(ex.getConstraintViolations());
  }

  private String buildValidationErrors(Set<ConstraintViolation<?>> violations) {
    return violations.stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.joining(";"));
  }
}
