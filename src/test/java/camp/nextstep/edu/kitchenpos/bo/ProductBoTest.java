package camp.nextstep.edu.kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import camp.nextstep.edu.kitchenpos.product.bo.ProductBo;
import camp.nextstep.edu.kitchenpos.product.domain.Product;
import camp.nextstep.edu.kitchenpos.product.infra.ProductDao;
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

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductBo productBo;

    private final int DEFAULT_PRICE = 5000;

    @Test
    @DisplayName("상품를 등록할 수 있다.")
    void add(){

        Product product = this.createProduct(DEFAULT_PRICE);
        when(productDao.save(any())).thenReturn(product);


        Product actual = productBo.create(product);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(product);
    }

    @Test
    @DisplayName("상품의 가격은 0 이상이어야 한다.")
    void product_price_check(){

        Product product = this.createProduct(-1);


        assertThrows(IllegalArgumentException.class, () -> productBo.create(product));
    }

    @Test
    @DisplayName("전체 목록을 확인할 수 있다.")
    void product_list(){

        List<Product> products = Arrays.asList(this.createProduct(DEFAULT_PRICE), this.createProduct(DEFAULT_PRICE));
        when(productDao.findAll()).thenReturn(products);


        List<Product> actual = productBo.list();


        assertThat(actual).isNotNull();
        assertThat(actual).containsOnlyElementsOf(actual);
    }

    private Product createProduct(int price){
        Product product = new Product();
        product.setName("상품");
        product.setPrice(new BigDecimal(price));

        return product;
    }

}