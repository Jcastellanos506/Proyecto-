package com.scoreapp;
import com.scoreapp.util.Validation; import com.scoreapp.exceptions.ValidationException; import org.junit.jupiter.api.Test; import static org.junit.jupiter.api.Assertions.*;
public class EmailValidationTest {
  @Test void invalid_email_throws(){ assertThrows(ValidationException.class, ()->Validation.requireEmail("no-at-sign")); }
}
