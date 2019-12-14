package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.InMemoryProductDao;
import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MockitoExtension.class)
class ProductBoTest {

    private ProductBo productBo;

    private ProductDao productDao;

    @BeforeEach
    void setUp() {
        productDao = new InMemoryProductDao();
        productBo = new ProductBo(productDao);
    }

    @DisplayName("점주는 상품을 등록 할 수 있다")
    @Test
    void create() {
        // given
        BigDecimal price = BigDecimal.valueOf(10_000);
        Product product = createProduct(1L, BigDecimal.valueOf(10_000));

        // when
        Product actual = productBo.create(product);

        // then
        assertThat(actual).isEqualTo(product);
        assertAll(
                () -> assertThat(actual.getPrice()).isEqualTo(price)
        );
    }


    @DisplayName("상품 가격이 0원이거나, 입력하지 않은 경우 등록에 실패한다")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "-1")
    void createWhenPriceLessThanZero_exception(BigDecimal wrongPrice) {
        // given
        Product registeredProduct = createProduct(1l, wrongPrice);

        // exception
        Assertions.assertThatIllegalArgumentException()
                  .isThrownBy(() -> productBo.create(registeredProduct));
    }

    @DisplayName("등록된 모든 상품을 조회할 수 있다.")
    @Test
    void list() {
        // given
        List<Product> products = Arrays.asList(createProduct(1L, BigDecimal.valueOf(10_000)),
                                               createProduct(2L, BigDecimal.valueOf(8_000)));

        // when
        List<Product> actual = productBo.list();

        // then
        assertThat(actual).containsExactlyInAnyOrderElementsOf(products);
    }

    private Product createProduct(Long id, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setName("양념치킨");
        product.setPrice(price);
        productDao.save(product);
        return product;
    }
}