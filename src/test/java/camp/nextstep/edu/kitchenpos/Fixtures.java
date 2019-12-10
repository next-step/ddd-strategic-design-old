package camp.nextstep.edu.kitchenpos;

import camp.nextstep.edu.kitchenpos.menu.model.menu.Menu;
import camp.nextstep.edu.kitchenpos.menu.model.menugroup.MenuGroup;
import camp.nextstep.edu.kitchenpos.menu.model.menu.MenuProduct;
import camp.nextstep.edu.kitchenpos.product.model.Product;

import java.math.BigDecimal;
import java.util.Arrays;

public class Fixtures {
    public static Menu menu() {
        final Menu menu = new Menu();
        menu.setId(1L);
        menu.setName("후라이드+후라이드");
        menu.setPrice(BigDecimal.valueOf(19000L));
        menu.setMenuGroupId(1L);
        menu.setMenuProducts(Arrays.asList(menuProduct()));
        return menu;
    }

    public static MenuGroup menuGroup() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroup.setName("두마리메뉴");
        return menuGroup;
    }

    public static Product friedChicken() {
        final Product product = new Product();
        product.setId(1L);
        product.setName("후라이드");
        product.setPrice(BigDecimal.valueOf(16000L));
        return product;
    }

    public static Product seasonedChicken() {
        final Product product = new Product();
        product.setId(2L);
        product.setName("양념치킨");
        product.setPrice(BigDecimal.valueOf(16000L));
        return product;
    }

    private static MenuProduct menuProduct() {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setMenuId(1L);
        menuProduct.setProductId(1L);
        menuProduct.setQuantity(2);
        return menuProduct;
    }
}
