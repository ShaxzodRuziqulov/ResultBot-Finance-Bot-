package com.example.resultbot.web.rest;


import com.example.resultbot.entity.User;
import com.example.resultbot.service.UserService;
import com.example.resultbot.service.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserResource {
    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    //
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody UserDto userDto) throws URISyntaxException {
        UserDto result = userService.create(userDto);
        return ResponseEntity.created(new URI("/api/user/create" + result.getId())).body(result);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestBody UserDto userDto, @PathVariable Long id) throws URISyntaxException {
        if (userDto.getId() != 0 && !userDto.getId().equals(id)) {
            return ResponseEntity.badRequest().body("Invalid id");
        }
        UserDto result = userService.update(userDto);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        List<UserDto> findAllUser = userService.findAllUser();
        return ResponseEntity.ok(findAllUser);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        User result = userService.findById(id);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        User user = userService.delete(id);
        return ResponseEntity.ok(user);
    }

//    @GetMapping("/page")
//    public ResponseEntity<?> getUsers(@RequestParam(defaultValue = "0") int page,
//                                      @RequestParam(defaultValue = "10") int size) {
//        Page<User> userPage = userService.getUsers(page, size);
//        return ResponseEntity.ok(userPage);
//    }
//
//    @GetMapping("/find/{email}")
//    public ResponseEntity<?> findActiveUserByEmail(@PathVariable String email) {
//        User result = userService.findActiveUserByEmail(email);
//        return ResponseEntity.ok(result);
//    }
//
//    @GetMapping("/me")
//    public ResponseEntity<User> getMe() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        User currentUser = (User) authentication.getPrincipal();
//
//        return ResponseEntity.ok(currentUser);
//    }
}
