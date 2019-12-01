package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.MenuDao;
import camp.nextstep.edu.kitchenpos.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.dao.MenuProductDao;
import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Menu;
import camp.nextstep.edu.kitchenpos.model.MenuProduct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("integrationTest")
@SpringBootTest
@ExtendWith(value = {SpringExtension.class})
class MenuBoIntegrationTest {

    private final MenuDao menuDao;
    private final MenuGroupDao menuGroupDao;
    private final MenuProductDao menuProductDao;
    private final ProductDao productDao;
    private final MenuBo menuBo;

    private Menu menu;

    public MenuBoIntegrationTest(@Autowired MenuDao menuDao,
                                @Autowired MenuGroupDao menuGroupDao,
                                @Autowired MenuProductDao menuProductDao,
                                @Autowired ProductDao productDao,
                                @Autowired MenuBo menuBo) {
        this.menuDao = menuDao;
        this.menuGroupDao = menuGroupDao;
        this.menuProductDao = menuProductDao;
        this.productDao = productDao;
        this.menuBo = menuBo;
    }

    @BeforeEach
    void setUp() {
        menu = new Menu();
        menu.setName("테스트 메뉴");
        menu.setMenuGroupId(1L);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(1L);
        menuProduct.setQuantity(1L);

        menu.setMenuProducts(Arrays.asList(menuProduct, menuProduct));
    }

    @DisplayName("구성 상품의 가격과 수량의 곱을 모두 더한 값보다 메뉴 가격이 크면 메뉴를 생성할 수 없다")
    @Test
    void createTest_priceSum() {
        menu.setPrice(BigDecimal.valueOf(Long.MAX_VALUE));

        assertThatThrownBy(() -> menuBo.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성 성공")
    @Test
    void name() {
        menu.setPrice(BigDecimal.ZERO);

        Menu resultMenu = menuBo.create(menu);
        menu.setId(resultMenu.getId());

        Assertions.assertAll(
            () -> assertThat(resultMenu)
                    .isEqualToIgnoringGivenFields(menu,
                            "price", "menuProducts"),

            () -> assertThat(resultMenu.getPrice())
                    .isEqualByComparingTo(menu.getPrice())
        );
    }

}
