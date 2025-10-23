package com.scoreapp.service;
import com.scoreapp.model.User; import com.scoreapp.repository.UserRepository; import com.scoreapp.util.Validation; import java.util.*;
public class UserService {
  private final UserRepository repo; public UserService(UserRepository r){this.repo=r;}
  public User createUser(String fullName,String email,double income){
    Validation.requireNonBlank(fullName,"nombre"); Validation.requireEmail(email); Validation.requirePositive(income,"ingreso mensual");
    if(repo.findByEmail(email).isPresent()) throw new IllegalArgumentException("email ya registrado");
    User u=new User(fullName,email,income); repo.save(u); return u;
  }
  public Optional<User> findByEmail(String email){ return repo.findByEmail(email); }
  public void updateIncome(User u,double inc){ u.setMonthlyIncome(inc); repo.save(u); }
}
