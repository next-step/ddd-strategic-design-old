package camp.nextstep.edu.kitchenpos.menu.application;

import camp.nextstep.edu.kitchenpos.menu.dao.MenuDao;
import camp.nextstep.edu.kitchenpos.menugroup.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.menu.dao.MenuProductDao;
import camp.nextstep.edu.kitchenpos.product.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.menu.domain.Menu;
import camp.nextstep.edu.kitchenpos.menu.domain.MenuProduct;
import camp.nextstep.edu.kitchenpos.product.model.Product;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@DisplayName("메뉴 Business Object 테스트 클래스")
@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    @Mock
    private Menu menu;

    @Mock
    private MenuDao menuDao;

    @Mock
    private MenuGroupDao menuGroupDao;

    @Mock
    private MenuProductDao menuProductDao;

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private MenuService menuService;

    private final Long DEFAULT_ID = 1L;

    @DisplayName("메뉴는 메뉴 번호, 메뉴명, 금액, 메뉴 상품 속성들을 가지고 있다.")
    @Test
    void hasProperties() {
        String menuIdPropertyName = "id";
        String menuNamePropertyName = "name";
        String menuPricePropertyName = "price";
        String menuGroupIdPropertyName = "menuGroupId";
        String menuProductsPropertyName = "menuProducts";

        assertAll("has properties menu",
                () -> assertThat(menu).hasFieldOrProperty(menuIdPropertyName),
                () -> assertThat(menu).hasFieldOrProperty(menuNamePropertyName),
                () -> assertThat(menu).hasFieldOrProperty(menuPricePropertyName),
                () -> assertThat(menu).hasFieldOrProperty(menuGroupIdPropertyName),
                () -> assertThat(menu).hasFieldOrProperty(menuProductsPropertyName)
        );
    }

    @DisplayName("하나의 메뉴는 여러 메뉴 상품들을 가질 수 있다")
    @Test
    void hasMultiMenuItem() {
        // given
        final int menuProductSize = 3;
        List<MenuProduct> menuProducts = mock(List.class);
        given(menuProducts.size()).willReturn(menuProductSize);

        // when
        int size = menuProducts.size();

        // then
        assertThat(menuProducts).hasSize(menuProductSize);
    }

    @DisplayName("[메뉴 생성] 메뉴의 금액은 0 원 이상이면 성공을 반환 한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 10, 100, 1000})
    void whenPriceIsGreaterThanOrEqualToZero_thenSuccess(int input) {
        // given
        BigDecimal price = BigDecimal.valueOf(input);
        given(menu.getPrice()).willReturn(price);

        // when
        BigDecimal menuPrice = menu.getPrice();

        // then
        assertAll(
                () -> assertThat(menuPrice).isGreaterThanOrEqualTo(BigDecimal.ZERO),
                () -> then(menu).should().getPrice()
        );
    }

    @DisplayName("[메뉴 생성] 메뉴의 금액은 0 원 미만일 경우 예외를 발생한다.")
    @Test
    void whenPriceIsLessThanZero_thenFail() {
        // given
        BigDecimal price = BigDecimal.valueOf(-1);
        given(menu.getPrice()).willReturn(price);

        // when then
        assertAll(
                () -> assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> menuService.create(menu)),
                () -> assertThat(menu.getPrice()).isLessThan(BigDecimal.ZERO),
                () -> verify(menu, atLeastOnce()).getPrice()
        );
    }

    @DisplayName("[메뉴 생성] 메뉴의 금액이 null일 경우 예외를 발생한다.")
    @Test
    void whenPriceIsNull_thenFail() {
        // given
        BigDecimal price = null;

        // when
        when(menu.getPrice()).thenReturn(price);

        // then
        assertAll(
                () -> assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> menuService.create(menu)),
                () -> then(menu).should().getPrice()
        );
    }

    @DisplayName("[메뉴 생성] 메뉴가 메뉴 그룹에 속해 있으면 성공을 반환 한다.")
    @Test
    void whenMenuExistInMenuGroup_thenSuccess() {
        // given
        given(menuGroupDao.existsById(DEFAULT_ID)).willReturn(true);

        // when
        Boolean isExist = menuGroupDao.existsById(DEFAULT_ID);

        // then
        assertThat(isExist).isTrue();
    }

    @DisplayName("[메뉴 생성] 메뉴가 메뉴 그룹에 속해 있지않으면 예외를 발생 한다.")
    @Test
    void whenMenuExistInMenuGroup_thenFail() {
        // given
        BigDecimal price = BigDecimal.valueOf(1);
        given(menu.getPrice()).willReturn(price);
        given(menu.getMenuGroupId()).willReturn(DEFAULT_ID);
        given(menuGroupDao.existsById(DEFAULT_ID)).willReturn(false);

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("[메뉴 생성] 메뉴상품이 존재하면 성공을 반환 한다.")
    @Test
    void whenMenuProductExist_thenSuccess() {
        // given
        given(productDao.findById(DEFAULT_ID)).willReturn(Optional.of(new Product()));

        // when
        Product product = productDao.findById(DEFAULT_ID)
                                    .get();

        // then
        assertThat(productDao.findById(DEFAULT_ID)).isNotNull();
    }

    @DisplayName("[메뉴 생성] 메뉴상품이 존재하지 않으면 런타임 예외를 발생 한다.")
    @Test
    void isNotExistMenuProduct() {
        // when
        when(productDao.findById(DEFAULT_ID)).thenReturn(Optional.empty());

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productDao.findById(DEFAULT_ID)
                                            .orElseThrow(IllegalArgumentException::new));
    }

    @DisplayName("[메뉴 생성] 메뉴 상품의 총 금액은 (상품의 금액 * 상품의 갯수) 이다.")
    @Test
    void whenPriceMultiplyQuantityEqualsToSum_thenSuccess() {
        // given
        final int thousand = 1000;
        final int twoThousand = 2000;
        final long quantity = 2L;

        Product product = mock(Product.class);
        given(product.getPrice()).willReturn(BigDecimal.valueOf(thousand));

        MenuProduct menuProduct = mock(MenuProduct.class);
        given(menuProduct.getQuantity()).willReturn(quantity);

        // when
        BigDecimal sum = BigDecimal.ZERO;
        sum = sum.add(product.getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())));

        // then
        assertThat(sum).isEqualTo(BigDecimal.valueOf(twoThousand));
    }

    @DisplayName("[메뉴 생성] 메뉴 상품의 합계 금액은 메뉴 금액보다 클 수 없다.")
    @Test
    void whenMenuProductPriceSum_notGreaterThan_MenuPrice() {
        // given
        final BigDecimal productPrice = BigDecimal.valueOf(1000);
        final BigDecimal menuPrice = BigDecimal.valueOf(2000);
        final long quantity = 2L;

        Product product = mock(Product.class);
        given(product.getPrice()).willReturn(productPrice);
        given(menu.getPrice()).willReturn(menuPrice);

        MenuProduct menuProduct = mock(MenuProduct.class);
        given(menuProduct.getQuantity()).willReturn(quantity);

        // when
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal price = menu.getPrice();
        sum = sum.add(product.getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())));
        int result = price.compareTo(sum);

        // then
        assertThat(result).isLessThanOrEqualTo(0);
    }

    @DisplayName("[메뉴 생성] 메뉴가 생성시메뉴 번호가 생성 되면 성공을 반환 한다.")
    @Test
    void whenMenuCreateWithMenuNumber_thenSuccess() {
        // given
        given(menuDao.save(menu)).willReturn(menu);

        // when
        Menu savedMenu = menuDao.save(menu);
        when(savedMenu.getId()).thenReturn(DEFAULT_ID);

        // then
        assertThat(savedMenu.getId()).isNotNull()
                                     .isEqualTo(DEFAULT_ID);
    }

    @DisplayName("[메뉴 생성] 생성된 메뉴 번호로 메뉴 상품들이 생성이 되면 성공을 반환 한다.")
    @Test
    void whenMenuProductsAreCreated_withCreatedMenuNumber_thenSuccess() {
        // given
        MenuProduct menuProduct = mock(MenuProduct.class);
        MenuProduct otherMenuProduct = mock(MenuProduct.class);

        given(menuProductDao.save(menuProduct)).willReturn(menuProduct);
        given(menuProductDao.save(otherMenuProduct)).willReturn(otherMenuProduct);

        given(menuProductDao.save(menuProduct).getMenuId()).willReturn(DEFAULT_ID);
        given(menuProductDao.save(otherMenuProduct).getMenuId()).willReturn(DEFAULT_ID);

        // when
        long menuProductMenuId = menuProductDao.save(menuProduct)
                                               .getMenuId();
        long otherMenuProductMenuId = menuProductDao.save(otherMenuProduct)
                                                    .getMenuId();

        // then
        assertAll(
                () -> assertThat(menuProductMenuId).isEqualTo(otherMenuProductMenuId),
                () -> assertThat(DEFAULT_ID).isEqualTo(menuProductMenuId),
                () -> assertThat(DEFAULT_ID).isEqualTo(otherMenuProductMenuId)
        );
    }

    @DisplayName("[메뉴 생성] 메뉴를 생성할 수 있다.")
    @Test
    void createMenu() {
        // given - 메뉴 객체 setting
        BigDecimal price = BigDecimal.valueOf(20000);
        int quantity = 1;

        Menu mockMenu = new Menu();
        mockMenu.setId(DEFAULT_ID);
        mockMenu.setMenuGroupId(DEFAULT_ID);
        mockMenu.setPrice(price);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(DEFAULT_ID);
        menuProduct.setQuantity(quantity);

        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(menuProduct);
        mockMenu.setMenuProducts(menuProducts);

        Product product = new Product();
        product.setId(DEFAULT_ID);
        product.setPrice(price);

        given(menuGroupDao.existsById(DEFAULT_ID)).willReturn(true);
        given(productDao.findById(DEFAULT_ID)).willReturn(Optional.of(product));
        given(menuDao.save(mockMenu)).willReturn(mockMenu);

        // when
        final Menu savedMenu = menuService.create(mockMenu);

        // then
        assertThat(savedMenu).isNotNull()
                             .isEqualTo(mockMenu);
    }

    @DisplayName("[메뉴 조회] 메뉴들의 목록을 조회 할 수 있다")
    @Test
    void whenMenuCanSelect_thenSuccess() {
        // given
        final int menuProductSize = 2;
        List<Menu> menus = mock(List.class);
        given(menuDao.findAll()).willReturn(menus);
        given(menus.size()).willReturn(menuProductSize);

        // when
        List<Menu> allMenu = menuDao.findAll();

        //then
        assertThat(allMenu.size()).isEqualTo(menuProductSize);
    }

    @DisplayName("[메뉴 조회] 각 메뉴의 메뉴 상품들을 조회할 수 있다.")
    @Test
    void whenMenuProductCanSelect_thenSuccess() {
        // given
        final int menuProductSize = 2;
        List<MenuProduct> menuProducts = mock(List.class);
        given(menuProductDao.findAllByMenuId(DEFAULT_ID)).willReturn(menuProducts);
        given(menuProducts.size()).willReturn(menuProductSize);

        // when
        List<MenuProduct> allMenuProducts = menuProductDao.findAllByMenuId(DEFAULT_ID);

        //then
        assertThat(allMenuProducts).hasSize(menuProductSize);
    }
}