package pl.where2play.restapie2etest.model;

import lombok.*;

import java.time.LocalDateTime;

//@Getter
//@Setter
@Data
@AllArgsConstructor
@Builder
public class User {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String department;
    private LocalDateTime createdAt;

    private String name;
    private String password;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String country;
    private String zip;
    private String countryCode;


    public User(String id, String email, String firstName, String lastName, String role, String department, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.department = department;
        this.createdAt = createdAt;
    }
}
