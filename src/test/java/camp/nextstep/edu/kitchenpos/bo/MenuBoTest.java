package camp.nextstep.edu.kitchenpos.bo;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import camp.nextstep.edu.kitchenpos.dao.*;
import camp.nextstep.edu.kitchenpos.model.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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


    @Test
    @DisplayName("Menu를 등록할 수 있다.")
    void add(){

        Menu menu = this.createMenu();

        when(menuGroupDao.existsById(anyLong())).thenReturn(true);
        when(menuDao.save(any())).thenReturn(menu);
        when(productDao.findById(anyLong())).thenReturn(this.createProduct(5000L));

        Menu result = menuBo.create(menu);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Menu에 등록되는 가격은 0이상이다.")
    void menu_price_zero(){

        Menu menu = this.createMenu(1L, -1);

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));

    }

    @Test
    @DisplayName("Menu는 Group에 속해 있어야한다.")
    void menu_group(){

        Menu menu = this.createMenu();
        when(menuGroupDao.existsById(anyLong())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));

    }

    @Test
    @DisplayName("Menu의 가격은 Menu Product들의 가격 합산보다 클 수 없다.")
    void menu_price_equals_products_price(){

        Menu menu = this.createMenu();

        when(menuGroupDao.existsById(anyLong())).thenReturn(true);
        when(productDao.findById(anyLong())).thenReturn(this.createProduct(3000L));

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }


    private Menu createMenu(){
        return this.createMenu(1L, 5000L);
    }

    private Menu createMenu(long menuGroupId, long price){

        Menu menu = new Menu();
        menu.setName("menu");
        menu.setMenuGroupId(menuGroupId);
        menu.setPrice(new BigDecimal(price));
        menu.setMenuGroupId(menuGroupId);
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