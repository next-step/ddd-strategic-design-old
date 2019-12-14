package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderLineItemDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static camp.nextstep.edu.kitchenpos.model.OrderStatus.COOKING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderBoTest {

    @InjectMocks
    private OrderBo orderBo;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderLineItemDao orderLineItemDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @DisplayName("주문을 등록 할 수 있다")
    @Test
    void create() {
        // given
        long menuId = 1L;
        long orderTableId = 1L;
        long tableGroupId = 2L;

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setQuantity(4);

        OrderTable orderTable = new OrderTable();
        orderTable.setId(orderTableId);
        orderTable.setTableGroupId(tableGroupId);

        Order order = new Order();
        order.setId(1L);
        order.setOrderTableId(tableGroupId);
        order.setOrderLineItems(Arrays.asList(orderLineItem));

        TableGroup tableGroup = new TableGroup();
        tableGroup.setId(2L);
        List<OrderTable> orderTables = Arrays.asList(orderTable);
        given(orderTableDao.findById(any())).willReturn(Optional.of(orderTable));
        given(tableGroupDao.findById(any())).willReturn(Optional.of(tableGroup));
        given(orderDao.save(any())).willReturn(order);
        given(orderLineItemDao.save(any())).willReturn(orderLineItem);
        given(orderTableDao.findAllByTableGroupId(any())).willReturn(orderTables);

        // when
        Order actual = orderBo.create(order);

        // then
        assertAll(
                () -> assertThat(actual.getOrderTableId()).isEqualTo(orderTableId),
                () -> assertThat(actual.getOrderStatus()).isEqualTo(COOKING.toString()),
                () -> assertThat(actual.getOrderLineItems()).extracting(OrderLineItem::getMenuId)
                                                            .containsExactly(menuId),
                () -> assertThat(actual.getOrderedTime()).isNotNull()
        );
    }

    @DisplayName("주문항목이 없을 시 주문 등록을 할 수 없다")
    @Test
    void createWhenOrderLineItemIsNull_exception() {
        // given
        Order order = new Order();
        order.setOrderLineItems(new ArrayList<>());

        // exception
        Assertions.assertThatIllegalArgumentException()
                  .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("주문 테이블의 정보가 없을 경우 주문 등록을 할 수 없다")
    @Test
    void createWhenNonOrderTable_exception() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1L);
        orderLineItem.setQuantity(4);

        Order order = new Order();
        order.setOrderLineItems(Arrays.asList(orderLineItem));
        order.setOrderTableId(1L);

        given(orderTableDao.findById(order.getOrderTableId())).willReturn(Optional.empty());

        // exception
        Assertions.assertThatIllegalArgumentException()
                  .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("주문한 테이블이 빈 테이블인 경우 주문 등록을 할 수 없다")
    @Test
    void createWhenOrderTableEmpty_exception() {
        // given
        OrderTable emptyOrderTable = new OrderTable();
        emptyOrderTable.setId(1L);
        emptyOrderTable.setEmpty(true);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1L);
        orderLineItem.setQuantity(4);

        Order order = new Order();
        order.setOrderLineItems(Arrays.asList(orderLineItem));
        order.setOrderTableId(null);

        given(orderTableDao.findById(order.getOrderTableId())).willReturn(Optional.of(emptyOrderTable));

        // exception
        Assertions.assertThatIllegalArgumentException()
                  .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("주문한 단체 테이블이 그룹 테이블로 등록이 되지 않은 경우 주문 등록을 할 수 없다")
    @Test
    void createWhenNonTableGroup_exception() {
        // given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1l);
        orderLineItem.setQuantity(4);

        Order order = new Order();
        order.setOrderLineItems(Arrays.asList(orderLineItem));
        order.setOrderTableId(null);

        OrderTable groupOrderTable = new OrderTable();
        groupOrderTable.setId(1l);
        groupOrderTable.setTableGroupId(2l);

        given(orderTableDao.findById(order.getOrderTableId())).willReturn(Optional.of(groupOrderTable));
        given(tableGroupDao.findById(any())).willReturn(Optional.empty());

        // exception
        Assertions.assertThatIllegalArgumentException()
                  .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("점주는 주문내역을 모두 볼 수 있다.")
    @Test
    void list() {
        // given
        Order order = new Order();
        order.setId(1L);
        order.setOrderTableId(1L);

        Order order2 = new Order();
        order.setId(2L);
        order.setOrderTableId(2L);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setOrderId(1L);
        orderLineItem.setMenuId(1L);

        OrderLineItem orderLineItem2 = new OrderLineItem();
        orderLineItem.setOrderId(2L);
        orderLineItem.setMenuId(2L);

        List<Order> orders = Arrays.asList(order, order2);
        given(orderDao.findAll()).willReturn(orders);
        given(orderLineItemDao.findAllByOrderId(any())).willReturn(Arrays.asList(orderLineItem, orderLineItem2));

        // when
        List<Order> actual = orderBo.list();

        // then
        assertThat(actual).containsExactlyInAnyOrderElementsOf(orders);
    }

    @DisplayName("주문상태를 요리중으로 변경한다")
    @Test
    void changeOrderStatus() {
        // given
        long orderId = 1L;
        String expectedOrderStatus = OrderStatus.COOKING.name();
        Order order = new Order();
        order.setId(orderId);
        order.setOrderStatus(expectedOrderStatus);

        given(orderDao.findById(any())).willReturn(Optional.of(order));

        // when
        Order actual = orderBo.changeOrderStatus(orderId, order);

        // then
        assertThat(actual.getId()).isEqualTo(orderId);
        assertThat(actual.getOrderStatus()).isEqualTo(expectedOrderStatus);
    }

    @DisplayName("등록된 주문이 아닐 시 주문상태를 변경 할 수 없다")
    @Test
    void changeOrderStatusWhenNonOrderId_exception() {
        // given
        long nonOrderId = 999999L;
        Order order = new Order();
        order.setId(nonOrderId);

        // exception
        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderBo.changeOrderStatus(nonOrderId, order));
    }

    @DisplayName("완료된 주문은 변경 할 수 없다")
    @Test
    void changeOrderStatusWhenCompletedOrder_exception() {
        // given
        long nonOrderId = 999999L;
        Order order = new Order();
        order.setId(nonOrderId);
        order.setOrderStatus(OrderStatus.COMPLETION.name());

        given(orderDao.findById(any())).willReturn(Optional.of(order));

        // exception
        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderBo.changeOrderStatus(nonOrderId, order));
    }
}