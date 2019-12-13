package camp.nextstep.edu.kitchenpos.product.application;

import camp.nextstep.edu.kitchenpos.product.dao.InMemoryProductDao;
import camp.nextstep.edu.kitchenpos.product.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("상품 Business Object 테스트 클래스")
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    private Product product;

    private ProductDao productDao = new InMemoryProductDao();

    private ProductService productService;

    @BeforeEach
    void setup() {
        productService = new ProductService(productDao);
        product = new Product();
        product.setId(1L);
        product.setName("후라이드 치킨");
        product.setPrice(BigDecimal.valueOf(19000L));
    }

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
        product.setPrice(null);

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(product));
    }

    @DisplayName("[상품 생성] 상품의 금액은이 0원 미만일 경우 예외를 발생한다.")
    @Test
    void whenPriceIsLessThanZero_thenFail() {
        // given
        BigDecimal productPrice = BigDecimal.valueOf(-1);
        product.setPrice(productPrice);

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(product));
    }

    @DisplayName("[상품 생성] 상품을 생성할 수 있다.")
    @Test
    void create() {
        // when
        Product actual = productService.create(product);

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getId()).isEqualTo(product.getId()),
                () -> assertThat(actual.getName()).isEqualTo(product.getName()),
                () -> assertThat(actual.getPrice()).isEqualTo(product.getPrice())
        );
    }

    @DisplayName("[상품 조회] 상품을 조회할 수 있다.")
    @Test
    void list() {
        // given
        final Product ohterProduct = new Product();
        ohterProduct.setId(2L);
        ohterProduct.setName("양념 치킨");
        ohterProduct.setPrice(BigDecimal.valueOf(20000L));

        productService.create(product);
        productService.create(ohterProduct);

        // when
        List<Product> actual = productService.list();

        // then
        assertThat(actual).containsExactlyInAnyOrder(product, ohterProduct);
    }
}