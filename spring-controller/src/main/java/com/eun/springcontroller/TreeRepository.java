package com.eun.springcontroller;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TreeRepository {

    public Tree findById(int id){

        //sample data
        List<Tree> list = new ArrayList<>();
        list.add(new Tree(1, 12, "홍길동"));

        return list.stream()
                .filter(item -> item.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Tree findByNameAndAge(String name, int age){

        //sample data
        List<Tree> list = new ArrayList<>();
        list.add(new Tree(1, 12, "홍길동"));

        return list.stream()
                .filter(item -> item.getName().equals(name))
                .filter(item -> item.getAge() == age)
                .findFirst()
                .orElse(null);
    }
}
