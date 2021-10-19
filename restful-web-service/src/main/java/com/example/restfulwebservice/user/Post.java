package com.example.restfulwebservice.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Post {

    @GeneratedValue
    @Id
    private Integer id;
    private String description;

    // USer : Post -> 1: (0~N) N, MAin : Sub
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
