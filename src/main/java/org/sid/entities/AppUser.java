package org.sid.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String userName;
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<AppRole> roles = new ArrayList<>();

    @JsonSetter
    public void setPassword(String password) {
        this.password = password;
    }
    @JsonIgnore
    public String getPassword() {
        return password;
    }
}
