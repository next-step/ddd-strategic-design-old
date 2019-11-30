package camp.nextstep.edu.kitchenpos.bo;

import static camp.nextstep.edu.kitchenpos.bo.MockBuilder.mockCompletedOrder;
import static camp.nextstep.edu.kitchenpos.bo.MockBuilder.mockNotEmptyOrderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @DisplayName("주문을 생성할 수 있다")
    @Test
    void create() {
        //given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(1L);
        orderLineItem.setMenuId(9L);
        orderLineItem.setSeq(0L);

        List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem);

        Order request = new Order();
        request.setOrderTableId(100L);
        request.setOrderLineItems(orderLineItems);

        OrderTable queriedOrderTable = mockNotEmptyOrderTable(request.getOrderTableId());

        when(orderTableDao.findById(anyLong())).thenReturn(Optional.of(queriedOrderTable));

        when(orderDao.save(any())).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(orderLineItemDao.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        Order result = orderBo.create(request);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
        assertThat(result.getOrderedTime()).isNotNull();
        assertThat(result.getOrderLineItems()).isNotNull();

        assertThat(result.getOrderLineItems()).allMatch(x -> x.getOrderId().equals(result.getId()));
    }

    @DisplayName("포함할 주문아이템이 0개이거나 nul일 때 주문을 생성할 수 있다")
    @ParameterizedTest
    @NullAndEmptySource
    void given_order_line_items_is_null_or_empty_then_create_order_fails(
        List<OrderLineItem> nullOrEmpty) {
        //given
        List<OrderLineItem> orderLineItems = nullOrEmpty;

        Order request = new Order();
        request.setOrderTableId(100L);
        request.setOrderLineItems(orderLineItems);

        //then
        assertThatIllegalArgumentException().isThrownBy(() ->
            orderBo.create(request)
        );
    }

    @DisplayName("주문이 유효한 주문테이블에 속하지 않으면 주문 생성 실패 ")
    @Test
    void given_order_table_group_is_not_found_then_create_order_fails() {
        //given
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(1L);
        orderLineItem.setMenuId(9L);
        orderLineItem.setSeq(0L);

        List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem);

        Order request = new Order();
        request.setOrderTableId(100L);
        request.setOrderLineItems(orderLineItems);

        when(orderTableDao.findById(anyLong())).thenReturn(Optional.empty());

        //then
        assertThatIllegalArgumentException().isThrownBy(() ->
            orderBo.create(request)
        );

    }

    @DisplayName("주문테이블이 속한 테이블 그룹이 없을 때 getOrderTableToServeOrder 실패 ")
    void given_order_does_not_have_order_table_when_getOrderTableToServeOrder_() {
        //given
        Order request = new Order();
        request.setOrderTableId(100L);

        when(orderTableDao.findById(anyLong())).thenReturn(Optional.empty());

        //then
        assertThatIllegalArgumentException().isThrownBy(() ->
            orderBo.getOrderTableToServeOrder(request)
        );

    }

    @DisplayName("주문테이블이 속한 테이블 그룹이 없을때 "
        + "getOrderTableToServeOrder 은 주문에 요청된 주문 테이블을 반환한다")
    void given_order_table_does_not_have_table_group_call_getOrderTableToServeOrder_returns_the_requested_order_table() {
        //given
        Order request = new Order();
        request.setOrderTableId(100L);

        OrderTable queriedOrderTable = mockNotEmptyOrderTable(1000L);
        queriedOrderTable.setTableGroupId(request.getOrderTableId());
        when(orderTableDao.findById(anyLong())).thenReturn(Optional.of(queriedOrderTable));

        when(tableGroupDao.findById(eq(queriedOrderTable.getTableGroupId())))
            .thenReturn(Optional.empty());

        //when
        OrderTable result = orderBo.getOrderTableToServeOrder(request);
        //then
        assertThat(result.getTableGroupId()).isEqualTo(request.getOrderTableId());
        assertThat(result.getId()).isEqualTo(queriedOrderTable.getId());

    }

    @DisplayName("주문테이블이 속한 테이블 그룹이 있을때 "
        + "getOrderTableToServeOrder 은 해당 테이블 그룹의 주문테이블 목록 중 가장 작은 ID를 갖는 주문 테이블을 반환한다")
    void given_order_table_group_have_other_order_tables_call_getOrderTableToServeOrder_returns_first_order_table() {
        //given
        Order request = new Order();
        request.setOrderTableId(100L);

        OrderTable queriedOrderTable = mockNotEmptyOrderTable(1000L);
        queriedOrderTable.setTableGroupId(request.getOrderTableId());
        when(orderTableDao.findById(anyLong())).thenReturn(Optional.of(queriedOrderTable));

        TableGroup tableGroupHavingOrderTable = new TableGroup();
        tableGroupHavingOrderTable.setId(queriedOrderTable.getTableGroupId());
        when(tableGroupDao.findById(eq(queriedOrderTable.getTableGroupId())))
            .thenReturn(Optional.of(tableGroupHavingOrderTable));

        OrderTable orderTableInSameTableGroup = mockNotEmptyOrderTable(900L);
        orderTableInSameTableGroup.setTableGroupId(request.getOrderTableId());
        List<OrderTable> orderTables = Arrays.asList(queriedOrderTable, orderTableInSameTableGroup);
        when(orderTableDao.findAllByTableGroupId(anyLong())).thenReturn(orderTables);

        //when
        OrderTable result = orderBo.getOrderTableToServeOrder(request);
        //then
        assertThat(result.getTableGroupId()).isEqualTo(request.getOrderTableId());
        assertThat(result.getId()).isEqualTo(orderTableInSameTableGroup.getId());

    }

    @DisplayName("전체 주문을 조회할 수 있다")
    @Test
    void list() {
        //given
        List<Order> order = Arrays.asList(mockCompletedOrder(1L, 100L));
        OrderLineItem orderLineItem = mockOrderLineItem(1L);
        List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem);
        when(orderDao.findAll()).thenReturn(order);
        when(orderLineItemDao.findAllByOrderId(anyLong())).thenReturn(orderLineItems);
        //when
        List<Order> result = orderBo.list();
        //then
        assertThat(result).hasSize(order.size());
        assertThat(result.get(0).getOrderLineItems()).hasSize(orderLineItems.size());
    }

    private OrderLineItem mockOrderLineItem(long orderId) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setOrderId(orderId);
        orderLineItem.setMenuId(100L);
        orderLineItem.setQuantity(1);
        orderLineItem.setSeq(0L);
        return orderLineItem;
    }


    @Test
    void changeOrderStatus() {
    }
}