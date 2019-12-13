package camp.nextstep.edu.kitchenpos.order.application;

import camp.nextstep.edu.kitchenpos.order.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.order.dao.OrderLineItemDao;
import camp.nextstep.edu.kitchenpos.ordertable.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.tablegroup.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.order.domain.Order;
import camp.nextstep.edu.kitchenpos.order.domain.OrderLineItem;
import camp.nextstep.edu.kitchenpos.order.domain.OrderStatus;
import camp.nextstep.edu.kitchenpos.ordertable.domain.OrderTable;
import camp.nextstep.edu.kitchenpos.tablegroup.domain.TableGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("주문 Business Object 테스트 클래스")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
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
    private OrderService orderService;

    private final Long DEFAULT_ID = 1L;

    @DisplayName("주문은 주문 번호, 주문 테이블 번호, 주문 상태, 주문 시간, 주문 항목 속성들을 가지고 있다.")
    @Test
    void hasProperties() {
        String orderIdPropertyName = "id";
        String orderTableIdPropertyName = "orderTableId";
        String orderStatusPropertyName = "orderStatus";
        String orderedTimePropertyName = "orderedTime";
        String orderLineItemsPropertyName = "orderLineItems";

        assertAll(
                () -> assertThat(order).hasFieldOrProperty(orderIdPropertyName),
                () -> assertThat(order).hasFieldOrProperty(orderTableIdPropertyName),
                () -> assertThat(order).hasFieldOrProperty(orderStatusPropertyName),
                () -> assertThat(order).hasFieldOrProperty(orderedTimePropertyName),
                () -> assertThat(order).hasFieldOrProperty(orderLineItemsPropertyName)
        );
    }

    @DisplayName("[주문 생성] 주문은 여러 주문 항목을 가질 수 있다")
    @Test
    void hasManyOrderLineItem() {
        // given
        final int orderLineItemsSize = 3;
        List<OrderLineItem> orderLineItems = mock(List.class);
        given(orderLineItems.size()).willReturn(orderLineItemsSize);

        // when
        int size = orderLineItems.size();

        // then
        assertThat(size).isNotZero()
                        .isEqualTo(orderLineItemsSize);
    }

    @DisplayName("[주문 생성] 주문 항목이 없으면 예외를 발생 한다.")
    @Test
    void whenMenuLineItemsNotExist_thenFail() {
        // given
        given(order.getOrderLineItems()).willReturn(null);

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("[주문 생성] 주문 테이블이 존재하지 않으면 예외를 발생한다.")
    @Test
    void whenOrderTableNotExist_thenFail() {
        // given
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(new OrderLineItem());
        given(order.getOrderLineItems()).willReturn(orderLineItems);
        given(order.getOrderTableId()).willReturn(DEFAULT_ID);
        given(orderTableDao.findById(DEFAULT_ID)).willReturn(Optional.empty());

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("[주문 생성] 주문 테이블이 특정 테이블 그룹에 속해 있으면 성공을 반환환다.")
    @Test
    void whenTableGroupExist_thenSuccess() {
        // given
        final int orderTablesFirstIndex = 0;
        OrderTable orderTable = new OrderTable();
        TableGroup tableGroup = new TableGroup();
        tableGroup.setId(DEFAULT_ID);
        tableGroup.setOrderTables(Arrays.asList(orderTable));

        given(tableGroupDao.findById(DEFAULT_ID)).willReturn(Optional.of(tableGroup));

        // when
        TableGroup actualTableGroup = tableGroupDao.findById(DEFAULT_ID).get();

        // then
        assertAll(
                () -> assertThat(actualTableGroup).isNotNull(),
                () -> assertThat(actualTableGroup.getOrderTables()
                                                 .get(orderTablesFirstIndex)).isEqualTo(orderTable)
        );
    }

    @DisplayName("[주문 생성] 테이블 그룹에 속해있는 주문 테이블들 중에서 가장 먼저 생성된 주문 테이블을 갖고오면 성공을 반환한다.")
    @Test
    void getFirstOrderTableInTableGroup() {
        // given
        final Long SECOND_ORDER_TABLE_ID = 2L;
        final Long THIRD_ORDER_TABLE_ID = 3L;

        List<OrderTable> mockOrderTables = new ArrayList<>();
        OrderTable firstOrderTable = new OrderTable();
        OrderTable secondOrderTable = new OrderTable();
        OrderTable thirdOrderTable = new OrderTable();

        firstOrderTable.setId(DEFAULT_ID);
        secondOrderTable.setId(SECOND_ORDER_TABLE_ID);
        thirdOrderTable.setId(THIRD_ORDER_TABLE_ID);

        mockOrderTables.add(secondOrderTable);
        mockOrderTables.add(thirdOrderTable);
        mockOrderTables.add(firstOrderTable);

        given(orderTableDao.findAllByTableGroupId(DEFAULT_ID)).willReturn(mockOrderTables);

        // when
        List<OrderTable> orderTables = orderTableDao.findAllByTableGroupId(DEFAULT_ID);
        final OrderTable actual = orderTables.stream()
                                             .sorted(Comparator.comparingLong(OrderTable::getId))
                                             .findFirst()
                                             .orElseThrow(IllegalArgumentException::new);

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getId()).isEqualTo(DEFAULT_ID)
        );
    }

    @DisplayName("[주문 생성] 주문이 생성시 주문 번호가 생성 되면 성공을 반환 한다.")
    @Test
    void whenOrderCreateWithOrderNumber_thenSuccess() {
        // given
        given(orderDao.save(order)).willReturn(order);
        given(order.getId()).willReturn(DEFAULT_ID);

        // when
        Order savedOrder = orderDao.save(order);

        // then
        assertThat(savedOrder.getId()).isNotNull()
                                      .isEqualTo(DEFAULT_ID);
    }

    @DisplayName("[주문 생성] 생성된 주문 번호로 주문 항복들이 생성 되면 성공을 반환 한다.")
    @Test
    void whenOrderLineItemsAreCreated_withCreatedOrderNumber_thenSuccess() {
        // given
        OrderLineItem orderLineItem = mock(OrderLineItem.class);
        OrderLineItem ohterOrderLineItem = mock(OrderLineItem.class);

        given(orderLineItemDao.save(orderLineItem)).willReturn(orderLineItem);
        given(orderLineItemDao.save(ohterOrderLineItem)).willReturn(ohterOrderLineItem);

        given(orderLineItemDao.save(orderLineItem).getOrderId()).willReturn(DEFAULT_ID);
        given(orderLineItemDao.save(ohterOrderLineItem).getOrderId()).willReturn(DEFAULT_ID);

        // when
        long orderLineItemId = orderLineItemDao.save(orderLineItem).getOrderId();
        long otherOrderLineItemId = orderLineItemDao.save(ohterOrderLineItem).getOrderId();

        // then
        assertAll(
                () -> assertThat(orderLineItemId).isEqualTo(otherOrderLineItemId),
                () -> assertThat(DEFAULT_ID).isEqualTo(orderLineItemId),
                () -> assertThat(DEFAULT_ID).isEqualTo(otherOrderLineItemId)
        );
    }

    @DisplayName("[주문 생성] 주문을 생성할 수 있다.")
    @Test
    void create() {
        // given - 주문 객체 setting
        OrderTable orderTable = new OrderTable();
        orderTable.setId(DEFAULT_ID);
        orderTable.setTableGroupId(DEFAULT_ID);

        List<OrderTable> orderTables = new ArrayList<>();
        orderTables.add(orderTable);

        List<OrderLineItem> orderLineItems = new ArrayList<>();
        OrderLineItem orderLineItem = createOrderLineItem();
        orderLineItems.add(orderLineItem);

        TableGroup tableGroup = new TableGroup();
        tableGroup.setId(DEFAULT_ID);

        Order mockOrder = createOrder();
        mockOrder.setOrderLineItems(orderLineItems);

        given(orderTableDao.findById(DEFAULT_ID)).willReturn(Optional.of(orderTable));
        given(tableGroupDao.findById(DEFAULT_ID)).willReturn(Optional.of(tableGroup));
        given(orderTableDao.findAllByTableGroupId(DEFAULT_ID)).willReturn(orderTables);
        given(orderDao.save(mockOrder)).willReturn(mockOrder);
        given(orderLineItemDao.save(orderLineItem)).willReturn(orderLineItem);

        // when
        Order savedOrder = orderService.create(mockOrder);

        // then
        assertThat(savedOrder).isNotNull()
                              .isEqualTo(mockOrder);
    }

    @DisplayName("[주문 조회] 주문 목록을 조회 할 수 있다")
    @Test
    void whenOrderCanSelect_thenSuccess() {
        // given
        final int ordersSize = 2;
        List<Order> orders = mock(List.class);
        given(orderDao.findAll()).willReturn(orders);
        given(orders.size()).willReturn(ordersSize);

        // when
        List<Order> allMenu = orderDao.findAll();

        //then
        assertThat(allMenu).hasSize(ordersSize);
    }

    @DisplayName("[주문 조회] 주문의 주문 항목들을 조회할 수 있다.")
    @Test
    void whenOrderLineItemsCanSelect_thenSuccess() {
        // given
        final int ordersSize = 2;
        List<OrderLineItem> orderLineItems = mock(List.class);
        given(orderLineItemDao.findAllByOrderId(DEFAULT_ID)).willReturn(orderLineItems);
        given(orderLineItems.size()).willReturn(ordersSize);

        // when
        List<OrderLineItem> allOrderLineItems = orderLineItemDao.findAllByOrderId(DEFAULT_ID);

        //then
        assertThat(allOrderLineItems).hasSize(ordersSize);
    }

    @DisplayName("[주문 상태 변경] 주문 상태가 완료일경우 예외를 발생 한다.")
    @Test
    void whenOrderStatusIsComplete_thenFail() {
        // given
        given(orderDao.findById(DEFAULT_ID)).willReturn(Optional.of(order));
        given(order.getOrderStatus()).willReturn(OrderStatus.COMPLETION.name());

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.changeOrderStatus(DEFAULT_ID, order));
    }

    @DisplayName("[주문 상태 변경] 주문 상태를 Cooking에서 meal로 변경할 수 있다.")
    @Test
    void changeOrderStatus() {
        // given
        Order savedOrder = createOrder();
        given(orderDao.findById(DEFAULT_ID)).willReturn(Optional.of(savedOrder));
        given(order.getOrderStatus()).willReturn(OrderStatus.MEAL.name());
        given(orderLineItemDao.findAllByOrderId(DEFAULT_ID)).willReturn(new ArrayList<>());

        // when
        Order changedOrder = orderService.changeOrderStatus(DEFAULT_ID, order);

        // then
        assertThat(changedOrder.getOrderStatus()).isEqualTo(OrderStatus.MEAL.name());
    }

    private Order createOrder() {
        Order order = new Order();
        order.setId(DEFAULT_ID);
        order.setOrderStatus(OrderStatus.COOKING.name());
        order.setOrderTableId(DEFAULT_ID);
        return order;
    }

    private OrderLineItem createOrderLineItem() {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(DEFAULT_ID);
        orderLineItem.setOrderId(DEFAULT_ID);
        return orderLineItem;
    }
}