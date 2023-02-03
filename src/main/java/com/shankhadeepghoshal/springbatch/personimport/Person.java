package com.shankhadeepghoshal.springbatch.personimport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author <a href="mailto:shankhadeepghoshal1996@gmail.com">Shankhadeep Ghoshal</a>
 * @since 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Person(Integer id, String name, String username, String email, String phone) {}
