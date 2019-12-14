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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

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
    private OrderBo bo;

    @BeforeEach
    void setUp() {

    }

    @DisplayName("주문 순서 아이템 리스트를 갖고 있지 않은 주문은 생성 할 수 없다")
    @Test
    void canNotCreateOrder_whenEmptyOrderLineItems() {
        Order order = new Order();
        order.setOrderLineItems(Collections.emptyList());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> {
                    bo.create(order);
                });
    }

    @DisplayName("주문 테이블을 찾을수 없으면 주문을 생성할 수 없다")
    @Test
    void canNotCreateOrder_whenNotFoundOrderTableById() {
        Order order = new Order();
        order.setOrderLineItems(Collections.singletonList(new OrderLineItem()));

        lenient().when(orderTableDao.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> bo.create(order));
    }

    @DisplayName("주문 테이블을 갖고 있지 않은 주문은 주문을 생성할 수 없다")
    @Test
    void canNotCreateOrder_whenOrderTableIsEmpty() {
        Order order = new Order();
        order.setOrderLineItems(Collections.singletonList(new OrderLineItem()));

        lenient().when(orderTableDao.findById(anyLong()))
                .thenReturn(Optional.of(new OrderTable()));
    }

    @DisplayName("조회되는 주문 테이블이 없으면 주문을 생성할 수 없다.")
    @Test
    void canNotCreateOrder_whenHaveOrderLineItemsAndNotFoundTableGroup() {
        Order order = new Order();
        order.setOrderLineItems(Collections.singletonList(new OrderLineItem()));

        given(orderTableDao.findById(order.getOrderTableId()))
                .willReturn(Optional.empty());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> bo.create(order));
    }

    @DisplayName("주문 순서 아이템을 소유하고 조회되는 주문 테이블이 있으면 주문을 생성할 수 있다.")
    @Test
    void canCreateOrder_whenHaveOrderLineItemsAndFoundTableGroup() {
        Order order = new Order();
        order.setOrderLineItems(Collections.singletonList(new OrderLineItem()));

        given(orderTableDao.findById(order.getOrderTableId()))
                .willReturn(Optional.of(new OrderTable()));
        given(orderDao.save(order))
                .willReturn(order);

        assertThat(bo.create(order)).isNotNull();
    }

    @DisplayName("주문 리스트를 조회한다")
    @Test
    void getAllOrderList() {
        List<Order> orders = Arrays.asList(new Order());
        given(orderDao.findAll())
                .willReturn(orders);
        lenient()
                .when(orderLineItemDao.findAllByOrderId(1L))
                .thenReturn(Arrays.asList(new OrderLineItem()));

        List<Order> ordered = bo.list();

        assertThat(ordered).hasSize(1);
    }

    @DisplayName("주문 상태가 완료 상태이면 주문 상태를 변경할 수 없다")
    @Test
    void canNotChangeOrderStatus_whenOrderStatusIsCompletion() {
        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.COMPLETION.name());
        given(orderDao.findById(order.getId())).willReturn(Optional.of(order));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> bo.changeOrderStatus(order.getId(), order));
    }

    @DisplayName("주문 상태가 완료가 아닌 주문은 상태를 변경할 수 있다")
    @Test
    void canChangeOrderStatus() {
        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.COOKING.name());
        given(orderDao.findById(order.getId())).willReturn(Optional.of(order));

        Order changedOrder = bo.changeOrderStatus(order.getId(), order);

        assertThat(changedOrder).isNotNull();
    }
}
