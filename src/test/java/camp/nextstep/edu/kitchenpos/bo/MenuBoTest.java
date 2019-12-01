package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.MenuDao;
import camp.nextstep.edu.kitchenpos.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.dao.MenuProductDao;
import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Menu;
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
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class MenuBoTest {

    @Mock
    private Menu mockMenu;

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
    private MenuBo bo;

    @BeforeEach
    void setUp() {
        menu = new Menu();
        menu.setPrice(BigDecimal.TEN);
        menu.setMenuGroupId(10L);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(2);
        menu.setMenuProducts(Arrays.asList(menuProduct, menuProduct));
    }

    @DisplayName("생성하려는 메뉴의 가격 정보가 없으면 메뉴를 생성할 수 없다")
    @Test
    void createTest_withNullPrice() {
        when(mockMenu.getPrice()).thenReturn(null);

        assertThatThrownBy(() -> bo.create(mockMenu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("생성하려는 메뉴의 가격이 음수면 메뉴를 생성할 수 없다")
    @Test
    void createTest_withNegativePrice() {
        when(mockMenu.getPrice()).thenReturn(new BigDecimal(-1));

        assertThatThrownBy(() -> bo.create(mockMenu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("생성하려는 메뉴가 속한 메뉴그룹이 이미 등록돼 있지 않다면 메뉴를 생성할 수 없다")
    @Test
    void createTest_nonExistsMenuGroup() {
        when(menuGroupDao.existsById(anyLong())).thenReturn(false);

        assertThatThrownBy(() -> bo.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("생성하려는 메뉴의 구성 상품이 이미 등록돼 있지 않다면 메뉴를 생성할 수 없다")
    @Test
    void createTest_nonExistsProductId() {
        when(menuGroupDao.existsById(anyLong())).thenReturn(true);
        when(productDao.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bo.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구성 상품의 가격과 수량의 곱을 모두 더한 값보다 메뉴 가격이 크면 메뉴를 생성할 수 없다")
    @Test
    void createTest_priceSum() {
        when(menuGroupDao.existsById(anyLong())).thenReturn(true);

        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(0L));
        when(productDao.findById(any())).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> bo.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성 성공")
    @Test
    void createTest_basic() {
        when(menuGroupDao.existsById(anyLong())).thenReturn(true);

        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(1000L));
        when(productDao.findById(any())).thenReturn(Optional.of(product));

        Menu savedMenu = new Menu();
        when(menuDao.save(menu)).thenReturn(savedMenu);

        when(menuProductDao.save(any(MenuProduct.class))).thenReturn(new MenuProduct());

        assertThat(bo.create(menu)).isEqualTo(savedMenu);
    }
}
