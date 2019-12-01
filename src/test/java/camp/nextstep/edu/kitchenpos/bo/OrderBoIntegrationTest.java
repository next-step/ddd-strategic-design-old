package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderLineItemDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.Order;
import camp.nextstep.edu.kitchenpos.model.OrderLineItem;
import camp.nextstep.edu.kitchenpos.model.OrderStatus;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import camp.nextstep.edu.kitchenpos.model.TableGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Tag("integrationTest")
@SpringBootTest
@ExtendWith(value = {SpringExtension.class})
class OrderBoIntegrationTest {

    private final OrderDao orderDao;
    private final OrderLineItemDao orderLineItemDao;
    private final OrderTableDao orderTableDao;
    private final TableGroupDao tableGroupDao;
    private final OrderBo bo;

    private Order order;
    private OrderTable orderTable;

    public OrderBoIntegrationTest(@Autowired final OrderDao orderDao,
                                @Autowired final OrderLineItemDao orderLineItemDao,
                                @Autowired final OrderTableDao orderTableDao,
                                @Autowired final TableGroupDao tableGroupDao,
                                @Autowired final OrderBo bo) {
        this.orderDao = orderDao;
        this.orderLineItemDao = orderLineItemDao;
        this.orderTableDao = orderTableDao;
        this.tableGroupDao = tableGroupDao;
        this.bo = bo;
    }

    @BeforeEach
    void setUp() {
        TableGroup tableGroup = new TableGroup();
        tableGroup.setId(1L);
        tableGroup.setCreatedDate(LocalDateTime.now());
        tableGroupDao.save(tableGroup);

        orderTable = orderTableDao.findById(1L).get();
        orderTable.setEmpty(false);
        orderTableDao.save(orderTable);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1L);
        orderLineItem.setQuantity(2);

        order = new Order();
        order.setOrderTableId(1L);
        order.setOrderLineItems(Arrays.asList(orderLineItem, orderLineItem));
    }

    @DisplayName("주문 생성 성공")
    @Test
    void createTest_basic() {
        Order result = bo.create(order);

        assertAll(
            () -> assertThat(result.getOrderStatus())
                    .isEqualTo(OrderStatus.COOKING.name()),

            () -> assertThat(result.getOrderTableId()).isEqualTo(1L)
        );
    }

    @DisplayName("주문 생성 성공:주문한 테이블이 특정 테이블그룹에 속하는 경우")
    @Test
    void createTest_basicWithTableGroup() {
        orderTable.setTableGroupId(1L);
        orderTableDao.save(orderTable);

        Order result = bo.create(order);

        assertAll(
                () -> assertThat(result.getOrderStatus())
                        .isEqualTo(OrderStatus.COOKING.name()),

                () -> assertThat(result.getOrderTableId()).isEqualTo(1L)
        );
    }
}
