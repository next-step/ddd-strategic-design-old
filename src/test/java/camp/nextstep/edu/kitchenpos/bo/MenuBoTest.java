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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MenuBoTest {

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

    @DisplayName("menu 생성 성공 테스트")
    @ParameterizedTest
    @MethodSource("makeSuccessMenuInfo")
    void createMenuSuccessTest(Menu menu) {

        // when
        when(menuGroupDao.existsById(anyLong())).thenReturn(true);

        Product chickenProduct = makeChickenProductInfo();
        Product cheesePizzaProduct = makeCheesePizzaProductInfo();

        lenient().when(productDao.findById(1L)).thenReturn(Optional.of(chickenProduct));
        lenient().when(productDao.findById(2L)).thenReturn(Optional.of(cheesePizzaProduct));
        when(menuDao.save(menu)).thenReturn(menu);
        when(menuProductDao.save(any(MenuProduct.class))).thenReturn(new MenuProduct());

        // then
        Menu result = menuBo.create(menu);
        assertThat(result.getMenuGroupId()).isEqualTo(menu.getMenuGroupId());
        assertThat(result.getName()).isEqualTo(menu.getName());
        assertThat(result.getPrice()).isEqualTo(menu.getPrice());
        assertThat(result.getMenuProducts().size()).isEqualTo(menu.getMenuProducts().size());

    }

    @DisplayName("menu 생성 실패 테스트 - price 값이 null 이나 음수")
    @ParameterizedTest
    @MethodSource("createMenuFailureForInvalidPriceTest")
    void createMenuFailureForInvalidPriceTest(Menu menu) {
        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    private static Stream<Menu> createMenuFailureForInvalidPriceTest() {

        Menu nullPriceMenu = new Menu();
        nullPriceMenu.setPrice(null);

        Menu negativeNumberPriceMenu = new Menu();
        negativeNumberPriceMenu.setPrice(BigDecimal.valueOf(-1));

        return Stream.of(nullPriceMenu, negativeNumberPriceMenu);
    }

    @DisplayName("menu 생성 실패 테스트 - group id 가 존재하지 않음")
    @Test
    void createMenuFailureForInvalidGroupIdTest() {

        // given
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(19000L));
        menu.setMenuGroupId(1L);

        // when
        when(menuGroupDao.existsById(anyLong())).thenReturn(false);

        // then
        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    @DisplayName("menu 생성 실패 테스트 - product id 가 존재하지 않음")
    @ParameterizedTest
    @MethodSource("makeFailureMenuInfo")
    void createMenuFailureForInvalidProductIdTest(Menu menu) {

        // when
        when(menuGroupDao.existsById(anyLong())).thenReturn(true);
        when(productDao.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));

    }

    private static Stream<Menu> makeFailureMenuInfo() {
        Menu menu = new Menu();
        menu.setId(1L);
        menu.setMenuGroupId(1L);
        menu.setName("실패메뉴");
        menu.setPrice(BigDecimal.valueOf(19000));
        menu.setMenuProducts(makeFriedChickenMenuProductInfo());
        return Stream.of(menu);
    }

    private static Stream<Menu> makeSuccessMenuInfo() {

        Menu friedChicken = new Menu();
        friedChicken.setId(1L);
        friedChicken.setMenuGroupId(1L);
        friedChicken.setName("후라이드+후라이드");
        friedChicken.setPrice(BigDecimal.valueOf(19000));
        friedChicken.setMenuProducts(makeFriedChickenMenuProductInfo());

        Menu cheesePizza = new Menu();
        cheesePizza.setId(2L);
        cheesePizza.setMenuGroupId(2L);
        cheesePizza.setName("치즈 피자");
        cheesePizza.setPrice(BigDecimal.valueOf(21000));
        cheesePizza.setMenuProducts(makeCheesePizzaMenuProductInfo());

        return Stream.of(friedChicken, cheesePizza);
    }

    private static List<MenuProduct> makeFriedChickenMenuProductInfo() {

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(1L);
        menuProduct.setMenuId(1L);
        menuProduct.setQuantity(3);

        return Collections.singletonList(menuProduct);
    }

    private static List<MenuProduct> makeCheesePizzaMenuProductInfo() {

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(2L);
        menuProduct.setMenuId(2L);
        menuProduct.setQuantity(10);

        return Collections.singletonList(menuProduct);
    }

    private static Product makeChickenProductInfo() {
        Product product = new Product();
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(19000));
        product.setName("후라이드치킨");
        return product;
    }

    private static Product makeCheesePizzaProductInfo() {
        Product product = new Product();
        product.setId(2L);
        product.setPrice(BigDecimal.valueOf(21000));
        product.setName("치즈피자");
        return product;
    }
}
