package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductBoTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductBo productBo;

    @DisplayName("product 생성 성공 테스트")
    @Test
    void createProductSuccessTest() {

        // given
        Product product = new Product();
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(17000));
        product.setName("강정치킨");

        // when
        when(productDao.save(product)).thenReturn(product);

        // then
        Product result = productBo.create(product);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(17000));
        assertThat(result.getName()).isEqualTo("강정치킨");

    }

    @DisplayName("product 생성 실패 테스트 - price 정보가 null 또는 0보다 작음")
    @ParameterizedTest
    @MethodSource(value = "createProductFailureInfo")
    void createProductFailureTest(BigDecimal price) {

        // given
        Product product = new Product();
        product.setId(1L);
        product.setPrice(price);
        product.setName("강정치킨");

        // then
        assertThrows(IllegalArgumentException.class, () -> productBo.create(product));
    }

    private static Stream<BigDecimal> createProductFailureInfo() {
        return Stream.of(null, BigDecimal.valueOf(-1L));
    }
}
