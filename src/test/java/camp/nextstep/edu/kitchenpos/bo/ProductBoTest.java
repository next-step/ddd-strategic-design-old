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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductBoTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductBo productBo;

    @DisplayName("product 생성 테스트")
    @Test
    public void createTest() {

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
}
