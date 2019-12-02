package camp.nextstep.edu.kitchenpos.support.dao;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Collections.unmodifiableList;

public class InMemoryOrderDao implements OrderDao {

    private final AtomicLong autoIncrement = new AtomicLong();
    private final List<Order> orders = new ArrayList<>();

    @Override
    public Order save(final Order order) {
        order.setId(autoIncrement.incrementAndGet());
        orders.add(order);
        return order;
    }

    @Override
    public Optional<Order> findById(final Long id) {
        if (Objects.isNull(id)) {
            return Optional.empty();
        }

        return orders.stream()
                .filter(order -> id.equals(order.getId()))
                .findAny();
    }

    @Override
    public List<Order> findAll() {
        return unmodifiableList(orders);
    }

    @Override
    public boolean existsByOrderTableIdAndOrderStatusIn(final Long orderTableId,
                                                        final List<String> orderStatuses) {
        if (Objects.isNull(orderTableId) || orderStatuses.isEmpty()) {
            return false;
        }

        return orders.stream()
                .filter(order -> orderTableId.equals(order.getOrderTableId()))
                .map(Order::getOrderStatus)
                .anyMatch(orderStatuses::contains);
    }
}
