package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.order.bo.OrderBo;
import camp.nextstep.edu.kitchenpos.order.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.order.model.Order;
import camp.nextstep.edu.kitchenpos.order.model.OrderStatus;
import camp.nextstep.edu.kitchenpos.orderlineitem.dao.OrderLineItemDao;
import camp.nextstep.edu.kitchenpos.orderlineitem.model.OrderLineItem;
import camp.nextstep.edu.kitchenpos.ordertable.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.ordertable.model.OrderTable;
import camp.nextstep.edu.kitchenpos.support.dao.InMemoryOrderDao;
import camp.nextstep.edu.kitchenpos.support.dao.InMemoryOrderLineItemDao;
import camp.nextstep.edu.kitchenpos.support.dao.InMemoryOrderTableDao;
import camp.nextstep.edu.kitchenpos.support.dao.InMemoryTableGroupDao;
import camp.nextstep.edu.kitchenpos.tablegroup.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.tablegroup.model.TableGroup;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.LongStream;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("`고객`이 `매장`에서 식사를 위해 `메뉴`를 `주문 항목`의 형태로 요청하는 행위를 뜻한다.")
class OrderBoTest {

    private OrderDao orderDao;
    private OrderLineItemDao orderLineItemDao;
    private OrderTableDao orderTableDao;
    private TableGroupDao tableGroupDao;

    private OrderBo orderBo;

    @BeforeEach
    void setUp() {
        orderDao = new InMemoryOrderDao();
        orderLineItemDao = new InMemoryOrderLineItemDao();
        orderTableDao = new InMemoryOrderTableDao();
        tableGroupDao = new InMemoryTableGroupDao();

        orderBo = new OrderBo(orderDao, orderLineItemDao, orderTableDao, tableGroupDao);
    }

    @DisplayName("`주문` 등록 시 `주문 항목`이 비었다면 예외처리 한다.")
    @Test
    void create_emptyOrderLineItems() {
        // given
        final Order order = new Order();
        order.setOrderLineItems(emptyList());

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("`주문` 등록 시 `주문 항목`이 1개 라면 예외처리 한다.")
    @Test
    void create_singleOrderLineItems() {
        // given
        final OrderLineItem orderLineItem = new OrderLineItem();

        final Order order = new Order();
        order.setOrderLineItems(List.of(orderLineItem));

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("`주문` 등록 시 `주문 테이블`이 미리 등록돼지 않았다면 예외처리 한다.")
    @Test
    void create_notExistsOrderTable() {
        // given
        final OrderLineItem orderLineItem1 = new OrderLineItem();
        final OrderLineItem orderLineItem2 = new OrderLineItem();

        final Order order = new Order();
        order.setOrderLineItems(List.of(orderLineItem1, orderLineItem2));

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("`주문` 등록 시 `주문 테이블`이 비어있다면 예외처리 한다.")
    @Test
    void create_emptyOrderTable() {
        // given
        final OrderLineItem orderLineItem1 = new OrderLineItem();
        final OrderLineItem orderLineItem2 = new OrderLineItem();

        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setEmpty(true);

        orderTableDao.save(orderTable);

        final Order order = new Order();
        order.setOrderLineItems(List.of(orderLineItem1, orderLineItem2));
        order.setOrderTableId(orderTable.getId());

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("`주문` 등록 시 `주문 테이블`이 `테이블 그룹`에 속해 있을 때, `테이블 그룹`이 미리 등록되지 않았다면 예외처리 한다.")
    @Test
    void create_whenOrderTableHaveTableGroupThenNotExistsTableGroup() {
        // given
        final OrderLineItem orderLineItem1 = new OrderLineItem();
        final OrderLineItem orderLineItem2 = new OrderLineItem();

        final TableGroup tableGroup = new TableGroup();
        tableGroup.setId(1L);

        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setTableGroupId(tableGroup.getId());

        orderTableDao.save(orderTable);

        final Order order = new Order();
        order.setOrderLineItems(List.of(orderLineItem1, orderLineItem2));
        order.setOrderTableId(orderTable.getId());

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("`주문` 등록 시 `주문 테이블`이 `테이블 그룹`에 속해 있을 때, " +
            "`테이블 그룹`에 포함된 `주문 테이블` 중 가장 먼저 등록된 `주문 테이블` 기준으로 주문을 진행한다.")
    @Test
    void create_changeOrderTableId() {
        // given
        final OrderLineItem orderLineItem1 = new OrderLineItem();
        final OrderLineItem orderLineItem2 = new OrderLineItem();

        final TableGroup tableGroup = new TableGroup();
        tableGroup.setId(1L);
        tableGroupDao.save(tableGroup);

        final OrderTable orderTable1 = new OrderTable();
        orderTable1.setId(1L);
        orderTable1.setTableGroupId(tableGroup.getId());
        orderTableDao.save(orderTable1);

        final OrderTable orderTable2 = new OrderTable();
        orderTable2.setId(2L);
        orderTable2.setTableGroupId(tableGroup.getId());
        orderTableDao.save(orderTable2);


        final Order order = new Order();
        order.setOrderLineItems(List.of(orderLineItem1, orderLineItem2));
        order.setOrderTableId(orderTable2.getId());

        // when
        final Order savedOrder = orderBo.create(order);

        // then
        assertThat(savedOrder.getOrderTableId()).isEqualTo(orderTable1.getId());
    }

    @DisplayName("`주문` 등록 시 `주문 상태`는 요리 중으로 변한다.")
    @Test
    void create() {
        // given
        final LocalDateTime startAt = LocalDateTime.now();
        final OrderLineItem orderLineItem1 = new OrderLineItem();
        final OrderLineItem orderLineItem2 = new OrderLineItem();

        final TableGroup tableGroup = new TableGroup();
        tableGroup.setId(1L);

        tableGroupDao.save(tableGroup);

        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);

        orderTableDao.save(orderTable);

        final Order order = new Order();
        order.setOrderLineItems(List.of(orderLineItem1, orderLineItem2));
        order.setOrderTableId(orderTable.getId());

        // when
        final Order savedOrder = orderBo.create(order);
        final LocalDateTime endAt = LocalDateTime.now();

        // then
        assertThat(savedOrder.getOrderedTime()).isBetween(startAt, endAt);
        assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
    }


    @DisplayName("`주문` 등록 시 `주문 항목`들도 같이 등록된다.")
    @Test
    void create_withOrderLineItem() {
        // given
        final OrderLineItem orderLineItem1 = new OrderLineItem();
        final OrderLineItem orderLineItem2 = new OrderLineItem();

        final TableGroup tableGroup = new TableGroup();
        tableGroup.setId(1L);

        tableGroupDao.save(tableGroup);

        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);

        orderTableDao.save(orderTable);

        final Order order = new Order();
        order.setOrderLineItems(List.of(orderLineItem1, orderLineItem2));
        order.setOrderTableId(orderTable.getId());

        // when
        orderBo.create(order);

        // then
        assertThat(orderLineItem1.getOrderId()).isEqualTo(order.getId());
        assertThat(orderLineItem2.getOrderId()).isEqualTo(order.getId());
    }

    @DisplayName("`주문` 조회 시 등록된 `주문`이 없다면 빈 리스트를 반환한다.")
    @Test
    void list_empty() {
        // when
        final List<Order> orders = orderBo.list();

        // then
        assertThat(orders).isEmpty();
    }

    @DisplayName("`주문`을 하나 등록 후 조회 시 등록된 `주문` 하나를 반환한다.")
    @Test
    void list_single() {
        // given
        final OrderLineItem orderLineItem1 = new OrderLineItem();
        final OrderLineItem orderLineItem2 = new OrderLineItem();

        final TableGroup tableGroup = new TableGroup();
        tableGroup.setId(1L);

        tableGroupDao.save(tableGroup);

        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);

        orderTableDao.save(orderTable);

        final Order order = new Order();
        order.setOrderLineItems(List.of(orderLineItem1, orderLineItem2));
        order.setOrderTableId(orderTable.getId());

        orderBo.create(order);

        // when
        final List<Order> orders = orderBo.list();

        // then
        assertThat(orders).containsExactly(order);
    }

    @DisplayName("`주문` 조회 시 등록된 `주문`의 갯수 만큼 반환한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 3, 100, 234})
    void list_many(final int size) {
        // given
        LongStream.range(0, size)
                .mapToObj(id -> {
                    final OrderLineItem orderLineItem1 = new OrderLineItem();
                    final OrderLineItem orderLineItem2 = new OrderLineItem();

                    final TableGroup tableGroup = new TableGroup();
                    tableGroup.setId(id);

                    tableGroupDao.save(tableGroup);

                    final OrderTable orderTable = new OrderTable();
                    orderTable.setId(id);

                    orderTableDao.save(orderTable);

                    final Order order = new Order();
                    order.setOrderLineItems(List.of(orderLineItem1, orderLineItem2));
                    order.setOrderTableId(orderTable.getId());

                    return order;
                })
                .forEach(orderBo::create);

        // when
        final List<Order> orders = orderBo.list();

        // then
        assertThat(orders).hasSize(size);
    }


    @DisplayName("`주문`의 목록을 조회하면 `주문`에 등록된 `주문 항목`들을 같이 볼 수 있다.")
    @Test
    void list_withOrderLineItems() {
        // given
        final OrderLineItem orderLineItem1 = new OrderLineItem();
        final OrderLineItem orderLineItem2 = new OrderLineItem();

        final TableGroup tableGroup = new TableGroup();
        tableGroup.setId(1L);

        tableGroupDao.save(tableGroup);

        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);

        orderTableDao.save(orderTable);

        final Order order = new Order();
        order.setId(1L);
        order.setOrderLineItems(List.of(orderLineItem1, orderLineItem2));
        order.setOrderTableId(orderTable.getId());

        orderBo.create(order);

        // when
        final List<Order> orders = orderBo.list();

        // then
        orders.stream()
                .map(Order::getOrderLineItems)
                .map(Assertions::assertThat)
                .forEach(orderLineItemsAssert -> orderLineItemsAssert.containsExactly(orderLineItem1, orderLineItem2));
    }

    @DisplayName("`주문`의 `주문 상태` 변경 시 `주문`이 미리 등록 돼 있지 않다면 예외처리 한다.")
    @Test
    void changeOrderStatus_notExists() {
        // given
        final Order order = new Order();
        order.setId(1L);

        final Order changeOrder = new Order();
        changeOrder.setOrderStatus(OrderStatus.COOKING.name());

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.changeOrderStatus(order.getId(), changeOrder));
    }

    @DisplayName("`주문`의 `주문 상태` 변경 시 `주문 상태`가 이미 완료 됐다면 예외처리 한다.")
    @Test
    void changeOrderStatus_orderStatusCompletion() {
        // given
        final OrderLineItem orderLineItem1 = new OrderLineItem();
        final OrderLineItem orderLineItem2 = new OrderLineItem();

        final TableGroup tableGroup = new TableGroup();
        tableGroup.setId(1L);

        tableGroupDao.save(tableGroup);

        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);

        orderTableDao.save(orderTable);

        final Order order = new Order();
        order.setId(1L);
        order.setOrderLineItems(List.of(orderLineItem1, orderLineItem2));
        order.setOrderTableId(orderTable.getId());

        orderBo.create(order);

        final Order completionChangeOrder = new Order();
        completionChangeOrder.setOrderStatus(OrderStatus.COMPLETION.name());
        orderBo.changeOrderStatus(order.getId(), completionChangeOrder);

        final Order changeOrder = new Order();
        changeOrder.setOrderStatus(OrderStatus.COOKING.name());

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.changeOrderStatus(order.getId(), changeOrder));
    }

    @DisplayName("`주문`의 `주문 상태`를 변경할 수 있다.")
    @EnumSource(value = OrderStatus.class)
    @ParameterizedTest
    void changeOrderStatus(final OrderStatus orderStatus) {
        // given
        final OrderLineItem orderLineItem1 = new OrderLineItem();
        final OrderLineItem orderLineItem2 = new OrderLineItem();

        final TableGroup tableGroup = new TableGroup();
        tableGroup.setId(1L);

        tableGroupDao.save(tableGroup);

        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);

        orderTableDao.save(orderTable);

        final Order order = new Order();
        order.setId(1L);
        order.setOrderLineItems(List.of(orderLineItem1, orderLineItem2));
        order.setOrderTableId(orderTable.getId());

        orderBo.create(order);

        final Order changeOrder = new Order();
        changeOrder.setOrderStatus(orderStatus.name());

        // when
        final Order changedOrder = orderBo.changeOrderStatus(order.getId(), changeOrder);

        // then
        assertThat(changedOrder.getOrderStatus()).isEqualTo(orderStatus.name());
    }
}