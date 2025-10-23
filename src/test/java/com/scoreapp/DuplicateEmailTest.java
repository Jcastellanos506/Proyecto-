package com.scoreapp;
import com.scoreapp.repository.*; import com.scoreapp.service.*; import org.junit.jupiter.api.*; import static org.junit.jupiter.api.Assertions.*;
public class DuplicateEmailTest {
  @Test void duplicate_email_rejected(){
    UserRepository repo=new InMemoryUserRepository(); UserService svc=new UserService(repo);
    svc.createUser("Ana","ana@example.com",1000000); assertThrows(IllegalArgumentException.class, ()->svc.createUser("Ana2","ana@example.com",2000000));
  }
}
