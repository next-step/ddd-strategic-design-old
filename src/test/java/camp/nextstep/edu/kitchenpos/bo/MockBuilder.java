package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.model.MenuProduct;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
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
    public static MenuProduct mockValidMenuProduct(long menuId) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(0L);
        menuProduct.setMenuId(menuId);
        menuProduct.setProductId(20L);
        menuProduct.setQuantity(2L);
        return menuProduct;
    }


    public static OrderTable mockNotEmptyOrderTable(long id) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setNumberOfGuests(3);
        orderTable.setEmpty(false);
        return orderTable;
    }

    public static OrderTable mockEmptyOrderTable(long id) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setNumberOfGuests(0);
        orderTable.setEmpty(true);
        return orderTable;
    }

}
