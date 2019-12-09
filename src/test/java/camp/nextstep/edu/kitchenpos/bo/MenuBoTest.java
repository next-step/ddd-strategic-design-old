package camp.nextstep.edu.kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import camp.nextstep.edu.kitchenpos.menu.bo.MenuBo;
import camp.nextstep.edu.kitchenpos.menu.domain.Menu;
import camp.nextstep.edu.kitchenpos.menu.domain.MenuDao;
import camp.nextstep.edu.kitchenpos.menu.domain.MenuProduct;
import camp.nextstep.edu.kitchenpos.menu.domain.MenuProductDao;
import camp.nextstep.edu.kitchenpos.menugroup.domain.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.product.domain.Product;
import camp.nextstep.edu.kitchenpos.product.domain.ProductDao;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


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

    private final long DEFAULT_MENU_PRICE = 5000L;

    @Test
    @DisplayName("메뉴를 등록할 수 있다.")
    void add(){

        Menu menu = this.createMenu();
        when(menuGroupDao.existsById(anyLong())).thenReturn(true);
        when(menuDao.save(any())).thenReturn(menu);
        when(productDao.findById(anyLong())).thenReturn(this.createProduct(DEFAULT_MENU_PRICE));


        Menu result = menuBo.create(menu);


        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("메뉴에 등록되는 가격은 0이상이다.")
    void menu_price_zero(){

        Menu menu = this.createMenu(-1);

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));

    }

    @Test
    @DisplayName("메뉴는 그룹에 속해 있어야한다.")
    void menu_group(){

        Menu menu = this.createMenu();
        when(menuGroupDao.existsById(anyLong())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));

    }

    @Test
    @DisplayName("메뉴의 가격은 메뉴상품들의 가격 합산보다 클 수 없다.")
    void menu_price_equals_products_price(){

        long priceOfProduct =  DEFAULT_MENU_PRICE - 2000L;

        Menu menu = this.createMenu();
        when(menuGroupDao.existsById(anyLong())).thenReturn(true);
        when(productDao.findById(anyLong())).thenReturn(this.createProduct(priceOfProduct));


        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    @Test
    @DisplayName("전체 목록을 확인할 수 있다.")
    void menu_list(){

        List<MenuProduct> menuProducts = Arrays.asList(this.createMenuProduct(3));
        when(menuProductDao.findAllByMenuId(anyLong())).thenReturn(menuProducts);
        when(menuDao.findAll()).thenReturn(Arrays.asList(this.createMenu()));


        List<Menu> menus = menuBo.list();


        assertThat(menus.size()).isEqualTo(1);
    }


    private Menu createMenu(){
        return this.createMenu(DEFAULT_MENU_PRICE);
    }

    private Menu createMenu(long price){

        Menu menu = new Menu();
        menu.setId(1L);
        menu.setName("menu");
        menu.setMenuGroupId(1L);
        menu.setPrice(new BigDecimal(price));
        menu.setMenuProducts(Arrays.asList(createMenuProduct(1 )));

        return menu;
    }

    private MenuProduct createMenuProduct(long quantity){

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(1L);
        menuProduct.setQuantity(quantity);

        return menuProduct;
    }

    private Optional<Product> createProduct(long price){

        Product product = new Product();
        product.setId(1L);
        product.setName("product");
        product.setPrice(new BigDecimal(price));

        return Optional.of(product);
    }

}