package com.scoreapp;
import com.scoreapp.model.*; import com.scoreapp.repository.*; import com.scoreapp.service.*; import com.scoreapp.exceptions.*; import org.junit.jupiter.api.*; import static org.junit.jupiter.api.Assertions.*;
public class UserServiceTest {
  @Test void createUser_validIncome(){ UserService svc=new UserService(new InMemoryUserRepository()); User u=svc.createUser("Ana","ana@example.com",2500000); assertEquals("Ana", u.getFullName()); }
  @Test void createUser_negativeIncome_throws(){ UserService svc=new UserService(new InMemoryUserRepository()); assertThrows(ValidationException.class, ()->svc.createUser("Ana","ana@example.com",-1)); }
}
