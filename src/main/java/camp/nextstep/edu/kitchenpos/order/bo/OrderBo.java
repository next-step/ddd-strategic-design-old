package camp.nextstep.edu.kitchenpos.order.bo;

import camp.nextstep.edu.kitchenpos.order.domain.Order;
import camp.nextstep.edu.kitchenpos.order.domain.OrderRepository;
import camp.nextstep.edu.kitchenpos.order.domain.OrderLineItem;
import camp.nextstep.edu.kitchenpos.order.domain.OrderLineItemRepository;
import camp.nextstep.edu.kitchenpos.order.domain.OrderStatus;
import camp.nextstep.edu.kitchenpos.ordertable.domain.OrderTable;
import camp.nextstep.edu.kitchenpos.ordertable.domain.OrderTableRepository;
import camp.nextstep.edu.kitchenpos.tablegroup.domain.TableGroup;
import camp.nextstep.edu.kitchenpos.tablegroup.domain.TableGroupRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Component
public class OrderBo {
    private final OrderRepository orderDao;
    private final OrderLineItemRepository orderLineItemDao;
    private final OrderTableRepository orderTableDao;
    private final TableGroupRepository tableGroupDao;

    public OrderBo(
            final OrderRepository orderDao,
            final OrderLineItemRepository orderLineItemDao,
            final OrderTableRepository orderTableDao,
            final TableGroupRepository tableGroupDao
    ) {
        this.orderDao = orderDao;
        this.orderLineItemDao = orderLineItemDao;
        this.orderTableDao = orderTableDao;
        this.tableGroupDao = tableGroupDao;
    }

    @Transactional
    public Order create(final Order order) {
        final List<OrderLineItem> orderLineItems = order.getOrderLineItems();

        if (CollectionUtils.isEmpty(orderLineItems)) {
            throw new IllegalArgumentException();
        }

        order.setId(null);

        OrderTable orderTable = orderTableDao.findById(order.getOrderTableId())
                .orElseThrow(IllegalArgumentException::new);

        if (orderTable.isEmpty()) {
            throw new IllegalArgumentException();
        }

        if (Objects.nonNull(orderTable.getTableGroupId())) {
            final TableGroup tableGroup = tableGroupDao.findById(orderTable.getTableGroupId())
                    .orElseThrow(IllegalArgumentException::new);

            final List<OrderTable> orderTables = orderTableDao.findAllByTableGroupId(tableGroup.getId());
            orderTable = orderTables.stream()
                    .sorted(Comparator.comparingLong(OrderTable::getId))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new)
            ;
        }

        order.setOrderTableId(orderTable.getId());
        order.setOrderStatus(OrderStatus.COOKING.name());
        order.setOrderedTime(LocalDateTime.now());

        final Order savedOrder = orderDao.save(order);

        final Long orderId = savedOrder.getId();
        final List<OrderLineItem> savedOrderLineItems = new ArrayList<>();
        for (final OrderLineItem orderLineItem : orderLineItems) {
            orderLineItem.setOrderId(orderId);
            savedOrderLineItems.add(orderLineItemDao.save(orderLineItem));
        }
        savedOrder.setOrderLineItems(savedOrderLineItems);

        return savedOrder;
    }

    public List<Order> list() {
        final List<Order> orders = orderDao.findAll();

        for (final Order order : orders) {
            order.setOrderLineItems(orderLineItemDao.findAllByOrderId(order.getId()));
        }

        return orders;
    }

    @Transactional
    public Order changeOrderStatus(final long orderId, final Order order) {
        final Order savedOrder = orderDao.findById(orderId)
                .orElseThrow(IllegalArgumentException::new);

        if (OrderStatus.COMPLETION.name().equals(savedOrder.getOrderStatus())) {
            throw new IllegalArgumentException();
        }

        final OrderStatus orderStatus = OrderStatus.valueOf(order.getOrderStatus());
        savedOrder.setOrderStatus(orderStatus.name());

        orderDao.save(savedOrder);

        savedOrder.setOrderLineItems(orderLineItemDao.findAllByOrderId(orderId));

        return savedOrder;
    }
}
