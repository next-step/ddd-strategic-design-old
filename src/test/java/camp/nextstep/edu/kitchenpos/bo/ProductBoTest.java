package camp.nextstep.edu.kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Product;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductBoTest {

    private static final long PRODUCT_ID = 1L;

    @Mock
    Product product;

    @Mock
    ProductDao productDao;

    @InjectMocks
    ProductBo productBo;

    @DisplayName("상품 생성중 메뉴의 금액이 0원 이하이면 예외를 발생한다.")
    @Test
    void create_productPriceIsNegative() {
        // Given
        final BigDecimal negativeProductPrice = BigDecimal.valueOf(-1L);
        given(product.getPrice()).willReturn(negativeProductPrice);

        // When
        // Then
        assertThatThrownBy(() -> productBo.create(product)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품을 생선한다.")
    @Test
    void create_success() {

        // Given
        final BigDecimal productPrice = BigDecimal.valueOf(10000L);
        final String productName = "후라이드치킨";
        final Product product = new Product();
        product.setId(PRODUCT_ID);
        product.setName(productName);
        product.setPrice(productPrice);

        given(productDao.save(product)).willReturn(product);

        // When
        final Product saveProduct = productBo.create(product);

        // Then
        assertAll(
                () -> assertThat(saveProduct).isNotNull(),
                () -> assertThat(saveProduct.getName()).isEqualTo(productName),
                () -> assertThat(saveProduct.getPrice()).isEqualTo(productPrice));
    }

    @DisplayName("상품의 목록을 조회할 수 있다.")
    @Test
    void productList() {
        // Given
        given(productDao.findAll()).willReturn(Arrays.asList(product, product, product));

        // When
        final List<Product> productList = productBo.list();

        // Then
        assertThat(productList).hasSize(3);
    }
}
