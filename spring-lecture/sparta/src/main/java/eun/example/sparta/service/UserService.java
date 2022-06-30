package eun.example.sparta.service;

import eun.example.sparta.dto.SignupRequestDto;
import eun.example.sparta.model.User;
import eun.example.sparta.model.UserRole;
import eun.example.sparta.repository.UserRepository;
import eun.example.sparta.security.UserDetailsImpl;
import eun.example.sparta.security.kakao.KakaoOAuth2;
import eun.example.sparta.security.kakao.KakaoUserInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Optional;


@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final KakaoOAuth2 kakaoOAuth2;
    private static final String ADMIN_TOKEN = "AAABnv/xRVklrnYxKZ0aHgTBcXukeZygoC";

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, KakaoOAuth2 kakaoOAuth2) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.kakaoOAuth2 = kakaoOAuth2;
    }

    public User registerUser(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        // 회원 ID 중복 확인
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자 ID 가 존재합니다.");
        }

        // 패스워드 인코딩
        String password = passwordEncoder.encode(requestDto.getPassword());
        String email = requestDto.getEmail();
        // 사용자 ROLE 확인
        UserRole role = UserRole.USER;
        if (requestDto.isAdmin()) {
            if (!requestDto.getAdminToken().equals(ADMIN_TOKEN)) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRole.ADMIN;
        }

        User user = new User(username, password, email, role);

        userRepository.save(user);

        return user;
    }

    public void kakaoLogin(String authorizedCode) {
        // 카카오 OAuth2 를 통해 카카오 사용자 정보 조회
        KakaoUserInfo userInfo = kakaoOAuth2.getUserInfo(authorizedCode);

        Long kakaoId = userInfo.getId();
        String nickname = userInfo.getNickname();
        String email = userInfo.getEmail();

        // DB 에 중복된 Kakao Id 가 있는지 확인
        User kakaoUser = userRepository.findByKakaoId(kakaoId).orElse(null);

        // 카카오 정보로 회원가입
        if (kakaoUser == null) {

            User sameEmailUser = userRepository.findByEmail(email).orElse(null);

            if (sameEmailUser != null) {
                kakaoUser = sameEmailUser;
                kakaoUser.setKakaoId(kakaoId);
            }
            else{
                String username = nickname;
                String password = kakaoId + ADMIN_TOKEN;

                String encodedPassword = passwordEncoder.encode(password);
                UserRole role = UserRole.USER;

                kakaoUser = new User(username, encodedPassword, email, role, kakaoId);
            }

            userRepository.save(kakaoUser);
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

