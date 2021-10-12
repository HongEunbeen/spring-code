package com.example.restfulwebservice.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class UserController {
    //의존성 주입으로 생성된다.
    //개발자가 프로그램 실행도중에 변경할 수 없어 일관된 사용이 가능하다
    private UserDaoService service;

    public UserController(UserDaoService service){
        this.service = service;
    }

    //사용자 전체목록 조희
    @GetMapping("/users")
    public List<User> retrieveAllUsers(){
        return service.findAll();
    }

    //사용자 한 명 조회
    //GET /users/1 -> 서버측에는 문자 형태(String)으로 옴 -> int로 자동 변환된다.
    @GetMapping("/users/{id}")
    public User retrieveUser(@PathVariable int id){
        User user = service.findOne(id);
        if(user == null) {
            throw new UserNotFoundException(String.format("ID[%s] not found", id));
        }
        return user;
    }

    //사용자 생성
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user){
        User savedUser = service.save(user);
        
        //사용자에게 요청값을 변환해주기 위해 사용
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

}
