package com.eun.springcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
@RequestMapping("/api/tree1")
public class TreeController {

    @Autowired
    private TreeRepository repository;

    @GetMapping("/{id}")
    public Tree getTreeById(@PathVariable int id) {
        return repository.findById(id);
    }

    @GetMapping
    public Tree getTreeById(@RequestParam String name,
                            @RequestParam int age) {
        return repository.findByNameAndAge(name, age);
    }
}
