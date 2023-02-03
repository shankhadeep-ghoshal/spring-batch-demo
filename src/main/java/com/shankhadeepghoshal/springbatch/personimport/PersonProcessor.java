package com.shankhadeepghoshal.springbatch.personimport;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:shankhadeepghoshal1996@gmail.com">Shankhadeep Ghoshal</a>
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Component
@PersonImport
public class PersonProcessor implements ItemProcessor<Person, Person> {
  public static final String FIND_IF_PERSON_EXISTS_QUERY =
      """
												SELECT COUNT(id) FROM person WHERE id = :id
								""";
  NamedParameterJdbcTemplate template;

  @Override
  public Person process(Person person) {
    log.info("Transforming person {}", person);
    final var paramSource = new BeanPropertySqlParameterSource(person);
    final var isPersonPresent =
        Optional.ofNullable(
            template.queryForObject(FIND_IF_PERSON_EXISTS_QUERY, paramSource, Integer.class));

    log.info("Person already exists: {}", isPersonPresent.isPresent());

    if (isPersonPresent.isPresent()) {
      return isPersonPresent.get() > 0 ? null : person;
    }

    return person;
  }
}
