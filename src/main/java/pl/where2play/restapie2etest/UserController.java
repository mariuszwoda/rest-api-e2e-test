package pl.where2play.restapie2etest;

import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resources")
class UserController {

    @GetMapping
    ResponseEntity<String> getUser() {
        return ResponseEntity.ok("Hello World");
    }

    @PostMapping
    ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(user);
    }
}
