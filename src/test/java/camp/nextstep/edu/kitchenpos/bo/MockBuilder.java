package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.model.MenuProduct;
import camp.nextstep.edu.kitchenpos.model.Order;
import camp.nextstep.edu.kitchenpos.model.OrderStatus;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import camp.nextstep.edu.kitchenpos.model.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    public static Order mockCompletedOrder(long id, long tableId){
        return mockOrder(1L, 100L, OrderStatus.COMPLETION);
    }
    public static Order mockMealOrder(long id, long tableId){
        return mockOrder(1L, 100L, OrderStatus.MEAL);
    }
    public static Order mockCookingOrder(long id, long tableId){
        return mockOrder(1L, 100L, OrderStatus.COOKING);
    }

    public static Order mockOrder(long id, long tableId, OrderStatus orderStatus){
        Order order = new Order();
        order.setId(id);
        order.setOrderTableId(tableId);
        order.setOrderStatus(orderStatus.name());
        order.setOrderedTime(LocalDateTime.of(2019, 11,29, 12, 0,0));
        return order;
    }
}
