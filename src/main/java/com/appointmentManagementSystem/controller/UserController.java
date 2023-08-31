package com.appointmentManagementSystem.controller;

import com.appointmentManagementSystem.model.EntityUser;
import com.appointmentManagementSystem.payload.MessageResponse;
import com.appointmentManagementSystem.payload.SignupRequest;
import com.appointmentManagementSystem.service.UserService;
import com.appointmentManagementSystem.util.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/getUsers")
    @PreAuthorize(" hasRole('USER')")
    public Iterable<EntityUser> getUsers() {
        return userService.findAll();
    }

    @PostMapping("/addUser")
    @PreAuthorize(" hasRole('ADMIN')")
    public ResponseEntity<Long> addUser(@RequestBody SignupRequest req){
        try {

            if (userService.existsByEmail(req.getEmail())) {
                return ResponseEntity
                        .badRequest()
                        .body(-2L);//new MessageResponse("Hata:Bu mail kullanılmakta")
            }
            EntityUser newUser = userService.addUser(req);
            return new ResponseEntity<>(newUser.getId(), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(-3L, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateUser")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> updateUser(@RequestBody SignupRequest req){

        try {
            EntityUser newUser = userService.updateUser(req,req.getId());
            if(newUser == null){
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Hata:Kullanıcı bulunamadı"));
            }else {
                return new ResponseEntity<>(null, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(e instanceof CustomException){
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse(e.getMessage()));
            }
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getUserById/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<EntityUser> getUserById(@PathVariable ("id")Long id){

        Optional<EntityUser> user = userService.getUserById(id);
        if(user.isPresent()){
            return new ResponseEntity<>(user.get(),HttpStatus.OK);
        }else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/deleteUserById/{id}")
    @PreAuthorize(" hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteUserById(@PathVariable("id") long id) {
        try {
            userService.deleteUserById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            System.out.println("deleteUserById/" + id + " failed. Error: " + e);
            if(e.getMessage().equals("No value present")){
                return new ResponseEntity<>(new MessageResponse("Silmek istediğiniz kullanıcı bulunamadı. Lütfen sayfanızı yenileyiniz."),HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(new MessageResponse(e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
