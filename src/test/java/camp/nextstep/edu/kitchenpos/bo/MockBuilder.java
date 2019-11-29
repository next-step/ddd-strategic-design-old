package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.model.MenuProduct;
import camp.nextstep.edu.kitchenpos.model.Product;
import java.math.BigDecimal;

public class MockBuilder {
    public static Product mockValidProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("테스트");
        product.setPrice(BigDecimal.valueOf(100L));
        return product;
    }
    public static MenuProduct mockValidMenuProduct(Long menuId) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(0L);
        menuProduct.setMenuId(menuId);
        menuProduct.setProductId(20L);
        menuProduct.setQuantity(2L);
        return menuProduct;
    }


}
