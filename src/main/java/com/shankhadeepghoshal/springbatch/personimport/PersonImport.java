package com.shankhadeepghoshal.springbatch.personimport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author <a href="mailto:shankhadeepghoshal1996@gmail.com">Shankhadeep Ghoshal</a>
 * @since 1.0
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface PersonImport {}
