package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@DisplayName("상품 Business Object 테스트 클래스")
@ExtendWith(MockitoExtension.class)
class ProductBoTest {
    @Mock
    private Product product;

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductBo productBo;

    @DisplayName("상품은 상품 번호, 상품명, 상품 가격 속성들을 가지고 있다.")
    @Test
    void hasProperties() {
        String productIdPropertyName = "id";
        String productNamePropertyName = "name";
        String productPricePropertyName = "price";

        assertAll(
                () -> assertThat(product).hasFieldOrProperty(productIdPropertyName),
                () -> assertThat(product).hasFieldOrProperty(productNamePropertyName),
                () -> assertThat(product).hasFieldOrProperty(productPricePropertyName)
        );
    }

    @DisplayName("[상품 생성] 상품의 금액이 없으면 예외를 발생한다.")
    @Test
    void whenPriceIsNull_thenFail() {
        // given
        BigDecimal price = null;
        given(product.getPrice()).willReturn(price);

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productBo.create(product));
    }

    @DisplayName("[상품 생성] 상품의 금액은이 0원 미만일 경우 예외를 발생한다.")
    @Test
    void whenPriceIsLessThanZero_thenFail() {
        // given
        BigDecimal price = BigDecimal.valueOf(-1);
        given(product.getPrice()).willReturn(price);

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productBo.create(product));
    }

    @DisplayName("[상품 생성] 상품을 생성할 수 있다.")
    @Test
    void create() {
        // given
        final BigDecimal productPrice = BigDecimal.valueOf(10000);
        given(product.getPrice()).willReturn(productPrice);
        given(productDao.save(product)).willReturn(product);

        // when
        Product savedProduct = productBo.create(product);

        // then
        assertAll(
                () -> assertThat(savedProduct).isNotNull(),
                () -> assertThat(savedProduct).isEqualTo(product),
                () -> then(productDao).should().save(product)
        );
    }

    @DisplayName("[상품 조회] 상품을 조회할 수 있다.")
    @Test
    void list() {
        // given
        final int productsSize = 2;
        List<Product> products = mock(List.class);
        given(productDao.findAll()).willReturn(products);
        given(products.size()).willReturn(productsSize);

        // when
        List<Product> savedProducts = productDao.findAll();

        // then
        assertThat(savedProducts).hasSize(productsSize);
    }
}