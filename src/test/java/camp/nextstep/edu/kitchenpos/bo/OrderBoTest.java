package camp.nextstep.edu.kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderLineItemDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.Order;
import camp.nextstep.edu.kitchenpos.model.OrderLineItem;
import camp.nextstep.edu.kitchenpos.model.OrderStatus;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import camp.nextstep.edu.kitchenpos.model.TableGroup;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderBoTest {

    private static final long ORDER_ID = 1L;
    private static final long ORDER_TABLE_ID = 1L;

    @Mock
    private Order order;

    @Mock
    private OrderTable orderTable;

    @Mock
    private OrderLineItem orderLineItem;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @Mock
    private OrderLineItemDao orderLineItemDao;

    @InjectMocks
    private OrderBo orderBo;

    @DisplayName("주문 생성중 주문품목이 비어있으면 예외를 발생한다.")
    @Test
    void create_isEmptyOrderLineItems() {
        // Given
        given(order.getOrderLineItems()).willReturn(Collections.emptyList());

        // When
        // Then
        assertThatThrownBy(() -> orderBo.create(order)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 생성중 주문테이블이 비어있으면 예외를 발생한다.")
    @Test
    void create_isEmptyOrderTable() {
        // Given
        given(order.getOrderLineItems()).willReturn(Arrays.asList(orderLineItem));
        given(order.getOrderTableId()).willReturn(ORDER_TABLE_ID);
        given(orderTableDao.findById(ORDER_TABLE_ID)).willReturn(Optional.of(orderTable));
        given(orderTable.isEmpty()).willReturn(true);

        // When
        // Then
        assertThatThrownBy(() -> orderBo.create(order)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 생성중 주문테이블에 테이블그룹이 있을 경우 주문테이블ID가 제일 작은 주문테이블을 주문에 지정한다.")
    @Test
    void create_orderTableIsFirstId() {
        // Given
        final long tableGroupId = 10L;
        final TableGroup tableGroup = new TableGroup();
        tableGroup.setId(tableGroupId);

        final long firstOrderTableId = 21L;
        final OrderTable firstOrderTable = new OrderTable();
        firstOrderTable.setId(firstOrderTableId);
        firstOrderTable.setTableGroupId(tableGroupId);
        firstOrderTable.setEmpty(false);

        final long secondOrderTableId = 22L;
        final OrderTable secondOrderTable = new OrderTable();
        secondOrderTable.setId(secondOrderTableId);
        secondOrderTable.setTableGroupId(tableGroupId);
        secondOrderTable.setEmpty(false);

        final long orderId = 30L;
        final Order order = new Order();
        order.setId(orderId);
        order.setOrderLineItems(Arrays.asList(orderLineItem));
        order.setOrderTableId(secondOrderTableId);

        given(orderTableDao.findById(secondOrderTableId)).willReturn(Optional.of(secondOrderTable));
        given(tableGroupDao.findById(tableGroupId)).willReturn(Optional.of(tableGroup));
        given(orderTableDao.findAllByTableGroupId(tableGroupId))
                .willReturn(Arrays.asList(firstOrderTable, secondOrderTable));
        given(orderDao.save(order)).willReturn(order);

        // When
        final Order saveOrder = orderBo.create(order);

        // Then
        assertAll(() -> assertThat(saveOrder.getOrderTableId()).isEqualTo(firstOrderTableId));
    }

    @DisplayName("주문 목록을 조회할 수 있다")
    @Test
    void orderList() {
        // Given
        given(orderDao.findAll()).willReturn(Arrays.asList(order, order, order));

        // When
        final List<Order> orderList = orderBo.list();

        // Then
        assertThat(orderList).hasSize(3);
    }

    @DisplayName("주문상태를 변경시에 주문이 완료된 경우 예외를 발생 한다.")
    @Test
    void changeOrderStatus_orderStatusIsCompletion() {
        // Given
        given(orderDao.findById(ORDER_ID)).willReturn(Optional.of(order));
        given(order.getOrderStatus()).willReturn(OrderStatus.COMPLETION.name());

        // When
        // Then
        assertThatThrownBy(() -> orderBo.changeOrderStatus(ORDER_ID, order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문상태를 변경한다.")
    @Test
    void changeOrderStatus_success() {
        // Given
        final Order order = new Order();
        order.setId(ORDER_ID);
        order.setOrderStatus(OrderStatus.MEAL.name());

        given(orderDao.findById(ORDER_ID)).willReturn(Optional.of(order));
        given(orderDao.save(order)).willReturn(order);
        given(orderLineItemDao.findAllByOrderId(ORDER_ID))
                .willReturn(Arrays.asList(orderLineItem, orderLineItem, orderLineItem));

        // When
        final Order saveOrder = orderBo.changeOrderStatus(ORDER_ID, order);

        // Then
        assertAll(
                () -> assertThat(saveOrder.getOrderStatus()).isEqualTo(OrderStatus.MEAL.name()),
                () -> assertThat(saveOrder.getOrderLineItems()).hasSize(3));
    }
}
