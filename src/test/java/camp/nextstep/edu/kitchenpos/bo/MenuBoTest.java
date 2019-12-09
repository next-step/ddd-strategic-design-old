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
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

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

    @DisplayName("가격이 존재하지 않을 경우 메뉴를 생성할 수 없다")
    @ParameterizedTest
    @NullSource
    void createFail_noPrice(BigDecimal price) {
        // given
        Menu menu = new Menu();
        menu.setName("메뉴이름1");
        menu.setPrice(price);

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> {
            menuBo.create(menu);
        });
    }

    @DisplayName("가격이 0원 미만일 경우 메뉴를 생성할 수 없다")
    @ParameterizedTest
    @ValueSource(longs = {-100000L, -100L, -1L})
    void createFail_negativePrice(long price) {
        // given
        Menu menu = new Menu();
        menu.setName("메뉴이름1");
        menu.setPrice(BigDecimal.valueOf(price));

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> {
            menuBo.create(menu);
        });
    }

    @DisplayName("메뉴그룹이 존재하지 않으면 메뉴를 생성할 수 없다")
    @Test
    void createFail_noMenuGroup() {
        // given
        Menu menu = new Menu();
        menu.setName("메뉴이름1");
        menu.setPrice(BigDecimal.valueOf(1000));
        menu.setMenuGroupId(0L);
        given(menuGroupDao.existsById(anyLong())).willReturn(false);

        // when
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴의 가격이 메뉴상품들의 가격 합보다 큰 경우 메뉴를 생성할 수 없다")
    @Test
    void createFail_notExistProduct() {
        // given
        Menu menu = new Menu();
        menu.setName("메뉴이름1");
        menu.setPrice(BigDecimal.valueOf(1000));
        menu.setMenuGroupId(0L);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(0L);
        menuProduct.setQuantity(1);

        menu.setMenuProducts(Arrays.asList(menuProduct));

        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(500));

        given(menuGroupDao.existsById(anyLong())).willReturn(true);
        given(productDao.findById(any(Long.class))).willReturn(Optional.of(product));

        // when
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴를 추가할 수 있다")
    @Test
    void createSuccess() {
        // given
        Menu menu = new Menu();
        menu.setName("메뉴이름1");
        menu.setPrice(BigDecimal.valueOf(1000));
        menu.setMenuGroupId(0L);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(0L);
        menuProduct.setQuantity(3);

        menu.setMenuProducts(Arrays.asList(menuProduct));

        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(500));

        given(menuGroupDao.existsById(anyLong())).willReturn(true);
        given(productDao.findById(any(Long.class))).willReturn(Optional.of(product));
        given(menuDao.save(any())).willReturn(menu);
        given(menuProductDao.save(any())).willReturn(menuProduct);


        // when
        Menu result = menuBo.create(menu);

        // then
        assertThat(result).isEqualTo(menu);
    }

    @DisplayName("메뉴 목록을 조회할 수 있다")
    @Test
    void list() {
        // given
        given(menuDao.findAll()).willReturn(Arrays.asList(new Menu(), new Menu()));

        // when
        List<Menu> menus = menuBo.list();

        // then
        assertThat(menus.size()).isEqualTo(2);
    }
}