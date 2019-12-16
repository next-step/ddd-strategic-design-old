package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.order.bo.OrderBo;
import camp.nextstep.edu.kitchenpos.order.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.order.dao.OrderLineItemDao;
import camp.nextstep.edu.kitchenpos.table.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.table.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.order.model.Order;
import camp.nextstep.edu.kitchenpos.order.model.OrderLineItem;
import camp.nextstep.edu.kitchenpos.order.model.OrderStatus;
import camp.nextstep.edu.kitchenpos.table.model.OrderTable;
import camp.nextstep.edu.kitchenpos.table.model.TableGroup;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class OrderBoTest {

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


  @DisplayName("주문 목록을 볼 수 있다.")
  @Test
  public void list() throws Exception {
    //given
    List<Order> orders = Arrays.asList(createOrder(1L), createOrder(2L));
    given(orderDao.findAll()).willReturn(orders);

    //when
    List<Order> actual = orderBo.list();

    //then
    assertThat(actual).isNotNull();
    assertThat(actual).hasSize(orders.size());
  }

  @DisplayName("주문 상태를 변경할 수 있다.")
  @Test
  public void changeOrderStatus() throws Exception {
    //given
    Order order = createOrder(1L);
    order.setOrderStatus(OrderStatus.COOKING.name());

    given(orderDao.findById(order.getId())).willReturn(Optional.of(order));
    given(orderDao.save(order)).willReturn(order);

    //when
    Order actual = orderBo.changeOrderStatus(order.getId(), order);

    //then
    assertThat(actual.getOrderStatus()).isEqualTo(order.getOrderStatus());
  }

  @DisplayName("주문 상태 변경 시 주문의 상태가 완료여서는 안 된다.")
  @Test
  public void OrderStatusMustNotBeCompleted() throws Exception {
    //given
    Order order = createOrder(1L);
    order.setOrderStatus(OrderStatus.COMPLETION.name());

    given(orderDao.findById(order.getId())).willReturn(Optional.of(order));

    //then
    assertThrows(IllegalArgumentException.class,
        () -> orderBo.changeOrderStatus(order.getId(), order));
  }

  @DisplayName("주문을 등록할 수 있다.")
  @Test
  public void create() throws Exception {
    //given
    Order order = createOrder(1L);
    order.setOrderLineItems(Arrays.asList(createOrderLineItem()));

    OrderTable orderTable = createOrderTable(1L, false);

    given(orderTableDao.findById(any())).willReturn(Optional.of(orderTable));
    given(orderDao.save(order)).willReturn(order);

    //when
    Order actual = orderBo.create(order);

    //then
    assertThat(actual).isNotNull();
    assertThat(actual.getId()).isEqualTo(order.getId());
  }

  @DisplayName("주문 등록 시 주문 내역 목록이 비어 있어서는 안 된다.")
  @Test
  public void orderLineItemListShouldNotBeEmpty() throws Exception {
    assertThrows(IllegalArgumentException.class,
        () -> orderBo.create(new Order()));
  }

  @DisplayName("주문 등록 시 주 테이블이 빈 상태이면 안 된다.")
  @Test
  public void orderTableShouldNotBeEmpty() throws Exception {
    //given
    Order order = createOrder(1L);
    order.setOrderLineItems(Arrays.asList(createOrderLineItem()));

    OrderTable orderTable = createOrderTable(1L, true);

    given(orderTableDao.findById(any())).willReturn(Optional.of(orderTable));

    //then
    assertThrows(IllegalArgumentException.class,
        () -> orderBo.create(order));
  }

  @DisplayName("주문 테이블이 테이블 그룹에 속해있으면, 테이블 그룹 중 가장 낮은 주문 테이블 번호를 가진 주문 테이블의 정보가 등록된다.")
  @Test
  public void createLowestTableNum() throws Exception {
    // given
    Order order = createOrder(1L);
    order.setOrderLineItems(Arrays.asList(createOrderLineItem()));
    order.setOrderTableId(1L);

    OrderTable orderTable = createOrderTable(1L, false);
    orderTable.setTableGroupId(1L);
    OrderTable orderTable2 = createOrderTable(2L, false);

    TableGroup tableGroup = new TableGroup();
    tableGroup.setId(1L);

    given(orderTableDao.findById(order.getOrderTableId())).willReturn(Optional.of(orderTable));
    given(tableGroupDao.findById(orderTable.getTableGroupId())).willReturn(Optional.of(tableGroup));
    given(orderTableDao.findAllByTableGroupId(tableGroup.getId())).willReturn(Arrays.asList(orderTable, orderTable2));
    given(orderDao.save(order)).willReturn(order);

    // when
    final Order actual = orderBo.create(order);

    // then
    assertThat(actual.getOrderTableId()).isEqualTo(orderTable.getId());
  }

  @DisplayName("주문 등록 시 주문 상태는 조리 중으로 변경된다.")
  @Test
  public void orderStatusIsCooking() throws Exception {
    //given
    Order order = createOrder(1L);
    order.setOrderLineItems(Arrays.asList(createOrderLineItem()));

    OrderTable orderTable = createOrderTable(1L, false);

    given(orderTableDao.findById(any())).willReturn(Optional.of(orderTable));
    given(orderDao.save(order)).willReturn(order);

    //when
    Order actual = orderBo.create(order);

    //then
    assertThat(actual.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
  }

  private Order createOrder(Long id) {
    final Order order = new Order();
    order.setId(id);
    return order;
  }

  private OrderTable createOrderTable(Long id, boolean empty) {
    final OrderTable orderTable = new OrderTable();
    orderTable.setId(id);
    orderTable.setEmpty(empty);
    return orderTable;
  }

  private OrderLineItem createOrderLineItem() {
    final OrderLineItem orderLineItem = new OrderLineItem();
    return orderLineItem;
  }

}
