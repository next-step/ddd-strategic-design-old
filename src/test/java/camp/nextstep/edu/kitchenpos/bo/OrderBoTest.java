package camp.nextstep.edu.kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import camp.nextstep.edu.kitchenpos.order.bo.OrderBo;
import camp.nextstep.edu.kitchenpos.order.domain.Order;
import camp.nextstep.edu.kitchenpos.order.domain.OrderLineItem;
import camp.nextstep.edu.kitchenpos.order.domain.OrderStatus;
import camp.nextstep.edu.kitchenpos.order.infra.OrderDao;
import camp.nextstep.edu.kitchenpos.order.infra.OrderLineItemDao;
import camp.nextstep.edu.kitchenpos.ordertable.domain.OrderTable;
import camp.nextstep.edu.kitchenpos.ordertable.infra.OrderTableDao;
import camp.nextstep.edu.kitchenpos.tablegroup.domain.TableGroup;
import camp.nextstep.edu.kitchenpos.tablegroup.infra.TableGroupDao;
import java.util.Arrays;
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

    @Test
    @DisplayName("주문은 주문상품이 있는 경우 생성할 수 있다.")
    void add(){

        Order order = this.createOrder(1L, Arrays.asList(this.createOrderLine(1L, 2), this.createOrderLine(1L, 3)));
        when(orderTableDao.findById(anyLong())).thenReturn(this.createOrderTable(1L, null, false));
        when(orderDao.save(any())).thenReturn(order);

        Order actual = orderBo.create(order);

        assertThat(actual).isNotNull();
        assertThat(actual.getOrderedTime()).isNotNull();
        assertThat(actual).isEqualTo(order);
    }

    @Test
    @DisplayName("주문테이블이 비어있으면 안된다.")
    void is_not_empty(){

        Order order = this.createOrder(1L, Arrays.asList(this.createOrderLine(1L, 2), this.createOrderLine(1L, 3)));
        when(orderTableDao.findById(anyLong())).thenReturn(this.createOrderTable(1L, 1L, true));


        assertThrows(IllegalArgumentException.class, () -> orderBo.create(order));
    }

    @Test
    @DisplayName("테이블그룹에 속한 경우 현재 주문을 테이블그룹에 추가한다.")
    void table_group_add(){

        Order order = this.createOrder(1L, Arrays.asList(this.createOrderLine(1L, 2), this.createOrderLine(1L, 3)));
        when(orderTableDao.findById(anyLong())).thenReturn(this.createOrderTable(1L, 1L, false));
        when(tableGroupDao.findById(anyLong())).thenReturn(this.createTableGroup(null));
        when(orderTableDao.findAllByTableGroupId(anyLong())).thenReturn(Arrays.asList(this.createOrderTable(1L, 1L, false).get(), this.createOrderTable(2L, 1L, false).get()));
        when(orderDao.save(any())).thenReturn(order);

        Order actual = orderBo.create(order);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(order);
    }

    @Test
    @DisplayName("테이블그룹에 속한 경우 현재 주문을 테이블그룹에 추가한다.")
    void order_list(){

        List<Order> orderList = Arrays.asList(this.createOrder(1L, null));
        when(orderDao.findAll()).thenReturn(orderList);
        List<OrderLineItem> orderLineItemList =  Arrays.asList(this.createOrderLine(1L, 2), this.createOrderLine(1L, 3));
        when(orderLineItemDao.findAllByOrderId(anyLong())).thenReturn(orderLineItemList);


        List<Order> actual = orderBo.list();


        assertThat(actual).isNotNull();
        assertThat(actual).hasSize(orderList.size());
    }

    @Test
    @DisplayName("주문상태를 변경할 수 있다.")
    void change_status(){

        Optional<Order> order = this.createStatusOrder(1L, OrderStatus.COOKING, Arrays.asList(this.createOrderLine(1L, 2), this.createOrderLine(1L, 3)));
        when(orderDao.findById(anyLong())).thenReturn(order);
        List<OrderLineItem> orderLineItemList =  Arrays.asList(this.createOrderLine(1L, 2), this.createOrderLine(1L, 3));
        when(orderLineItemDao.findAllByOrderId(anyLong())).thenReturn(orderLineItemList);


        Order actual = orderBo.changeOrderStatus(1L, order.get());


        assertThat(actual).isNotNull();
        assertThat(actual.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
    }

    @Test
    @DisplayName("요리 준비, 식사  상태의 경우만 변경할 수 있다.")
    void not_completion_status(){
        Optional<Order> order = this.createStatusOrder(1L, OrderStatus.COMPLETION, Arrays.asList(this.createOrderLine(1L, 2), this.createOrderLine(1L, 3)));
        when(orderDao.findById(anyLong())).thenReturn(order);

        
        assertThrows(IllegalArgumentException.class, () -> orderBo.changeOrderStatus(1L, order.get()));
    }

    private Optional<Order> createStatusOrder(Long orderTableId, OrderStatus orderStatus, List<OrderLineItem> orderLineItems){
        Order order = this.createOrder(orderTableId, orderLineItems);
        order.setOrderStatus(orderStatus.name());

        return Optional.of(order);
    }

    private Order createOrder(Long orderTableId, List<OrderLineItem> orderLineItems){
        Order order = new Order();
        order.setId(1L);
        order.setOrderTableId(orderTableId);
        order.setOrderLineItems(orderLineItems);

        return order;
    }

    private OrderLineItem createOrderLine(Long orderId, long quantity){
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setOrderId(orderId);
        orderLineItem.setQuantity(quantity);

        return orderLineItem;
    }

    private Optional<TableGroup> createTableGroup(List<OrderTable> orderTables){
        TableGroup tableGroup = new TableGroup();
        tableGroup.setId(1L);
        tableGroup.setOrderTables(orderTables);

        return Optional.of(tableGroup);
    }

    private Optional<OrderTable> createOrderTable(Long id, Long tableGroupId, boolean isEmpty){

        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setTableGroupId(tableGroupId);
        orderTable.setEmpty(isEmpty);

        return Optional.of(orderTable);
    }

}