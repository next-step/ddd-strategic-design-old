package camp.nextstep.edu.kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import camp.nextstep.edu.kitchenpos.product.bo.ProductBo;
import camp.nextstep.edu.kitchenpos.product.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.product.model.Product;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith({MockitoExtension.class})
class ProductBoTest {

  @Mock
  private ProductDao productDao;

  @InjectMocks
  private ProductBo productBo;

  @DisplayName("상품을 등록할 수 있다.")
  @Test
  public void create() throws Exception {
    //given
    int price = 5000;
    Product product = createProduct(price);

    given(productDao.save(product)).willReturn(product);

    //when
    Product createdProduct = productBo.create(product);

    //then
    assertThat(createdProduct).isNotNull();
  }

  @DisplayName("상품의 가격은 0원 이상이어야 한다.")
  @Test
  public void negativePriceWillReturnException() throws Exception {
    //given
    int price = -5000;
    Product product = createProduct(price);

    //then
    assertThrows(IllegalArgumentException.class, () -> productBo.create(product));
  }

  @DisplayName("등록된 상품 목록을 볼 수 있다.")
  @Test
  public void findAllProduct() throws Exception {
    //given
    int price = 1000;
    List<Product> products = Arrays.asList(createProduct(price), createProduct(price));

    given(productDao.findAll()).willReturn(products);

    //when
    List<Product> productList = productBo.list();

    //then
    assertThat(productList).hasSize(products.size());
  }

  private Product createProduct(int price) {
    Product product = new Product();
    product.setPrice(new BigDecimal(price));
    return product;
  }
}
