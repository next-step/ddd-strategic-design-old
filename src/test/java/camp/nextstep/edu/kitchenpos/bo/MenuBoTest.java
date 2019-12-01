package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.MenuDao;
import camp.nextstep.edu.kitchenpos.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.dao.MenuProductDao;
import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Menu;
import camp.nextstep.edu.kitchenpos.model.MenuProduct;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuBoTest {

    @Mock private MenuDao menuDao;
    @Mock private MenuGroupDao menuGroupDao;
    @Mock private MenuProductDao menuProductDao;
    @Mock private ProductDao productDao;

    @InjectMocks
    private MenuBo menuBo;

    @DisplayName("메뉴를 추가할 수 있다")
    @Test
    void add() {
        // given
        final Menu menu = createMenu(0, 0);

        given(menuGroupDao.existsById(any(Long.class))).willReturn(true);
        given(menuDao.save(any())).willReturn(menu);

        // when
        final Menu actual = menuBo.create(menu);

        // then
        assertThat(actual).isNotNull();
    }

    @DisplayName("모든 메뉴를 조회할 수 있다")
    @Test
    void findAll() {
        // given
        given(menuDao.findAll()).willReturn(Arrays.asList(new Menu(), new Menu()));

        // when
        final List<Menu> actual = menuBo.list();

        // then
        assertThat(actual.size()).isEqualTo(2);
    }

    @DisplayName("가격은 0원 이상이다")
    @Test
    void priceGraterThenZero() {
        // given
        final Menu menu = createMenu(-1, 1L);

        // when
        // then
        assertThrows(IllegalArgumentException.class,
                () -> menuBo.create(menu));
    }

    @DisplayName("메뉴의 가격은 메뉴별 메뉴상품 가격의 총합보다 클 수 없다")
    @Test
    void menuProductSumGraterThenPrice() {
        // given
        final Menu menu = createMenu(2001, 0, createMenuProduct(2, 0L));
        final Product product = createProduct(1000);

        given(menuGroupDao.existsById(any(Long.class))).willReturn(true);
        given(productDao.findById(any(Long.class))).willReturn(Optional.of(product));

        // when
        // then
        assertThrows(IllegalArgumentException.class,
                () -> menuBo.create(menu));
    }

    @DisplayName("메뉴묶음을 가지고 있다")
    @Test
    void hasMenuGroup() {
        // given
        final Menu menu = createMenu(2001, 0, createMenuProduct(2, 0L));

        given(menuGroupDao.existsById(any(Long.class))).willReturn(false);

        // when
        // then
        assertThat(menu.getPrice()).isEqualTo(new BigDecimal(2001));
        assertThrows(IllegalArgumentException.class,
                () -> menuBo.create(menu));
    }

    @DisplayName("메뉴는 메뉴상품 목록을 가지고 있다")
    @Test
    void hasMenuProducts() {
        // given
        final Menu menu = createMenu(500, 0, createMenuProduct(1, 0L));
        final Product product = createProduct(500);

        given(menuGroupDao.existsById(any(Long.class))).willReturn(true);
        given(menuDao.save(any())).willReturn(menu);
        given(productDao.findById(any(Long.class))).willReturn(Optional.of(product));

        // when
        final Menu actual = menuBo.create(menu);

        // then
        assertThat(actual.getMenuProducts()).isNotEmpty();
    }

    private Menu createMenu(final int number, final long menuGroupId, final MenuProduct... menuProducts) {
        final Menu menu = new Menu();
        menu.setPrice(new BigDecimal(number));
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(Arrays.asList(menuProducts));
        return menu;
    }

    private MenuProduct createMenuProduct(final int quantity, final long productId) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(productId);
        return menuProduct;
    }

    private Product createProduct(final int value) {
        final Product product = new Product();
        product.setPrice(new BigDecimal(value));
        return product;
    }
}
