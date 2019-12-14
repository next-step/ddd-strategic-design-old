package camp.nextstep.edu.kitchenpos.bo;


import camp.nextstep.edu.kitchenpos.dao.*;
import camp.nextstep.edu.kitchenpos.model.Menu;
import camp.nextstep.edu.kitchenpos.model.MenuGroup;
import camp.nextstep.edu.kitchenpos.model.MenuProduct;
import camp.nextstep.edu.kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MockitoExtension.class)
class MenuBoTest {

    private MenuBo menuBo;

    private MenuDao menuDao;

    private MenuGroupDao menuGroupDao;

    private MenuProductDao menuProductDao;

    private ProductDao productDao;

    @BeforeEach
    void setUp() {
        menuDao = new InMemoryMenuDao();
        menuGroupDao = new InMemoryMenuGroupDao();
        menuProductDao = new InMemoryMenuProductDao();
        productDao = new InMemoryProductDao();
        menuBo = new MenuBo(menuDao, menuGroupDao, menuProductDao, productDao);
    }

    @DisplayName("메뉴 등록에 성공한다")
    @Test
    void create() {
        // given
        String menuOfName = "후라이드 단품";
        BigDecimal price = BigDecimal.valueOf(10_000);

        Product product = createProduct(price);
        productDao.save(product);
        MenuGroup menuGroup = createOfMenuGroup("추천메뉴");
        menuGroupDao.save(menuGroup);
        List<MenuProduct> menuProducts = createOfMenuProduct(product);
        Menu menu = createOfMenu(menuOfName, price, menuGroup, menuProducts);
        menuDao.save(menu);

        // when
        Menu actual = menuBo.create(menu);

        // then
        assertAll(
                () -> assertThat(actual.getName()).isEqualTo(menuOfName),
                () -> assertThat(actual.getPrice()).isEqualTo(price)
        );
        assertThat(actual.getMenuProducts()).hasSize(1);
    }

    @DisplayName("메뉴 가격이 0미만인 경우 등록 실패")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "-1")
    void createWhenMenuPriceLessThanZero_exception(BigDecimal wrongPrice) {
        // given
        Product product = createProduct(BigDecimal.valueOf(10_000));
        productDao.save(product);
        Product product2 = createProduct(BigDecimal.valueOf(9_000));
        productDao.save(product2);
        MenuGroup menuGroup = createOfMenuGroup("추천메뉴");
        menuGroupDao.save(menuGroup);
        List<MenuProduct> menuProducts = createOfMenuProduct(product, product2);

        Menu menu = createOfMenu("후라이드+양념",
                                 wrongPrice,
                                 menuGroup, menuProducts);
        // exception
        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("미등록된 메뉴그룹일 경우 메뉴 등록에 실패")
    @Test
    void createWhenNotRegisteredMenuGroup_fail() {
        // given
        long nonMenuGroupId = 9999L;

        BigDecimal priceOfProduct = BigDecimal.valueOf(10_000);
        BigDecimal priceOfProduct2 = BigDecimal.valueOf(9_000);
        Product product = createProduct(priceOfProduct);
        productDao.save(product);
        Product product2 = createProduct(priceOfProduct2);
        productDao.save(product2);
        List<MenuProduct> menuProducts = createOfMenuProduct(product, product2);

        Menu menu = new Menu();
        menu.setName("후라이드+양념");
        menu.setPrice(priceOfProduct.add(priceOfProduct2));
        menu.setMenuGroupId(nonMenuGroupId);
        menu.setMenuProducts(menuProducts);

        // exception
        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuBo.create(menu));
    }


    @DisplayName("미등록된 상품일 경우 메뉴 등록에 실패")
    @Test
    void createWhenNotRegisteredProduct_fail() {
        // given
        Product product = createProduct(BigDecimal.valueOf(10_000));
        MenuGroup menuGroup = createOfMenuGroup("추천메뉴");
        menuGroupDao.save(menuGroup);
        List<MenuProduct> menuProducts = createOfMenuProduct(product);

        Menu menu = new Menu();
        menu.setName("후라이드 단품");
        menu.setPrice(BigDecimal.valueOf(10_000)
                                .add(BigDecimal.valueOf(9_000)));
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);

        // exception
        assertThatNullPointerException()
                .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴의 가격이 메뉴상품의 전체 가격의 합보다 큰 경우 등록에 실패")
    @Test
    void failIfMenuPriceEqualsTotalPriceOfMenuItem() {
        // given
        BigDecimal priceOfProduct = BigDecimal.valueOf(10_000);
        BigDecimal priceOfProduct2 = BigDecimal.valueOf(9_000);
        BigDecimal priceOfMenu = priceOfProduct.add(priceOfProduct2)
                                               .plus();

        Product product = createProduct(priceOfProduct);
        productDao.save(product);
        MenuGroup menuGroup = createOfMenuGroup("추천메뉴");
        menuGroupDao.save(menuGroup);
        List<MenuProduct> menuProducts = createOfMenuProduct(product);
        Menu menu = createOfMenu("후라이드+양념", priceOfMenu, menuGroup, menuProducts);

        // exception
        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴의 가격이 메뉴상품의 전체 가격의 합과 같은 경우 성공")
    @Test
    void successfulIfMenuPriceEqualsTotalPriceOfMenuItem() {
        // given
        BigDecimal priceOfProduct = BigDecimal.valueOf(10_000);
        BigDecimal priceOfProduct2 = BigDecimal.valueOf(9_000);
        BigDecimal priceOfMenu = priceOfProduct.add(priceOfProduct2);

        Product product = createProduct(priceOfProduct);
        productDao.save(product);
        MenuGroup menuGroup = createOfMenuGroup("추천메뉴");
        menuGroupDao.save(menuGroup);
        List<MenuProduct> menuProducts = createOfMenuProduct(product);
        Menu menu = createOfMenu("후라이드+양념", priceOfMenu, menuGroup, menuProducts);

        // exception
        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("점주는 등록된 모든 상품을 볼 수 있다")
    @Test
    void list() {
        // given
        String menuOfName2 = "추천메뉴";
        Menu menu1 = createMenu("후라이드 단품");
        Menu menu2 = createMenu(menuOfName2);

        // when
        List<Menu> actual = menuBo.list();

        // then
        assertThat(actual).containsExactlyInAnyOrder(menu1, menu2);
    }

    private Menu createMenu(String menuOfName) {
        Product product = createProduct(BigDecimal.valueOf(10_000));
        productDao.save(product);
        MenuGroup menuGroup = createOfMenuGroup("추천메뉴");
        menuGroupDao.save(menuGroup);
        List<MenuProduct> menuProducts = createOfMenuProduct(product);
        Menu menu = createOfMenu(menuOfName, BigDecimal.valueOf(10_000), menuGroup, menuProducts);
        menuDao.save(menu);
        return menu;
    }

    private Menu createOfMenu(String name, BigDecimal price, MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    private List<MenuProduct> createOfMenuProduct(Product... products) {
        return Arrays.stream(products)
                     .map(product -> createOfMenuProduct(product, 1L))
                     .collect(Collectors.toList());
    }

    private MenuProduct createOfMenuProduct(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    private MenuGroup createOfMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }

    private Product createProduct(BigDecimal price) {
        Product product = new Product();
        product.setName("양념치킨");
        product.setPrice(price);
        return product;
    }
}