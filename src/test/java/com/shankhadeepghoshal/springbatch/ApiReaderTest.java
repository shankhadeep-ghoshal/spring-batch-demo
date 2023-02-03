package com.shankhadeepghoshal.springbatch;

import com.shankhadeepghoshal.springbatch.personimport.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author <a href="mailto:shankhadeepghoshal1996@gmail.com">Shankhadeep Ghoshal</a>
 * @since 1.0
 */
@SpringBootTest
class ApiReaderTest {
  @Autowired private transient JsonItemReader<Person> reader;

  @Test
  void testJsonReader() throws Exception {
    reader.open(new ExecutionContext());

    Person person;
    int id = 1;
    while ((person = reader.read()) != null) { // NOPMD
      Assertions.assertEquals(id, person.id());
      id++;
    }
  }
}
