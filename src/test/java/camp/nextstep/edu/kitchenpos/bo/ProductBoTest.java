package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductBoTest {

    @InjectMocks
    ProductBo productBo;

    @Mock
    ProductDao productDao;

    @DisplayName("상품을 생성할 수 있다")
    @Test
    void createSuccess() {
        // given
        Product request = new Product();
        request.setId(0L);
        request.setName("상품");
        request.setPrice(BigDecimal.valueOf(1000));

        given(productDao.save(any())).willReturn(request);

        // when
        Product actual = productBo.create(request);


        // then
        assertThat(actual.getId()).isEqualTo(0L);
        assertThat(actual.getName()).isEqualTo("상품");
        assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(1000));
    }

    @DisplayName("상품의 가격이 존재하지 않으면 생성할 수 없다")
    @ParameterizedTest
    @NullSource
    void createFail_priceIsNull(BigDecimal price) {
        // given
        Product request = new Product();
        request.setId(0L);
        request.setName("상품");
        request.setPrice(price);

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> {
            productBo.create(request);
        });
    }

    @DisplayName("상품의 가격이 음수이면 생성할 수 없다")
    @ParameterizedTest
    @ValueSource(longs = {Long.MIN_VALUE, -1L})
    void createFail_priceIsNegative(Long price) {
        // given
        Product request = new Product();
        request.setId(0L);
        request.setName("상품");
        request.setPrice(BigDecimal.valueOf(price));

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> {
            productBo.create(request);
        });
    }

    @DisplayName("상품 목록을 조회할 수 있다")
    @Test
    void list() {
        // given
        Product product1 = new Product();
        product1.setId(0L);
        product1.setName("상품1");
        product1.setPrice(BigDecimal.valueOf(1000L));

        Product product2 = new Product();
        product2.setId(1L);
        product2.setName("상품2");
        product2.setPrice(BigDecimal.valueOf(1000L));

        List<Product> products = Arrays.asList(product1, product2);

        given(productDao.findAll()).willReturn(products);

        // when
        assertThat(productBo.list()).hasSize(2);
    }
}