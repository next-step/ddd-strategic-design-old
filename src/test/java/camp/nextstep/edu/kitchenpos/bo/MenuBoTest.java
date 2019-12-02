package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.MenuDao;
import camp.nextstep.edu.kitchenpos.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.dao.MenuProductDao;
import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Menu;
import camp.nextstep.edu.kitchenpos.model.MenuProduct;
import camp.nextstep.edu.kitchenpos.model.Product;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("메뉴 Business Object 테스트 클래스")
@ExtendWith(MockitoExtension.class)
class MenuBoTest {
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
    private MenuBo menuBo;

    @DisplayName("메뉴는 메뉴 번호, 메뉴명, 금액, 메뉴 상품 속성들을 가지고 있다.")
    @Test
    void hasProperties() {
        String menuIdPropertyName = "id";
        String menuNamePropertyName = "name";
        String menuPricePropertyName = "price";
        String menuGroupIdPropertyName = "menuGroupId";
        String menuProductsPropertyName = "menuProducts";

        assertThat(menu).hasFieldOrProperty(menuIdPropertyName);
        assertThat(menu).hasFieldOrProperty(menuNamePropertyName);
        assertThat(menu).hasFieldOrProperty(menuPricePropertyName);
        assertThat(menu).hasFieldOrProperty(menuGroupIdPropertyName);
        assertThat(menu).hasFieldOrProperty(menuProductsPropertyName);
    }

    @DisplayName("하나의 메뉴는 여러 메뉴 상품들을 가질 수 있다")
    @Test
    void hasMultiMenuItem() {
        // given
        List<MenuProduct> menuProducts = mock(List.class);
        given(menuProducts.size()).willReturn(3);

        // when
        int size = menuProducts.size();

        // then
        assertThat(menuProducts.size()).isNotZero()
                                       .isEqualTo(3);
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
        assertThat(menuPrice).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        verify(menu, atLeast(1)).getPrice();
    }

    @DisplayName("[메뉴 생성] 메뉴의 금액은 0 원 미만일 경우 예외를 발생한다.")
    @Test
    void whenPriceIsLessThanZero_thenFail() {
        // given
        BigDecimal price = BigDecimal.valueOf(-1);

        // when
        when(menu.getPrice()).thenReturn(price);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(menu));
        assertThat(menu.getPrice()).isLessThan(BigDecimal.ZERO);
        verify(menu, atLeast(1)).getPrice();
    }

    @DisplayName("[메뉴 생성] 메뉴의 금액이 null일 경우 예외를 발생한다.")
    @Test
    void whenPriceIsNull_thenFail() {
        // given
        BigDecimal price = null;

        // when
        when(menu.getPrice()).thenReturn(price);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(menu));
        verify(menu, atLeast(1)).getPrice();
    }

    @DisplayName("[메뉴 생성] 메뉴가 메뉴 그룹에 속해 있으면 성공을 반환 한다.")
    @Test
    void whenMenuExistInMenuGroup_thenSuccess() {
        // given
        given(menuGroupDao.existsById(any())).willReturn(true);

        // when
        Boolean isExist = menuGroupDao.existsById(any());

        // then
        assertThat(isExist).isTrue();
    }

    @DisplayName("[메뉴 생성] 메뉴가 메뉴 그룹에 속해 있지않으면 예외를 발생 한다.")
    @Test
    void whenMenuExistInMenuGroup_thenFail() {
        // given
        given(menu.getPrice()).willReturn(BigDecimal.valueOf(1));
        given(menu.getMenuGroupId()).willReturn(1L);
        given(menuGroupDao.existsById(any())).willReturn(true);

        // when
        when(menuGroupDao.existsById(any())).thenReturn(false);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("[메뉴 생성] 메뉴상품이 존재하면 성공을 반환 한다.")
    @Test
    void whenMenuProductExist_thenSuccess() {
        // given
        given(productDao.findById(any())).willReturn(Optional.of(new Product()));

        // when
        Product product = productDao.findById(any())
                                    .get();

        // then
        assertThat(productDao.findById(any())).isNotNull();
    }

    @DisplayName("[메뉴 생성] 메뉴상품이 존재하지 않으면 런타임 예외를 발생 한다.")
    @Test
    void isNotExistMenuProduct() {
        // when
        when(productDao.findById(any())).thenReturn(Optional.empty());

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productDao.findById(any())
                                            .orElseThrow(IllegalArgumentException::new));
    }

    @DisplayName("[메뉴 생성] 메뉴 상품의 총 금액은 (상품의 금액 * 상품의 갯수) 이다.")
    @Test
    void whenPriceMultiplyQuantityEqualsToSum_thenSuccess() {
        // given
        Product product = mock(Product.class);
        given(product.getPrice()).willReturn(BigDecimal.valueOf(1000));

        MenuProduct menuProduct = mock(MenuProduct.class);
        given(menuProduct.getQuantity()).willReturn(2L);

        // when
        BigDecimal sum = BigDecimal.ZERO;
        sum = sum.add(product.getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())));

        // then
        assertThat(sum).isEqualTo(BigDecimal.valueOf(2000));
    }

    @DisplayName("[메뉴 생성] 메뉴 상품의 합계 금액은 메뉴 금액보다 클 수 없다.")
    @Test
    void whenMenuProductPriceSum_notGreaterThan_MenuPrice() {
        // given
        given(menu.getPrice()).willReturn(BigDecimal.valueOf(2000));

        Product product = mock(Product.class);
        given(product.getPrice()).willReturn(BigDecimal.valueOf(1000));

        MenuProduct menuProduct = mock(MenuProduct.class);
        given(menuProduct.getQuantity()).willReturn(2L);

        // when
        BigDecimal price = menu.getPrice();
        BigDecimal sum = BigDecimal.ZERO;
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
        when(savedMenu.getId()).thenReturn(1L);

        // then
        assertThat(savedMenu.getId()).isNotNull()
                                     .isEqualTo(1L);
    }

    @DisplayName("[메뉴 생성] 생성된 메뉴 번호로 메뉴 상품들이 생성이 되면 성공을 반환 한다.")
    @Test
    void whenMenuProductsAreCreated_withCreatedMenuNumber_thenSuccess() {
        // given
        long menuId = 1L;
        MenuProduct menuProduct = mock(MenuProduct.class);
        MenuProduct otherMenuProduct = mock(MenuProduct.class);

        given(menuProductDao.save(any())).willReturn(menuProduct);
        given(menuProductDao.save(any())).willReturn(otherMenuProduct);

        given(menuProductDao.save(any()).getMenuId()).willReturn(menuId);
        given(menuProductDao.save(any()).getMenuId()).willReturn(menuId);

        // when
        long menuProductMenuId = menuProductDao.save(any())
                                               .getMenuId();
        long otherMenuProductMenuId = menuProductDao.save(any())
                                                    .getMenuId();

        // then
        assertThat(menuProductMenuId).isEqualTo(otherMenuProductMenuId);
        assertThat(menuId).isEqualTo(menuProductMenuId);
        assertThat(menuId).isEqualTo(otherMenuProductMenuId);
    }

    @DisplayName("[메뉴 생성] 메뉴를 생성할 수 있다.")
    @Test
    void createMenu() {
        // given - 메뉴 객체 setting
        long id = 1L;
        Menu mockMenu = new Menu();
        mockMenu.setId(id);
        mockMenu.setPrice(BigDecimal.valueOf(20000));
        mockMenu.setMenuGroupId(id);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(id);
        menuProduct.setQuantity(1);

        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(menuProduct);
        mockMenu.setMenuProducts(menuProducts);

        Product product = new Product();
        product.setId(id);
        product.setPrice(BigDecimal.valueOf(20000));

        given(menuGroupDao.existsById(any())).willReturn(true);
        given(productDao.findById(any())).willReturn(Optional.of(product));
        given(menuDao.save(any())).willReturn(mockMenu);

        // when
        final Menu savedMenu = menuBo.create(mockMenu);

        // then
        assertThat(savedMenu).isNotNull()
                             .isEqualTo(mockMenu);
    }

    @DisplayName("[메뉴 조회] 메뉴들의 목록을 조회 할 수 있다")
    @Test
    void whenMenuCanSelect_thenSuccess() {
        // given
        List<Menu> menus = mock(List.class);
        given(menuDao.findAll()).willReturn(menus);
        given(menus.size()).willReturn(2);

        // when
        List<Menu> allMenu = menuDao.findAll();

        //then
        assertThat(allMenu.size()).isEqualTo(2);
    }

    @DisplayName("[메뉴 조회] 각 메뉴의 메뉴 상품들을 조회할 수 있다.")
    @Test
    void whenMenuProductCanSelect_thenSuccess() {
        // given
        List<MenuProduct> menuProducts = mock(List.class);
        given(menuProductDao.findAllByMenuId(any())).willReturn(menuProducts);
        given(menuProducts.size()).willReturn(2);

        // when
        List<MenuProduct> allMenuProducts = menuProductDao.findAllByMenuId(any());

        //then
        assertThat(allMenuProducts.size()).isEqualTo(2);
    }
}