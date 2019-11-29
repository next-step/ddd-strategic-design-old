package camp.nextstep.edu.kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.when;

import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Product;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductBoTest {

    @InjectMocks
    private ProductBo productBo;
    @Mock
    private ProductDao productDao;

    @DisplayName("0 이상의 가격과 이름이 주어졌을 때 상품 생성 성공")
    @ParameterizedTest
    @ValueSource(longs = {0L, 100L, 10000L, 2500000L})
    void create(long price) {
        //given
        Product request = new Product();
        request.setName("상품1");
        request.setPrice(BigDecimal.valueOf(price));

        //when
        Product result = productBo.create(request);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(request.getName());
        assertThat(result.getPrice()).isEqualTo(request.getPrice());
    }

    @DisplayName("0 미만의 가격과 이름이 주어졌을 때 상품 생성 성공")
    @ParameterizedTest
    @ValueSource(longs = {-1L, -100L, -10000L, -2500000L})
    void given_negative_price_create_product_fails(long price) {
        //given
        Product request = new Product();
        request.setName("상품1");
        request.setPrice(BigDecimal.valueOf(price));

        //when
        assertThatIllegalArgumentException().isThrownBy(() -> {
            Product result = productBo.create(request);
        });

    }

    @DisplayName("가격이 없을때 상품 생성 실패")
    @Test
    void given_null_price_create_product_fails() {
        //given
        Product request = new Product();
        request.setName("상품1");
        request.setPrice(null);

        //when
        assertThatIllegalArgumentException().isThrownBy(() -> {
            Product result = productBo.create(request);
        });

    }

    @DisplayName("전체 상품 목록을 조회할 수 있다")
    @Test
    void list() {
        //given
        List<Product> products = mockProducts();
        when(productDao.findAll()).thenReturn(products);
        //when
        List<Product> result = productBo.list();
        //then
        assertThat(result).isNotNull();
    }

    private List<Product> mockProducts() {
        Product product = new Product();
        product.setId(1L);
        product.setName("테스트");
        product.setPrice(BigDecimal.valueOf(100L));
        return Arrays.asList(product);
    }
}