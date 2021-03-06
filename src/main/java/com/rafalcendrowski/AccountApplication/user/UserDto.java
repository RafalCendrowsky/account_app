package com.rafalcendrowski.AccountApplication.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

import java.util.Set;


@Data
@NoArgsConstructor
@Relation(collectionRelation = "users")
public class UserDto { // a dto for modelling user entities
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private Set<User.Role> roles;

    public UserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.email = user.getUsername();
        this.roles = user.getRoles();
    }

    public static UserDto of(User user) {
        return new UserDto(user);
    }
}
