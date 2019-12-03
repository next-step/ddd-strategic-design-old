package camp.nextstep.edu.kitchenpos.bo;


import camp.nextstep.edu.kitchenpos.dao.MenuDao;
import camp.nextstep.edu.kitchenpos.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.dao.MenuProductDao;
import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Menu;
import camp.nextstep.edu.kitchenpos.model.MenuGroup;
import camp.nextstep.edu.kitchenpos.model.MenuProduct;
import camp.nextstep.edu.kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

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

    @DisplayName("메뉴 등록에 성공한다")
    @Test
    void createOfMenu() {
        // given
        long menuGroupId = 1l;
        Product product = new Product();
        product.setId(1l);
        product.setName("후라이드치킨");
        product.setPrice(BigDecimal.valueOf(5_000));

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(2);

        List<MenuProduct> menuProducts = Arrays.asList(menuProduct);

        Menu menu = new Menu();
        menu.setName("후라이드+후라이드");
        menu.setMenuGroupId(menuGroupId);
        menu.setPrice(BigDecimal.valueOf(10_000));
        menu.setMenuProducts(menuProducts);

        given(menuGroupDao.existsById(anyLong())).willReturn(Boolean.TRUE);
        given(productDao.findById(anyLong())).willReturn(Optional.of(product));
        given(menuDao.save(any())).willReturn(menu);
        given(menuProductDao.save(any())).willReturn(menuProduct);

        // when
        Menu expectedMenu = menuBo.create(menu);

        // then
        assertThat(expectedMenu.getMenuProducts()).size()
                                                  .isEqualTo(1);
    }

    @DisplayName("메뉴 가격이 0미만인 경우 등록 실패")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "-1")
    void createMenu(BigDecimal wrongPrice) {
        String nameOfMenuGroup = "추천메뉴";
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(1l);
        menuGroup.setName(nameOfMenuGroup);

        String nameOfProduct = "후라이드치킨";
        Product product = new Product();
        product.setId(1l);
        product.setName(nameOfProduct);
        product.setPrice(BigDecimal.valueOf(5_000));

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(2);

        List<MenuProduct> menuProducts = Arrays.asList(menuProduct);

        String nameOfMenu = "후라이드+후라이드";
        Menu menu = new Menu();
        menu.setName(nameOfMenu);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setPrice(wrongPrice);
        menu.setMenuProducts(menuProducts);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴 그룹에 등록되지 않았을 경우 등록에 실패")
    @Test
    void createWhenPriceLessThanZero_exception() {
        long menuGroupId = 1l;

        String nameOfProduct = "후라이드치킨";
        Product product = new Product();
        product.setId(1l);
        product.setName(nameOfProduct);
        product.setPrice(BigDecimal.valueOf(5_000));

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(2);

        List<MenuProduct> menuProducts = Arrays.asList(menuProduct);

        String nameOfMenu = "후라이드+후라이드";
        Menu menu = new Menu();
        menu.setName(nameOfMenu);
        menu.setMenuGroupId(menuGroupId);
        menu.setPrice(BigDecimal.ZERO);
        menu.setMenuProducts(menuProducts);

        given(menuGroupDao.existsById(anyLong())).willReturn(false);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("미등록된 상품일 시 메뉴 등록 실패")
    @Test
    void createMenuWhen() {
        String nameOfMenuGroup = "추천메뉴";
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(1l);
        menuGroup.setName(nameOfMenuGroup);

        String nameOfProduct = "후라이드치킨";
        Product product = new Product();
        product.setId(1l);
        product.setName(nameOfProduct);
        product.setPrice(BigDecimal.valueOf(5_000));

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(2);

        List<MenuProduct> menuProducts = Arrays.asList(menuProduct);

        String nameOfMenu = "후라이드+후라이드";
        Menu menu = new Menu();
        menu.setName(nameOfMenu);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setPrice(BigDecimal.ZERO);
        menu.setMenuProducts(menuProducts);

        given(menuGroupDao.existsById(anyLong())).willReturn(true);


        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴의 가격이 메뉴 상품들의 가격의 총액보다 클 경우 등록 실패")
    @Test
    void createMenu2() {

    }

    @DisplayName("전체 메뉴를 보는데 성공한다")
    @Test
    void listMenu() {

    }
}