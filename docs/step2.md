# 2단계 - 테스트를 통한 코드 보호

## 실습 환경 구축
* ddd-strategic-design를 기반으로 미션을 진행한다.
* 미션 시작 버튼을 눌러 미션을 시작한다.
* 저장소에 자신의 GitHub 아이디로 된 브랜치가 생성되었는지 확인한다.
* 저장소를 자신의 계정으로 Fork 한다.
* 요구 사항에 대한 구현을 완료한 후, 작업이 다 되었으면 Push를 한다.
* next-step/ddd-strategic-design에서 Pull Request를 작성한다.
* 리뷰 받은 내용을 토대로 코드를 리팩터링하고 다시 Push를 한다.
* 모든 피드백을 완료하면 다음 단계를 도전하고 앞의 과정을 반복한다.

## 요구 사항
* 작성한 키친포스의 요구 사항을 토대로 테스트 코드를 작성한다.
* 모든 Business Object에 대한 테스트 코드를 작성한다.
    * @SpringBootTest를 이용한 통합 테스트 코드
    * 또는 @ExtendWith(MockitoExtension.class)를 이용한 단위 테스트 코드를 작성한다.
* Controller에 대한 테스트 코드 작성은 권장하지만 필수는 아니다.
* 미션을 진행함에 있어 아래 문서를 적극 활용한다.
    * [Testing in Spring Boot](https://www.baeldung.com/spring-boot-testing)

## 힌트
* 텐트를 세우기 위해 말뚝이 필요하듯이 리팩터링을 하기 위해선 테스트 코드가 필요하다.

![camping](https://cdn.pixabay.com/photo/2014/04/03/09/58/camping-309472_960_720.png)

* Business Object
```java
@ExtendWith(MockitoExtension.class)
public class BoTest {
    @Mock
    private Dao dao;

    @InjectMocks
    private Bo bo;

    @Test
    public void test() {
        given(dao.findById(anyLong()))
                .willReturn(new Object());
    }
}
```

* Controller
```java
@ExtendWith(value = {SpringExtension.class})
@WebMvcTest
public class ControllerTest {
    @Autowired
    private WebMvc webMvc;
    
    @Test
    public void test() {
        webMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }
}
```