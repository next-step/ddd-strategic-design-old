package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderLineItemDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.Order;
import camp.nextstep.edu.kitchenpos.model.OrderLineItem;
import camp.nextstep.edu.kitchenpos.model.OrderStatus;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderBoTest {

    @Mock private OrderDao orderDao;
    @Mock private OrderLineItemDao orderLineItemDao;
    @Mock private OrderTableDao orderTableDao;
    @Mock private TableGroupDao tableGroupDao;

    @InjectMocks
    private OrderBo orderBo;

    @DisplayName("새로운 주문을 받을 수 있다")
    @Test
    void create() {
        // given
        final OrderTable orderTable = createOrderTable(1L, false);
        final Order order = createOrder(1L, orderTable, createOrderLineItem());

        given(orderTableDao.findById(order.getOrderTableId())).willReturn(Optional.of(orderTable));
        given(orderDao.save(order)).willReturn(order);

        // when
        final Order actual = orderBo.create(order);

        // then
        assertThat(actual).isNotNull();
    }

    @DisplayName("주문은 주문품목명을 한 개 이상 갖고 있어야 한다")
    @Test
    void orderLineItemsIsNotEmpty() {
        // given
        final Order order = createOrder(1L, createOrderLineItem());

        // when
        // then
        assertThrows(IllegalArgumentException.class,
                () -> orderBo.create(order));
    }

    @DisplayName("주문의 주문테이블이 비어있으면 안된다")
    @Test
    void orderTableIsNotEmpty() {
        // given
        final OrderTable orderTable = createOrderTable(1L, true);
        final Order order = createOrder(1L, orderTable, createOrderLineItem());

        given(orderTableDao.findById(order.getOrderTableId())).willReturn(Optional.of(orderTable));

        // when
        // then
        assertThrows(IllegalArgumentException.class,
                () -> orderBo.create(order));
    }

    @DisplayName("주문의 주문테이블이 테이블묶음이면 가장 작은 주문테이블번호가 주문테이블이다")
    @Test
    void orderTableIdIsMin() {
        // given
        final OrderTable orderTable = createOrderTable(1L, false);
        final Order order = createOrder(1L, orderTable, createOrderLineItem());

        given(orderTableDao.findById(order.getOrderTableId())).willReturn(Optional.of(orderTable));
        given(orderDao.save(order)).willReturn(order);

        // when
        final Order actual = orderBo.create(order);

        // then
        assertThat(actual.getOrderTableId()).isEqualTo(orderTable.getId());
    }

    @DisplayName("새로운 주문 상태는 요리중이 된다")
    @Test
    void newOrderIsCooking() {
        // given
        final OrderTable orderTable = createOrderTable(1L, false);
        final Order order = createOrder(1L, orderTable, createOrderLineItem());

        given(orderTableDao.findById(order.getOrderTableId())).willReturn(Optional.of(orderTable));
        given(orderDao.save(order)).willReturn(order);

        // when
        final Order actual = orderBo.create(order);

        // then
        assertThat(actual.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
    }

    @DisplayName("모든 주문을 조회할 수 있다")
    @Test
    void list() {
        // given
        final List<Order> orders = Arrays.asList(
                createOrder(1L, createOrderLineItem()),
                createOrder(2L, createOrderLineItem()),
                createOrder(3L, createOrderLineItem()));

        given(orderDao.findAll()).willReturn(orders);

        // when
        final List<Order> actual = orderBo.list();

        // then
        assertThat(actual).hasSize(3);
    }

    @DisplayName("주문상태를 변경할 수 있다")
    @Test
    void changeOrderStatus() {
        // given
        final Order order = createOrder(1L, OrderStatus.COOKING, createOrderLineItem(), createOrderLineItem());

        given(orderDao.findById(order.getId())).willReturn(Optional.of(order));
        given(orderLineItemDao.findAllByOrderId(order.getId())).willReturn(order.getOrderLineItems());

        // when
        order.setOrderStatus(OrderStatus.MEAL.name());
        final Order actual = orderBo.changeOrderStatus(order.getId(), order);

        // then
        assertThat(actual.getOrderStatus()).isEqualTo(OrderStatus.MEAL.name());
    }

    @DisplayName("주문상태가 완료면 상태를 바꿀 수 없다")
    @Test
    void cannotChangeEqualOrderStatus() {
        // given
        final Order order = createOrder(1L, OrderStatus.COOKING, createOrderLineItem(), createOrderLineItem());

        given(orderDao.findById(order.getId())).willReturn(Optional.of(order));

        // when
        order.setOrderStatus(OrderStatus.COMPLETION.name());

        // then
        assertThrows(IllegalArgumentException.class,
                () -> orderBo.changeOrderStatus(order.getId(), order));
    }

    private Order createOrder(final Long id, final OrderLineItem... orderLineItems) {
        final Order order = new Order();
        order.setId(id);
        order.setOrderLineItems(Arrays.asList(orderLineItems));
        return order;
    }

    private Order createOrder(final Long id, final OrderTable orderTable, final OrderLineItem... orderLineItems) {
        final Order order = createOrder(id, orderLineItems);
        order.setOrderTableId(orderTable.getTableGroupId());
        return order;
    }

    private Order createOrder(final Long id, final OrderStatus orderStatus, final OrderLineItem... orderLineItems) {
        final Order order = createOrder(id, orderLineItems);
        order.setOrderStatus(orderStatus.name());
        return order;
    }

    private OrderLineItem createOrderLineItem() {
        final OrderLineItem orderLineItem = new OrderLineItem();
        return orderLineItem;
    }

    private OrderTable createOrderTable(final Long id, final boolean isEmpty) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setEmpty(isEmpty);
        return orderTable;
    }
}