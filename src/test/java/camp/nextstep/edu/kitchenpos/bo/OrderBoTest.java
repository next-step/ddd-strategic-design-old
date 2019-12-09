package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderLineItemDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderBoTest {

    @Mock
    private Order order;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderLineItemDao orderLineItemDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @InjectMocks
    private OrderBo orderBo;

    @DisplayName("주문품목 리스트가 비어있다면 주문을 할 수 없다")
    @Test
    void createFail_orderLineItemsEmpty() {
        // given
        Order order = new Order();
        order.setOrderLineItems(Arrays.asList());
        order.setOrderTableId(0L);

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> {
            orderBo.create(order);
        });
    }

    @DisplayName("주문에 지정된 주문테이블이 존재하지 않는 경우 주문을 생성할 수 없다")
    @Test
    void createFail_orderTableNotExist() {
        // given
        Order order = new Order();
        order.setOrderTableId(0L);

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> {
            orderBo.create(order);
        });
    }

    @DisplayName("주문테이블이 비어있으면 주문을 생성할 수 없다")
    @Test
    void createFail_tableGroupNotExist() {
        // given
        List<OrderLineItem> orderLineItems = Arrays.asList(new OrderLineItem(), new OrderLineItem());

        Order order = new Order();
        order.setOrderTableId(0L);
        order.setOrderLineItems(orderLineItems);

        OrderTable orderTable = new OrderTable();
        orderTable.setId(0L);
        orderTable.setTableGroupId(0L);
        orderTable.setEmpty(true);

        //when
        assertThatIllegalArgumentException().isThrownBy(() -> {
            orderBo.create(order);
        });
    }

    @DisplayName("주문테이블이 테이블그룹에 속해있을때 번호가 빠른 테이블을 주문테이블로 지정한다")
    @Test
    void firstTableOfTableGroup_asOrderTable() {
        // given
        List<OrderLineItem> orderLineItems = Arrays.asList(new OrderLineItem(), new OrderLineItem());

        Order order = new Order();
        order.setOrderTableId(0L);
        order.setOrderLineItems(orderLineItems);

        OrderTable orderTable = new OrderTable();
        orderTable.setId(0L);
        orderTable.setTableGroupId(1L);
        orderTable.setEmpty(false);

        OrderTable orderTable2 = new OrderTable();
        orderTable2.setId(1L);
        orderTable2.setTableGroupId(1L);
        orderTable2.setEmpty(false);

        List<OrderTable> orderTables = Arrays.asList(orderTable, orderTable2);

        TableGroup tableGroup = new TableGroup();
        tableGroup.setId(1L);
        tableGroup.setOrderTables(orderTables);

        given(orderTableDao.findById(anyLong())).willReturn(Optional.of(orderTable));
        given(tableGroupDao.findById(anyLong())).willReturn(Optional.of(tableGroup));
        given(orderTableDao.findAllByTableGroupId(anyLong())).willReturn(orderTables);
        given(orderDao.save(any(Order.class))).willReturn(order);

        // when
        Order result = orderBo.create(order);

        // then
        assertThat(result.getOrderTableId()).isEqualTo(0L);
    }

    @DisplayName("주문상태가 완료이면 상태를 변경할 수 없다")
    @Test
    void changeStatusFail_whenStatusCompletion() {
        // given
        given(orderDao.findById(anyLong())).willReturn(Optional.of(order));
        given(order.getOrderStatus()).willReturn(String.valueOf(OrderStatus.COMPLETION));

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> {
            orderBo.changeOrderStatus(0L, order);
        });
    }
}