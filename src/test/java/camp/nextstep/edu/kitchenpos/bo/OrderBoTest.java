package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderLineItemDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.Order;
import camp.nextstep.edu.kitchenpos.model.OrderLineItem;
import camp.nextstep.edu.kitchenpos.model.OrderStatus;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import org.junit.jupiter.api.BeforeEach;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class OrderBoTest {

    private Order order;
    private List<OrderLineItem> orderLineItems;

    @Mock
    private Order savedOrder;

    @Mock
    private OrderTable orderTable;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderLineItemDao orderLineItemDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @InjectMocks
    private OrderBo bo;

    @BeforeEach
    void setUp() {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItems = Arrays.asList(orderLineItem, orderLineItem);

        order = new Order();
        order.setOrderLineItems(orderLineItems);
    }

    @DisplayName("생성하려는 주문에 주문 항목 정보가 없을 경우 해당 주문을 생성할 수 없다")
    @Test
    void createTest_nullOrderLineItems() {
        order.setOrderLineItems(null);

        assertThatThrownBy(() -> bo.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("생성하려는 주문에 포함된 주문 항목이 비어있다면 해당 주문을 생성할 수 없다")
    @Test
    void createTest_emptyOrderLineItems() {
        order.setOrderLineItems(new ArrayList<>());

        assertThatThrownBy(() -> bo.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("생성하려는 주문에 테이블 정보가 없다면 해당 주문을 생성할 수 없다")
    @Test
    void createTest_nonExistOrderTable() {
        when(orderTableDao.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bo.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("생성하려는 주문을 한 테이블이 비움 처리 되어 있다면 해당 주문을 생성할 수 없다")
    @Test
    void createTest_emptyOrderTable() {
        when(orderTableDao.findById(any())).thenReturn(Optional.of(orderTable));
        when(orderTable.isEmpty()).thenReturn(true);

        assertThatThrownBy(() -> bo.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("생성하려는 주문을 한 테이블이 특정 테이블그룹에 속하면서" +
            " 해당 테이블그룹이 이미 등록되어 있지 않다면, 해당 주문을 생성할 수 없다")
    @Test
    void createTest_tableWithTableGroup() {
        when(orderTableDao.findById(any())).thenReturn(Optional.of(orderTable));
        when(orderTable.isEmpty()).thenReturn(false);
        when(orderTable.getTableGroupId()).thenReturn(1L);
        when(tableGroupDao.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bo.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("이미 등록되어 있지 않은 주문은 주문 진행 상태를 변경할 수 없다")
    @Test
    void changeOrderStatusTest_nonExistOrder() {
        when(orderDao.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bo.changeOrderStatus(1L, order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 진행 상태가 이미 식사 완료 상태라면, 주문 진행 상태를 변경할 수 없다")
    @Test
    void changeOrderStatusTest_completedOrder() {
        when(orderDao.findById(1L)).thenReturn(Optional.of(savedOrder));
        when(savedOrder.getOrderStatus()).thenReturn(OrderStatus.COMPLETION.name());

        assertThatThrownBy(() -> bo.changeOrderStatus(1L, order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 진행 상태 변경 성공")
    @Test
    void changeOrderStatusTest_basic() {
        String expectedOrderStatus = OrderStatus.MEAL.name();
        order.setOrderStatus(expectedOrderStatus);

        when(orderDao.findById(1L)).thenReturn(Optional.of(savedOrder));
        when(savedOrder.getOrderStatus()).thenReturn(OrderStatus.COOKING.name());
        when(orderLineItemDao.findAllByOrderId(1L)).thenReturn(orderLineItems);

        bo.changeOrderStatus(1L, order);

        verify(savedOrder, times(1))
                .setOrderStatus(expectedOrderStatus);
        verify(orderDao, times(1))
                .save(savedOrder);
        verify(savedOrder, times(1))
                .setOrderLineItems(orderLineItems);
    }

}
