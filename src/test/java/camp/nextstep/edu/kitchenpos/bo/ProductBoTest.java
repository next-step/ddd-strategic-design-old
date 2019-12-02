package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.MenuProductDao;
import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Menu;
import camp.nextstep.edu.kitchenpos.model.MenuProduct;
import camp.nextstep.edu.kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
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

    @DisplayName("메뉴그룹은 메뉴 그룹 번호와 메뉴 그룹명 속성들을 가지고 있다.")
    @Test
    void hasProperties() {
        String productIdPropertyName = "id";
        String productNamePropertyName = "name";
        String productPricePropertyName = "price";

        assertThat(product).hasFieldOrProperty(productIdPropertyName);
        assertThat(product).hasFieldOrProperty(productNamePropertyName);
        assertThat(product).hasFieldOrProperty(productPricePropertyName);
    }

    @DisplayName("[상품 생성] 상품의 금액이 없으면 예외를 발생한다.")
    @Test
    void whenPriceisNull_thenFail() {
        // given
        BigDecimal price = null;
        given(product.getPrice()).willReturn(price);

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productBo.create(product));
    }

    @DisplayName("[메뉴 생성] 메뉴의 금액은 0 원 미만일 경우 예외를 발생한다.")
    @Test
    void whenPriceisLessThanZero_thenFail() {
        // given
        BigDecimal price = BigDecimal.valueOf(-1);
        given(product.getPrice()).willReturn(price);

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productBo.create(product));
    }

    @DisplayName("[메뉴 생성] 메뉴를 생성할 수 있다.")
    @Test
    void create() {
        // given
        given(product.getPrice()).willReturn(BigDecimal.valueOf(10000));
        given(productDao.save(any())).willReturn(product);

        // when
        Product savedProduct = productBo.create(product);

        // then
        assertThat(savedProduct).isNotNull()
                                .isEqualTo(product);
        verify(productDao, atLeastOnce()).save(any());
    }

    @DisplayName("[메뉴 조회] 메뉴를 조회할 수 있다.")
    @Test
    void list() {
        // given
        List<Product> products = mock(List.class);
        given(productDao.findAll()).willReturn(products);
        given(products.size()).willReturn(2);

        // when
        List<Product> savedProducts = productDao.findAll();

        // then
        assertThat(savedProducts.size()).isEqualTo(2);
    }
}