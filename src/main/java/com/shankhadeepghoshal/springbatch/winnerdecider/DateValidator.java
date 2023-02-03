package com.shankhadeepghoshal.springbatch.winnerdecider;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * @author <a href="mailto:shankhadeepghoshal1996@gmail.com">Shankhadeep Ghoshal</a>
 * @since 1.0
 */
public class DateValidator implements ConstraintValidator<DateCheck, String> {
  private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final String ZONE_ID = "Etc/UTC";

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    try {
      final var date = LocalDate.from(FORMAT.parse(value));
      final var dayTenYearsBeforeCurrentDay =
          LocalDate.of(
              LocalDate.now().getYear() - 10,
              LocalDate.now().getMonth(),
              LocalDate.now().getDayOfMonth());

      return !date.isAfter(LocalDate.now(ZoneId.of(ZONE_ID)))
          && !date.isBefore(dayTenYearsBeforeCurrentDay);
    } catch (DateTimeParseException e) {
      return false;
    }
  }
}
