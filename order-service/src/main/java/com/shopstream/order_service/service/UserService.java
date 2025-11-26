package com.shopstream.order_service.service;



import com.shopstream.order_service.entity.User;
import com.shopstream.order_service.repository.UserRepository;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

 private final UserRepository userRepo;

 public UserService(UserRepository userRepo) {
     this.userRepo = userRepo;
 }

 public Long getCurrentUserId() {
     Authentication auth = SecurityContextHolder.getContext().getAuthentication();

     if (auth == null || !auth.isAuthenticated()) {
         throw new RuntimeException("User is not authenticated");
     }

     String username = auth.getName(); // set by your JwtAuthFilter
     User user = userRepo.findByUsername(username)
             .orElseThrow(() -> new RuntimeException("User not found for username: " + username));

     return user.getId(); // assumes User has Long id;
 }

 public Long getUserIdFromPrincipal(Principal principal) {
	    if (principal == null) {
	        throw new RuntimeException("Principal is null");
	    }

	    String username = principal.getName();
	    User user = userRepo.findByUsername(username)
	            .orElseThrow(() -> new RuntimeException("User not found for username: " + username));

	    return user.getId();
	}

}
