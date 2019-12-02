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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("주문 Business Object 테스트 클래스")
@ExtendWith(MockitoExtension.class)
class OrderBoTest {
    @Mock
    private Order order;

    @Mock
    private OrderDao orderDao;

    @InjectMocks
    private OrderBo orderBo;

    @Mock
    private OrderLineItemDao orderLineItemDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @DisplayName("주문은 주문 번호, 주문 테이블 번호, 주문 상태, 주문 시간, 주문 항목 속성들을 가지고 있다.")
    @Test
    void hasProperties() {
        String orderIdPropertyName = "id";
        String orderTableIdPropertyName = "orderTableId";
        String orderStatusPropertyName = "orderStatus";
        String orderedTimePropertyName = "orderedTime";
        String orderLineItemsPropertyName = "orderLineItems";

        assertThat(order).hasFieldOrProperty(orderIdPropertyName);
        assertThat(order).hasFieldOrProperty(orderTableIdPropertyName);
        assertThat(order).hasFieldOrProperty(orderStatusPropertyName);
        assertThat(order).hasFieldOrProperty(orderedTimePropertyName);
        assertThat(order).hasFieldOrProperty(orderLineItemsPropertyName);
    }

    @DisplayName("[주문 생성] 주문은 여러 주문 항목을 가질 수 있다")
    @Test
    void hasManyOrderLineItem() {
        // given
        List<OrderLineItem> orderLineItems = mock(List.class);
        given(orderLineItems.size()).willReturn(3);

        // when
        int size = orderLineItems.size();

        // then
        assertThat(size).isNotZero()
                        .isEqualTo(3);
    }

    @DisplayName("[주문 생성] 주문 항목이 없으면 예외를 발생 한다.")
    @Test
    void whenMenuLineItemsNotExist_thenFail() {
        // given
        given(order.getOrderLineItems()).willReturn(null);

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("[주문 생성] 주문 테이블이 존재하지 않으면 예외를 발생한다.")
    @Test
    void whenOrderTableNotExist_thenFail() {
        // given
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(new OrderLineItem());
        given(order.getOrderLineItems()).willReturn(orderLineItems);
        given(orderTableDao.findById(any())).willReturn(Optional.empty());

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("[주문 생성] 주문 테이블이 특정 테이블 그룹에 속해 있으면 성공을 반환환다.")
    @Test
    void whenTableGroupExist_thenSuccess() {
        // given
        OrderTable orderTable = new OrderTable();
        orderTable.setTableGroupId(1L);

        given(orderTableDao.findById(any()))
                           .willReturn(Optional.of(orderTable));
        given(tableGroupDao.findById(any()))
                           .willReturn(Optional.of(new TableGroup()));

        // when
        OrderTable savedOrderTable = orderTableDao.findById(any()).get();
        TableGroup tableGroup = tableGroupDao.findById(any()).get();

        // then
        assertThat(tableGroup).isNotNull();
    }

    @DisplayName("[주문 생성] 테이블 그룹에 속해있는 주문 테이블들 중에서 가장 먼저 생성된 주문 테이블을 갖고오면 성공을 반환한다.")
    @Test
    void getFirstOrderTableInTableGroup() {
        // given
        List<OrderTable> mockOrderTables = new ArrayList<>();
        OrderTable latestOrderTable = null;
        OrderTable firstOrderTable = new OrderTable();
        OrderTable secondOrderTable = new OrderTable();
        OrderTable thirdOrderTable = new OrderTable();

        firstOrderTable.setId(1L);
        secondOrderTable.setId(2L);
        thirdOrderTable.setId(3L);

        mockOrderTables.add(secondOrderTable);
        mockOrderTables.add(thirdOrderTable);
        mockOrderTables.add(firstOrderTable);

        given(orderTableDao.findAllByTableGroupId(any())).willReturn(mockOrderTables);

        // when
        List<OrderTable> orderTables = orderTableDao.findAllByTableGroupId(any());

        latestOrderTable = orderTables.stream()
                                      .sorted(Comparator.comparingLong(OrderTable::getId))
                                      .findFirst()
                                      .orElseThrow(IllegalArgumentException::new);

        // then
        assertThat(latestOrderTable).isNotNull();
        assertThat(latestOrderTable.getId()).isEqualTo(1L);
    }

    @DisplayName("[주문 생성] 주문이 생성시 주문 번호가 생성 되면 성공을 반환 한다.")
    @Test
    void whenOrderCreateWithOrderNumber_thenSuccess() {
        // given
        given(orderDao.save(any())).willReturn(order);

        // when
        Order savedOrder = orderDao.save(any());
        when(savedOrder.getId()).thenReturn(1L);

        // then
        assertThat(savedOrder.getId()).isNotNull()
                                      .isEqualTo(1L);
    }

    @DisplayName("[주문 생성] 생성된 주문 번호로 주문 항복들이 생성 되면 성공을 반환 한다.")
    @Test
    void whenOrderLineItemsAreCreated_withCreatedOrderNumber_thenSuccess() {
        // given
        long orderId = 1L;
        OrderLineItem orderLineItem = mock(OrderLineItem.class);
        OrderLineItem ohterOrderLineItem = mock(OrderLineItem.class);

        given(orderLineItemDao.save(any())).willReturn(orderLineItem);
        given(orderLineItemDao.save(any())).willReturn(ohterOrderLineItem);

        given(orderLineItemDao.save(any()).getOrderId()).willReturn(orderId);
        given(orderLineItemDao.save(any()).getOrderId()).willReturn(orderId);

        // when
        long orderLineItemId = orderLineItemDao.save(any()).getOrderId();
        long otherOrderLineItemId = orderLineItemDao.save(any()).getOrderId();

        // then
        assertThat(orderLineItemId).isEqualTo(otherOrderLineItemId);
        assertThat(orderId).isEqualTo(orderLineItemId);
        assertThat(orderId).isEqualTo(otherOrderLineItemId);
    }

    @DisplayName("[주문 생성] 주문을 생성할 수 있다.")
    @Test
    void create() {
        // given - 주문 객체 setting
        long id = 1L;

        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setTableGroupId(id);

        OrderTable otherOrderTable = new OrderTable();
        otherOrderTable.setId(2L);
        otherOrderTable.setTableGroupId(id);

        List<OrderTable> orderTables = new ArrayList<>();
        orderTables.add(otherOrderTable);
        orderTables.add(orderTable);

        OrderLineItem orderLineItem = createOrderLineItem();

        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        TableGroup tableGroup = new TableGroup();
        tableGroup.setId(id);

        Order mockOrder = createOrder();
        mockOrder.setOrderLineItems(orderLineItems);

        given(orderTableDao.findById(any())).willReturn(Optional.of(orderTable));
        given(tableGroupDao.findById(any())).willReturn(Optional.of(tableGroup));
        given(orderTableDao.findAllByTableGroupId(any())).willReturn(orderTables);
        given(orderDao.save(any())).willReturn(mockOrder);
        given(orderLineItemDao.save(any())).willReturn(orderLineItem);

        // when
        Order savedOrder = orderBo.create(mockOrder);

        assertThat(savedOrder).isNotNull()
                              .isEqualTo(mockOrder);
    }

    @DisplayName("[주문 조회] 주문 목록을 조회 할 수 있다")
    @Test
    void whenOrderCanSelect_thenSuccess() {
        // given
        List<Order> orders = mock(List.class);
        given(orderDao.findAll()).willReturn(orders);
        given(orders.size()).willReturn(2);

        // when
        List<Order> allMenu = orderDao.findAll();

        //then
        assertThat(allMenu.size()).isEqualTo(2);
    }

    @DisplayName("[주문 조회] 주문의 주문 항목들을 조회할 수 있다.")
    @Test
    void whenOrderLineItemsCanSelect_thenSuccess() {
        // given
        List<OrderLineItem> orderLineItems = mock(List.class);
        given(orderLineItemDao.findAllByOrderId(any())).willReturn(orderLineItems);
        given(orderLineItems.size()).willReturn(2);

        // when
        List<OrderLineItem> allOrderLineItems = orderLineItemDao.findAllByOrderId(any());

        //then
        assertThat(allOrderLineItems.size()).isEqualTo(2);
    }

    @DisplayName("[주문 상태 변경] 주문 상태가 완료일경우 예외를 발생 한다.")
    @Test
    void whenOrderStatusIsComplete_thenFail() {
        // given
        given(orderDao.findById(any())).willReturn(Optional.of(order));
        given(order.getOrderStatus()).willReturn(OrderStatus.COMPLETION.name());

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.changeOrderStatus(1L, order));
    }

    @DisplayName("[주문 상태 변경] 주문 상태를 Cooking에서 meal로 변경할 수 있다.")
    @Test
    void changeOrderStatus() {
        // given
        Order savedOrder = createOrder();

        given(orderDao.findById(any())).willReturn(Optional.of(savedOrder));
        given(order.getOrderStatus()).willReturn(OrderStatus.MEAL.name());
        given(orderLineItemDao.findAllByOrderId(any())).willReturn(new ArrayList<>());

        // when
        Order changedOrder = orderBo.changeOrderStatus(1L, order);

        // then
        assertThat(changedOrder.getOrderStatus()).isEqualTo(OrderStatus.MEAL.name());
    }

    @DisplayName("Order 객체 생성 테스트 픽스쳐")
    Order createOrder() {
        Order order = new Order();
        order.setId(1l);
        order.setOrderStatus(OrderStatus.COOKING.name());
        order.setOrderTableId(1l);
        return order;
    }

    @DisplayName("OrderLineItem 객체 생성 테스트 픽스쳐")
    OrderLineItem createOrderLineItem() {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1l);
        orderLineItem.setOrderId(1l);
        return orderLineItem;
    }
}