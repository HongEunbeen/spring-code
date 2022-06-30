package com.eun.springcontroller;

import java.util.ArrayList;
import java.util.List;

public class Tree {
    private int id;
    private int age;
    private String name;

    public Tree(int id, int age, String name) {
        this.id = id;
        this.age = age;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }
}
