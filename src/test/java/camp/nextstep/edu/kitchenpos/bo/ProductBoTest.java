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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class})
class ProductBoTest {

    @Mock
    private ProductDao dao;

    @Mock
    private Product product;

    @InjectMocks
    private ProductBo bo;

    @DisplayName("생성하려는 상품의 가격이 없으면 해당 상품을 생성할 수 없다")
    @Test
    void createTest_withNullPrice() {
        when(product.getPrice()).thenReturn(null);

        assertThatThrownBy(() -> bo.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("생성하려는 상품의 가격이 음수면 해당 상품을 생성할 수 없다")
    @Test
    void createTest_withNegativePrice() {
        when(product.getPrice()).thenReturn(new BigDecimal(-1));

        assertThatThrownBy(() -> bo.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 생성 성공")
    @Test
    void createTest_basic() {
        Product expectedProduct = new Product();

        when(product.getPrice()).thenReturn(BigDecimal.ZERO);
        when(dao.save(product)).thenReturn(expectedProduct);

        assertThat(bo.create(product)).isEqualTo(expectedProduct);
    }
}
