package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.dao.MenuProductDao;
import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Menu;
import camp.nextstep.edu.kitchenpos.model.MenuGroup;
import camp.nextstep.edu.kitchenpos.model.MenuProduct;
import camp.nextstep.edu.kitchenpos.model.Product;
import camp.nextstep.edu.kitchenpos.support.dao.InMemoryMenuDao;
import camp.nextstep.edu.kitchenpos.support.dao.InMemoryMenuGroupDao;
import camp.nextstep.edu.kitchenpos.support.dao.InMemoryMenuProductDao;
import camp.nextstep.edu.kitchenpos.support.dao.InMemoryProductDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.LongStream;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("`메뉴`는 `고객`이 `매장`에게 `주문`할 수 있는 대상을 뜻한다.")
class MenuBoTest {

    private MenuGroupDao menuGroupDao;
    private MenuProductDao menuProductDao;
    private ProductDao productDao;

    private MenuBo menuBo;

    @BeforeEach
    void setUp() {
        menuGroupDao = new InMemoryMenuGroupDao();
        menuProductDao = new InMemoryMenuProductDao();
        productDao = new InMemoryProductDao();


        menuBo = new MenuBo(new InMemoryMenuDao(), menuGroupDao, menuProductDao, productDao);
    }

    @DisplayName("`메뉴`의 가격이 빈 값 이라면 예외처리 한다.")
    @Test
    void create_lowerPrice() {
        // given
        final Menu menu = new Menu();

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("`메뉴`의 가격이 0 원 이상이 아니라면 예외처리 한다.")
    @ValueSource(strings = {"-12312421", "-1", "0"})
    @ParameterizedTest
    void create_lowerPrice(final String price) {
        // given
        final Menu menu = new Menu();
        menu.setPrice(new BigDecimal(price));

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("`메뉴`가 메뉴 그룹의 식별자를 가지지 않는다면 예외처리 한다.")
    @Test
    void create_nullMenuGroupId() {
        // given
        final Menu menu = new Menu();
        menu.setPrice(new BigDecimal("1000"));

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("`메뉴`가 미리 등록된 메뉴 그룹을 가지지 않는다면 예외처리 한다.")
    @Test
    void create_notExistsMenuGroup() {
        // given
        final Menu menu = new Menu();
        menu.setPrice(new BigDecimal("1000"));
        menu.setMenuGroupId(1L);

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("`상품 메뉴`가 존재하지 않는 `메뉴`를 등록할 수 있다.")
    @Test
    void create_emptyMenuGroup() {
        // given
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroupDao.save(menuGroup);

        final Menu menu = new Menu();
        menu.setPrice(new BigDecimal("0"));
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(emptyList());

        // when
        final Menu savedMenu = menuBo.create(menu);

        // then
        assertThat(savedMenu).isEqualTo(menu);
    }

    @DisplayName("`메뉴`를 등록 시 `메뉴 상품`들의 금액 총합이 메뉴의 금액보다 크면 예외처리 한다.")
    @Test
    void create_menuPriceLessThanMenuProductSum_single() {
        // given
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroupDao.save(menuGroup);

        final Product product = new Product();
        product.setId(1L);
        product.setPrice(new BigDecimal("1001"));

        productDao.save(product);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(1L);
        menuProduct.setQuantity(1);
        menuProduct.setProductId(product.getId());

        final Menu menu = new Menu();
        menu.setPrice(new BigDecimal("1000"));
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(List.of(menuProduct));

        // when
        final Menu savedMenu = menuBo.create(menu);

        // then
        assertThat(savedMenu).isEqualTo(menu);
    }

    @DisplayName("`메뉴`를 등록 시 `메뉴 상품`들의 금액 총합이 메뉴의 금액보다 크면 예외처리 한다.")
    @Test
    void create_menuPriceLessThanMenuProductSum_multiProduct() {
        // given
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroupDao.save(menuGroup);

        final Product product1 = new Product();
        product1.setId(1L);
        product1.setPrice(new BigDecimal("500"));
        productDao.save(product1);

        MenuProduct menuProduct1 = new MenuProduct();
        menuProduct1.setSeq(1L);
        menuProduct1.setQuantity(1);
        menuProduct1.setProductId(product1.getId());

        final Product product2 = new Product();
        product2.setId(2L);
        product2.setPrice(new BigDecimal("501"));
        productDao.save(product2);

        MenuProduct menuProduct2 = new MenuProduct();
        menuProduct2.setSeq(2L);
        menuProduct2.setQuantity(1);
        menuProduct2.setProductId(product2.getId());

        final Menu menu = new Menu();
        menu.setPrice(new BigDecimal("1000"));
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(List.of(menuProduct1, menuProduct2));

        // when
        final Menu savedMenu = menuBo.create(menu);

        // then
        assertThat(savedMenu).isEqualTo(menu);
    }

    @DisplayName("`메뉴`를 등록 시 `메뉴 상품`들의 금액 총합이 메뉴의 금액보다 크면 예외처리 한다.")
    @Test
    void create_menuPriceLessThanMenuProductSum_multiQuantity() {
        // given
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroupDao.save(menuGroup);

        final Product product = new Product();
        product.setId(1L);
        product.setPrice(new BigDecimal("501"));

        productDao.save(product);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(1L);
        menuProduct.setQuantity(2);
        menuProduct.setProductId(product.getId());

        final Menu menu = new Menu();
        menu.setPrice(new BigDecimal("1000"));
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(List.of(menuProduct));

        // when
        final Menu savedMenu = menuBo.create(menu);

        // then
        assertThat(savedMenu).isEqualTo(menu);
    }

    @DisplayName("가격이 있는 `메뉴`를 등록할 수 있다.")
    @Test
    void create() {
        // given
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroupDao.save(menuGroup);

        final Product product = new Product();
        product.setId(1L);
        product.setPrice(new BigDecimal("1000"));

        productDao.save(product);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(1L);
        menuProduct.setQuantity(1);
        menuProduct.setProductId(product.getId());

        final Menu menu = new Menu();
        menu.setPrice(new BigDecimal("1000"));
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(List.of(menuProduct));

        // when
        final Menu savedMenu = menuBo.create(menu);

        // then
        assertThat(savedMenu).isEqualTo(menu);
    }

    @DisplayName("`메뉴` 등록 시 `메뉴 상품`들도 같이 등록된다.")
    @Test
    void create_withMenuProduct() {
        // given
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroupDao.save(menuGroup);

        final Product product = new Product();
        product.setId(1L);
        product.setPrice(new BigDecimal("1000"));

        productDao.save(product);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(1L);
        menuProduct.setQuantity(1);
        menuProduct.setProductId(product.getId());

        final Menu menu = new Menu();
        menu.setPrice(new BigDecimal("1000"));
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(List.of(menuProduct));

        menuBo.create(menu);

        // when
        final MenuProduct savedMenuProduct = menuProductDao.findById(menuProduct.getSeq())
                .orElseThrow();

        // then
        assertThat(savedMenuProduct).isEqualTo(menuProduct);
    }

    @DisplayName("`메뉴` 조회 시 등록된 `메뉴`가 없다면 빈 리스트를 반환한다.")
    @Test
    void list_empty() {
        // when
        final List<Menu> menus = menuBo.list();

        // then
        assertThat(menus).isEmpty();
    }

    @DisplayName("`메뉴`을 하나 등록 후 조회 시 등록된 `메뉴` 하나를 반환한다.")
    @Test
    void list_single() {
        // given
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroupDao.save(menuGroup);

        final Product product = new Product();
        product.setId(1L);
        product.setPrice(new BigDecimal("1000"));

        productDao.save(product);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(1L);
        menuProduct.setQuantity(1);
        menuProduct.setProductId(product.getId());

        final Menu menu = new Menu();
        menu.setPrice(new BigDecimal("1000"));
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(List.of(menuProduct));

        menuBo.create(menu);

        // when
        final List<Menu> menus = menuBo.list();

        // then
        assertThat(menus).containsExactly(menu);
    }

    @DisplayName("`메뉴` 조회 시 등록된 `메뉴`의 갯수 만큼 반환한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 3, 100, 234})
    void list_many(final int size) {
        // given
        LongStream.rangeClosed(1, size)
                .mapToObj(id -> {
                    final MenuGroup menuGroup = new MenuGroup();
                    menuGroup.setId(id);
                    menuGroupDao.save(menuGroup);

                    final Product product = new Product();
                    product.setId(id);
                    product.setPrice(new BigDecimal("1000"));

                    productDao.save(product);

                    MenuProduct menuProduct = new MenuProduct();
                    menuProduct.setSeq(id);
                    menuProduct.setQuantity(1);
                    menuProduct.setProductId(product.getId());

                    final Menu menu = new Menu();
                    menu.setPrice(new BigDecimal("1000"));
                    menu.setMenuGroupId(menuGroup.getId());
                    menu.setMenuProducts(List.of(menuProduct));

                    return menu;
                })
                .forEach(menuBo::create);

        // when
        final List<Menu> menus = menuBo.list();

        // then
        assertThat(menus).hasSize(size);
    }
}