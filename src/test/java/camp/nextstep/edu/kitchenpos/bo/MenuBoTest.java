package camp.nextstep.edu.kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import camp.nextstep.edu.kitchenpos.dao.MenuDao;
import camp.nextstep.edu.kitchenpos.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.dao.MenuProductDao;
import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Menu;
import camp.nextstep.edu.kitchenpos.model.MenuProduct;
import camp.nextstep.edu.kitchenpos.model.Product;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuBoTest {

    private static final Long MENU_ID = 10L;

    private static final Long MENU_GROUP_ID = 20L;
    private static final Long NOT_EXISTS_MENU_GROUP_ID = 21L;

    private static final Long PRODUCT_ID = 30L;
    private static final Long NOT_EXISTS_PRODUCT_ID = 31L;

    @Mock
    private Menu menu;

    @Mock
    private MenuProduct menuProduct;

    @Mock
    private Product product;

    @Mock
    private MenuDao menuDao;

    @Mock
    private MenuGroupDao menuGroupDao;

    @Mock
    private MenuProductDao menuProductDao;

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private MenuBo menuBo;

    @DisplayName("메뉴 생성중 메뉴의 금액이 0원 이하이면 예외를 발생한다.")
    @Test
    void create_menuPriceIsNegative() {
        // Given
        final BigDecimal negativeMenuPrice = BigDecimal.valueOf(-1L);
        given(menu.getPrice()).willReturn(negativeMenuPrice);

        // When
        // Then
        assertThatThrownBy(() -> menuBo.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성중 메뉴의 금액이 null이면 예외를 발생한다.")
    @Test
    void create_menuPriceIsNull() {
        // Given
        given(menu.getPrice()).willReturn(null);

        // When
        // Then
        assertThatThrownBy(() -> menuBo.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성중 메뉴에 메뉴그룹이 존재하지 않으면 예외를 발생 한다.")
    @Test
    void create_menuGroupNotExists() {
        // Given
        final BigDecimal menuPrice = BigDecimal.valueOf(1000L);
        given(menu.getPrice()).willReturn(menuPrice);
        given(menu.getMenuGroupId()).willReturn(NOT_EXISTS_MENU_GROUP_ID);
        given(menuGroupDao.existsById(NOT_EXISTS_MENU_GROUP_ID)).willReturn(false);

        // When
        // Then
        assertThatThrownBy(() -> menuBo.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성중 메뉴상품이 존재하지 않으면 예외를 발생 한다.")
    @Test
    void create_isNotExistMenuProduct() {
        // Given
        final BigDecimal menuPrice = BigDecimal.valueOf(1000L);
        given(menu.getPrice()).willReturn(menuPrice);
        given(menu.getMenuGroupId()).willReturn(MENU_GROUP_ID);
        given(menuGroupDao.existsById(MENU_GROUP_ID)).willReturn(true);
        given(menuProduct.getProductId()).willReturn(NOT_EXISTS_PRODUCT_ID);
        given(menu.getMenuProducts()).willReturn(Lists.list(menuProduct));
        given(productDao.findById(NOT_EXISTS_PRODUCT_ID)).willReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> menuBo.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성중 메뉴가격이 메뉴상품들에 가격과 수량에 합보다 크면 예외를 발생 한다.")
    @Test
    void create_menuPriceIsLargerThanMenuProductPrice() {
        // Given
        final BigDecimal menuPrice = BigDecimal.valueOf(3000L);
        given(menu.getPrice()).willReturn(menuPrice);
        given(menu.getMenuGroupId()).willReturn(MENU_GROUP_ID);
        given(menuGroupDao.existsById(MENU_GROUP_ID)).willReturn(true);

        given(menu.getMenuProducts()).willReturn(Lists.list(menuProduct));
        given(menuProduct.getProductId()).willReturn(PRODUCT_ID);
        given(productDao.findById(PRODUCT_ID)).willReturn(Optional.of(product));

        BigDecimal productPrice = BigDecimal.valueOf(1000L);
        given(product.getPrice()).willReturn(productPrice);

        final long shortageQuantity = 2L;
        given(menuProduct.getQuantity()).willReturn(shortageQuantity);

        // When
        // Then
        assertThatThrownBy(() -> menuBo.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 생성한다.")
    @Test
    void create_success() {
        // Given
        final BigDecimal menuPrice = BigDecimal.valueOf(3000L);

        final Menu menu = new Menu();
        menu.setId(MENU_ID);
        menu.setMenuGroupId(MENU_GROUP_ID);
        menu.setPrice(menuPrice);

        final long adequateQuantity = 3L;
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(PRODUCT_ID);
        menuProduct.setQuantity(adequateQuantity);

        final List<MenuProduct> menuProductList = new ArrayList<>();
        menuProductList.add(menuProduct);
        menu.setMenuProducts(menuProductList);

        final BigDecimal productPrice = BigDecimal.valueOf(1000L);
        final Product product = new Product();
        product.setId(PRODUCT_ID);
        product.setPrice(productPrice);

        given(menuGroupDao.existsById(MENU_GROUP_ID)).willReturn(true);
        given(productDao.findById(PRODUCT_ID)).willReturn(Optional.of(product));
        given(menuDao.save(menu)).willReturn(menu);

        // When
        final Menu saveMenu = menuBo.create(menu);

        // Then
        assertAll(
                () -> assertThat(saveMenu.getMenuGroupId()).isEqualTo(MENU_GROUP_ID),
                () -> assertThat(saveMenu.getPrice()).isEqualTo(menuPrice),
                () -> assertThat(saveMenu.getMenuProducts()).hasSize(1));
    }

    @DisplayName("메뉴 목록을 조회할 수 있다")
    @Test
    void menuList() {
        // Given
        given(menuDao.findAll()).willReturn(Arrays.asList(menu, menu, menu));

        // When
        final List<Menu> menuList = menuBo.list();

        // Then
        assertThat(menuList).hasSize(3);
    }
}
