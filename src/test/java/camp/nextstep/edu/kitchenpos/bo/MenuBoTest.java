package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.MenuDao;
import camp.nextstep.edu.kitchenpos.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.dao.MenuProductDao;
import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Menu;
import camp.nextstep.edu.kitchenpos.model.MenuGroup;
import camp.nextstep.edu.kitchenpos.model.MenuProduct;
import camp.nextstep.edu.kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuBoTest {
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

    private Product product;
    private MenuProduct menuProduct;
    private MenuGroup menuGroup;
    private Menu menu;
    private List<Menu> menus;

    @BeforeEach
    void dummyMenuSetUp() {
        /*
        {
            "name": "후라이드+후라이드",
            "price": 19000,
            "menuGroupId": 1,
            "menuProducts": [
                {
                    "productId": 1,
                    "quantity": 2
                }
            ]
        }
        */
        product = new Product();
        product.setId(1L);
        product.setName("후라이드");
        product.setPrice(BigDecimal.valueOf(16000));

        menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroup.setName("두마리메뉴");

        menuProduct = new MenuProduct();
        menuProduct.setMenuId(1L);
        menuProduct.setProductId(1L);
        menuProduct.setQuantity(2);

        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(menuProduct);

        menu = new Menu();
        menu.setName("후라이드+후라이드");
        menu.setPrice(BigDecimal.valueOf(16000));
        menu.setMenuGroupId(1L);
        menu.setMenuProducts(menuProducts);

        menus = new ArrayList<>();
        menus.add(menu);
    }


    @DisplayName("메뉴그룹이 없으면 메뉴를 생성할 수 없다")
    @Test
    void canNotCreateMenuWhenEmptyMenuGroup() {
        given(menuGroupDao.existsById(anyLong()))
                .willReturn(false);

        assertThatThrownBy(() -> menuBo.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴그룹에 메뉴제품이 없으면 메뉴를 생성할 수 없다. ")
    @Test
    void canNotCreateMenuWhenNotEmptyMenuGroupEmptyMenuProduct() {
        given(menuGroupDao.existsById(anyLong()))
                .willReturn(true);
        given(productDao.findById(anyLong()))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> menuBo.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴제품이 포함된 메뉴그룹을 1개 가지고 있는 메뉴를 생성하기")
    @Test
    void createMenuHasOneMenuGroupIncludedMenuProduct() {
        given(menuGroupDao.existsById(anyLong())).willReturn(true);
        given(productDao.findById(anyLong())).willReturn(Optional.of(product));
        given(menuDao.save(menu)).willReturn(menu);
        given(menuProductDao.save(menuProduct)).willReturn(menuProduct);

        Menu savedMenu = menuBo.create(menu);

        assertThat(savedMenu.getId()).isEqualTo(menu.getId());
        assertThat(savedMenu.getPrice()).isEqualTo(menu.getPrice());
        assertThat(savedMenu.getName()).isEqualTo(menu.getName());
        assertThat(savedMenu.getMenuGroupId()).isEqualTo(menuGroup.getId());
    }

    @DisplayName("메뉴 리스트 조회하기")
    @Test
    void menuListTest() {
        given(menuDao.findAll()).willReturn(menus);

        List<Menu> list = menuBo.list();

        assertThat(list).hasSize(1);
    }
}
