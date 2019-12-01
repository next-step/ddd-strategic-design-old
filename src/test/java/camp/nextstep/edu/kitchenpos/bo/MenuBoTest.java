package camp.nextstep.edu.kitchenpos.bo;


import static camp.nextstep.edu.kitchenpos.bo.MockBuilder.mockValidProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import camp.nextstep.edu.kitchenpos.dao.MenuDao;
import camp.nextstep.edu.kitchenpos.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.dao.MenuProductDao;
import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Menu;
import camp.nextstep.edu.kitchenpos.model.MenuProduct;
import camp.nextstep.edu.kitchenpos.model.Product;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuBoTest {

    @InjectMocks
    private MenuBo menuBo;
    @Mock
    private MenuDao menuDao;
    @Mock
    private MenuGroupDao menuGroupDao;
    @Mock
    private MenuProductDao menuProductDao;
    @Mock
    private ProductDao productDao;

    @DisplayName("메뉴를 생성할 수 있다")
    @Test
    void create() {
        //given
        Product product = mockValidProduct(1L);
        product.setPrice(BigDecimal.valueOf(1500L));
        when(productDao.findById(any())).thenReturn(Optional.of(product));

        List<MenuProduct> menuProducts = Arrays.asList(mockMenuProduct(product.getId(), 2));

        Menu request = new Menu();
        request.setName("떡볶이 1인분");
        request.setPrice(BigDecimal.valueOf(2500));
        request.setMenuGroupId(100L);

        request.setMenuProducts(menuProducts);

        when(menuGroupDao.existsById(request.getMenuGroupId())).thenReturn(true);

        when(menuDao.save(any())).thenAnswer(invocation -> {
            Menu menu = invocation.getArgument(0);
            menu.setId(1L);
            return menu;
        });

        when(menuProductDao.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        //when
        Menu menu = menuBo.create(request);

        //then
        assertThat(menu.getId()).isNotNull();
        assertThat(menu.getName()).isEqualTo(request.getName());
        assertThat(menu.getPrice()).isEqualTo(request.getPrice());
        assertThat(menu.getMenuProducts()).hasSize(menuProducts.size());
        assertThat(menu.getMenuGroupId()).isEqualTo(request.getMenuGroupId());

        MenuProduct requested = menuProducts.get(0);
        MenuProduct created = menu.getMenuProducts().get(0);

        assertThat(created.getMenuId()).isEqualTo(menu.getId());
        assertThat(created.getProductId()).isEqualTo(requested.getProductId());
        assertThat(created.getQuantity()).isEqualTo(requested.getQuantity());
        assertThat(created.getSeq()).isEqualTo(requested.getSeq());

    }

    @DisplayName("0원 미만의 가격이 주어졌을 때 메뉴 생성이 실패한다")
    @ParameterizedTest
    @ValueSource(longs = {-1L, -1000L, 25000L})
    void given_negative_price_create_menu_fail(long price) {
        //given
        List<MenuProduct> menuProducts = Arrays.asList(mockMenuProduct(1L, 2));

        Menu request = new Menu();
        request.setName("떡볶이 1인분");
        request.setPrice(BigDecimal.valueOf(price));
        request.setMenuGroupId(100L);
        request.setMenuProducts(menuProducts);

        //then
        assertThatIllegalArgumentException().isThrownBy(() ->
            menuBo.create(request)
        );
    }

    @DisplayName("메뉴의 가격은 판매할 상품 목록의 개별 (수량 * 가격)의 총합보다 크면 메뉴 생성이 실패한다")
    @Test
    void given_sum_of_menu_products_is_smaller_then_menu_price_create_menu_fail() {
        //given
        Product product = mockValidProduct(1L);
        product.setPrice(BigDecimal.valueOf(1500L));
        when(productDao.findById(any())).thenReturn(Optional.of(product));

        List<MenuProduct> menuProducts = Arrays.asList(mockMenuProduct(product.getId(), 2));

        Menu request = new Menu();
        request.setName("떡볶이 1인분");
        request.setMenuGroupId(100L);
        request.setPrice(BigDecimal.valueOf(3001L));
        request.setMenuProducts(menuProducts);

        when(menuGroupDao.existsById(request.getMenuGroupId())).thenReturn(true);

        //then
        assertThatIllegalArgumentException().isThrownBy(() ->
            menuBo.create(request)
        );
    }

    @DisplayName("속하는 메뉴 그룹이 없을 경우 메뉴 생성이 실패한다")
    @Test
    void given_menu_group_not_exists_then_menu_price_create_menu_fail() {
        //given
        List<MenuProduct> menuProducts = Arrays.asList(mockMenuProduct(1L, 2L));

        Menu request = new Menu();
        request.setName("떡볶이 1인분");
        request.setPrice(BigDecimal.valueOf(2500));
        request.setMenuGroupId(100L);
        request.setMenuProducts(menuProducts);

        when(menuGroupDao.existsById(request.getMenuGroupId())).thenReturn(true);
        //then
        assertThatIllegalArgumentException().isThrownBy(() ->
            menuBo.create(request)
        );
    }

    @DisplayName("전체 메뉴를 조회할 수 있다")
    @Test
    void list() {
        //given
        Menu menu = new Menu();
        menu.setId(1L);
        menu.setName("떡볶이 1인분");
        menu.setPrice(BigDecimal.valueOf(4000L));
        menu.setMenuGroupId(1L);

        MenuProduct menuProduct = MockBuilder.mockValidMenuProduct(menu.getId());
        List<MenuProduct> menuProducts = Arrays.asList(menuProduct);

        when(menuDao.findAll()).thenReturn(Arrays.asList(menu));
        when(menuProductDao.findAllByMenuId(eq(menu.getId()))).thenReturn(menuProducts);

        //when
        List<Menu> result = menuBo.list();
        //then
        assertThat(result).hasSize(1);
        Menu queriedMenu = result.get(0);
        assertThat(queriedMenu.getMenuProducts()).isEqualTo(menuProducts);
    }

    private MenuProduct mockMenuProduct(long productId, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(0L);
        menuProduct.setMenuId(null);
        menuProduct.setProductId(productId);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}