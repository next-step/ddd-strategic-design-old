package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.product.bo.ProductBo;
import camp.nextstep.edu.kitchenpos.product.model.Product;
import camp.nextstep.edu.kitchenpos.support.dao.InMemoryProductDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("`상품`은 `고객`이 `매장`에게 `주문`하는 대상을 뜻한다.")
class ProductBoTest {

    private ProductBo productBo;

    @BeforeEach
    void setUp() {
        productBo = new ProductBo(new InMemoryProductDao());
    }

    @DisplayName("`상품` 등록 시 금액이 0 이상일 경우 등록한다.")
    @ParameterizedTest
    @ValueSource(strings = {"0", "1", "1231241"})
    void create(final String price) {
        // given
        final Product product = new Product();
        product.setPrice(new BigDecimal(price));

        // when
        final Product savedProduct = productBo.create(product);

        // then
        assertThat(savedProduct).isEqualTo(product);
    }

    @DisplayName("`상품` 등록 시 금액이 0 보다 적을 경우 예외처리 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"-123234", "-1"})
    void create_lowerPrice(final String price) {
        // given
        final Product product = new Product();
        product.setPrice(new BigDecimal(price));

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productBo.create(product));
    }

    @DisplayName("`상품` 조회 시 등록된 `상품`이 없다면 빈 리스트를 반환한다.")
    @Test
    void list_empty() {
        // when
        final List<Product> products = productBo.list();

        // then
        assertThat(products).isEmpty();
    }

    @DisplayName("`상품`을 하나 등록 후 조회 시 등록된 `상품` 하나를 반환한다.")
    @Test
    void list_single() {
        // given
        final Product product = new Product();
        productBo.create(product);

        // when
        final List<Product> products = productBo.list();

        // then
        assertThat(products).containsExactly(product);
    }

    @DisplayName("`상품` 조회 시 등록된 `상품`의 갯수 만큼 반환한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 3, 100, 234})
    void list_many(final int size) {
        // given
        IntStream.range(0, size)
                .mapToObj(ignored -> {
                    final Product product = new Product();
                    product.setPrice(new BigDecimal("1000"));

                    return product;
                })
                .forEach(productBo::create);

        // when
        final List<Product> products = productBo.list();

        // then
        assertThat(products).hasSize(size);
    }
}