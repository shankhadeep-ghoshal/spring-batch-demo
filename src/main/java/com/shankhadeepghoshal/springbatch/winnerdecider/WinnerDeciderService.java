package com.shankhadeepghoshal.springbatch.winnerdecider;

import io.lettuce.core.api.StatefulRedisConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:shankhadeepghoshal1996@gmail.com">Shankhadeep Ghoshal</a>
 * @since 1.0
 */
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Service
public class WinnerDeciderService {
  private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  public static final String SQL_QUERY =
      """
        SELECT person.name AS contestants
        FROM(SELECT person_purchase.cust_id, SUM(people.person_purchase.amount) AS total_amount
           FROM people.person_purchase
           WHERE YEARWEEK(people.person_purchase.buy_date, 0) = YEARWEEK(DATE(:date),0)
           GROUP BY people.person_purchase.cust_id
           HAVING total_amount > :amount) as temp, people.person
        WHERE people.person.id = temp.cust_id
      """;

  private static final Locale LOCALE_FOR_WEEK = Locale.UK;

  NamedParameterJdbcTemplate jdbcTemplate;
  GenericObjectPool<StatefulRedisConnection<String, String>> pool;
  Supplier<ThreadLocalRandom> randomSupplier;

  @Value("${winner.min-amt}")
  Integer minAmountForBeingWinner;

  public String findWinnerOfTheWeek(final String date) {
    final var localDate = LocalDate.parse(date, FORMAT);
    final var weekOfYear = getWeekGivenLocalDate(localDate);
    final var key = localDate.getYear() + " " + weekOfYear;
    final var cachedWinner = getOrWinnerOfGivenYearAndWeek(key);

    if (cachedWinner.isEmpty()) {
      final var contestants = getEveryoneWeekOfGivenDayAboveSpecifiedSpending(localDate);
      if (contestants.isEmpty()) {
        log.info("Nc contestant found for the given date {}", localDate);
        return null;
      }
      final var winner = chooseRandomWinner(contestants);

      log.info("Winner for date {} is {}", localDate, winner);

      final var putSuccessful = putWinnerIntoCache(key, winner);
      if (putSuccessful.isPresent()) {
        return winner;
      }

      return null;
    }

    return cachedWinner.get();
  }

  private int getWeekGivenLocalDate(final LocalDate date) {
    final var woy = WeekFields.of(LOCALE_FOR_WEEK).weekOfWeekBasedYear();
    return date.get(woy);
  }

  private List<String> getEveryoneWeekOfGivenDayAboveSpecifiedSpending(final LocalDate date) {
    final var namedParams =
        new MapSqlParameterSource()
            .addValue("date", date)
            .addValue("amount", minAmountForBeingWinner);
    final var resultList =
        jdbcTemplate.query(SQL_QUERY, namedParams, (rs, rowNum) -> rs.getString(1));

    log.info(
        "Got result of size {} fo amount {} of date {}",
        resultList.size(),
        minAmountForBeingWinner,
        date);
    return resultList;
  }

  private Optional<String> getOrWinnerOfGivenYearAndWeek(final String key) {
    try (var connection = pool.borrowObject()) {
      return Optional.ofNullable(connection.sync().get(key));
    } catch (Exception e) { // NOPMD
      log.error("Error while fetching winner from Redis", e);
      throw new RuntimeException(e); // NOPMD
    }
  }

  private Optional<String> putWinnerIntoCache(String key, String winnerName) {
    try (var connection = pool.borrowObject()) {
      return Optional.ofNullable(connection.sync().set(key, winnerName));
    } catch (Exception e) { // NOPMD
      log.error("Error while putting winner into Redis", e);
      throw new RuntimeException(e); // NOPMD
    }
  }

  private String chooseRandomWinner(final List<String> contestants) {
    return contestants.get(randomSupplier.get().nextInt(contestants.size()));
  }
}
