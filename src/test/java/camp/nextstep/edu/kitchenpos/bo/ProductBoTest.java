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
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductBoTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductBo productBo;

    @DisplayName("상품 가격은 0원 이상이다")
    @Test
    void nonZero() {
        // given
        final Product negativeNumber = createProduct(-1000);
        final Product nullPrice = createProduct();

        // when
        // then
        assertThrows(IllegalArgumentException.class,
                () -> productBo.create(negativeNumber));

        assertThrows(IllegalArgumentException.class,
                () -> productBo.create(nullPrice));
    }

    @DisplayName("상품을 추가할 수 있다")
    @Test
    void create() {
        // given
        final Product product = createProduct(1000);

        given(productDao.save(product)).willReturn(product);

        // when
        final Product actual = productBo.create(product);

        // then
        assertThat(actual).isNotNull();
    }

    @DisplayName("모든 상품을 조회할 수 있다")
    @Test
    void list() {
        // given
        final List<Product> products = Arrays.asList(
                createProduct(1), createProduct(2), createProduct(3));

        given(productDao.findAll()).willReturn(products);

        // when
        final List<Product> actual = productBo.list();

        // then
        assertThat(actual).hasSize(products.size());
    }

    private Product createProduct() {
        final Product product = new Product();
        return product;
    }

    private Product createProduct(final int number) {
        final Product product = createProduct();
        product.setPrice(new BigDecimal(number));
        return product;
    }
}