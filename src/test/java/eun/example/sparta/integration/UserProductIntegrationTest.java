package eun.example.sparta.integration;

import eun.example.sparta.dto.ProductMypriceRequestDto;
import eun.example.sparta.dto.ProductRequestDto;
import eun.example.sparta.dto.SignupRequestDto;
import eun.example.sparta.model.Product;
import eun.example.sparta.model.User;
import eun.example.sparta.model.UserRole;
import eun.example.sparta.service.ProductService;
import eun.example.sparta.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserProductIntegrationTest {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ProductService productService;


    Long userId = null;
    Product createdProduct = null;
    int updatedMyPrice = -1;

    @Test
    @DisplayName("회원 가입 전 관심상품 등록 : 실패")
    @Order(1)
    void test1(){
        //given
        String title = "Apple <b>에어팟</b> 2세대 유선충전 모델 (MV7N2KH/A)";
        String imageUrl = "https://shopping-phinf.pstatic.net/main_1862208/18622086330.20200831140839.jpg";
        String linkUrl = "https://search.shopping.naver.com/gate.nhn?id=18622086330";
        int lPrice = 77000;
        ProductRequestDto requestDto = new ProductRequestDto(
                title,
                imageUrl,
                linkUrl,
                lPrice
        );

        //when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(requestDto, userId);
        });

        //then
        assertEquals("회원 Id 가 유효하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원 가입")
    @Order(2)
    void test2(){
        //given
        String username = "홍길동";
        String password = "1234";
        String email = "email@naver.com";
        boolean admin = false;

        SignupRequestDto signupRequestDto = new SignupRequestDto(
            username,
            password,
            email,
            admin,
            null
        );

        //when
        User user = userService.registerUser(signupRequestDto);

        //then
        assertNotNull(user.getId());
        assertEquals(username, user.getUsername());
        assertTrue(passwordEncoder.matches(password, user.getPassword()));
        assertEquals(email, user.getEmail());
        assertEquals(UserRole.USER, user.getRole());

        userId = user.getId();

    }

    @Test
    @DisplayName("가입된 회원으로 관심상품 등록")
    @Order(3)
    void test3(){
        //given
        String title = "Apple <b>에어팟</b> 2세대 유선충전 모델 (MV7N2KH/A)";
        String imageUrl = "https://shopping-phinf.pstatic.net/main_1862208/18622086330.20200831140839.jpg";
        String linkUrl = "https://search.shopping.naver.com/gate.nhn?id=18622086330";
        int lPrice = 77000;
        ProductRequestDto requestDto = new ProductRequestDto(
                title,
                imageUrl,
                linkUrl,
                lPrice
        );

        //when
        Product product = productService.createProduct(requestDto, userId);

        //then
        assertNotNull(product.getId());
        assertEquals(userId, product.getUserId());
        assertEquals(title, product.getTitle());
        assertEquals(imageUrl, product.getImage());
        assertEquals(linkUrl, product.getLink());
        assertEquals(lPrice, product.getLprice());
        assertEquals(0, product.getMyprice());
        createdProduct = product;

    }

    @Test
    @DisplayName("신규 등록된 관심상품의 희망 최저가 변")
    @Order(4)
    void test4(){
        //given
        int myPrice = 7000;
        Long productId = createdProduct.getId();
        ProductMypriceRequestDto productMypriceRequestDto = new ProductMypriceRequestDto(myPrice);

        //when
        Product product = productService.updateProduct(productId, productMypriceRequestDto);

        //then
        assertNotNull(product.getId());
        assertEquals(this.userId, product.getUserId());
        assertEquals(myPrice, product.getMyprice());
        updatedMyPrice = myPrice;
    }

    @Test
    @DisplayName("회원이 등록한 모든 관심상품 조회")
    @Order(5)
    void test5(){
        //given
        //when
        int page = 0;
        int size = 10;
        String sortBy = "id";
        boolean isAsc = true;

        Page<Product> productList = productService.getProducts(userId, page, size, sortBy, isAsc);
        //then
        // 1. 전체 상품에서 테스트에 의해 생성된 상품 찾아오기 (상품의 id 로 찾음)증
        Product product = productList.stream()
                .filter(product1 -> product1.getId().equals(createdProduct.getId()))
                .findFirst()
                .orElse(null);
        // 2. Order(1) 테스트에 의해 생성된 상품과 일치하는지 검증
        assertNotNull(product);
        assertEquals(userId, product.getUserId());
        assertEquals(this.createdProduct.getId(), product.getId());
        assertEquals(this.createdProduct.getTitle(), product.getTitle());
        assertEquals(this.createdProduct.getImage(), product.getImage());
        assertEquals(this.createdProduct.getLink(), product.getLink());
        assertEquals(this.createdProduct.getLprice(), product.getLprice());
        // 3. Order(2) 테스트에 의해 myPrice 가격이 정상적으로 업데이트되었는지 검증
        assertEquals(updatedMyPrice, product.getMyprice());
    }
}
