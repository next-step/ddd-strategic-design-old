package camp.nextstep.edu.kitchenpos.support.dao;

import camp.nextstep.edu.kitchenpos.dao.OrderLineItemDao;
import camp.nextstep.edu.kitchenpos.model.OrderLineItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableList;

public class InMemoryOrderLineItemDao implements OrderLineItemDao {

    private final List<OrderLineItem> orderLineItems = new ArrayList<>();

    @Override
    public OrderLineItem save(final OrderLineItem orderLineItem) {
        orderLineItems.add(orderLineItem);
        return orderLineItem;
    }

    @Override
    public Optional<OrderLineItem> findById(final Long id) {
        if (Objects.isNull(id)) {
            return Optional.empty();
        }

        return orderLineItems.stream()
                .filter(orderLineItem -> id.equals(orderLineItem.getSeq()))
                .findAny();
    }

    @Override
    public List<OrderLineItem> findAll() {
        return unmodifiableList(orderLineItems);
    }

    @Override
    public List<OrderLineItem> findAllByOrderId(final Long orderId) {
        if (Objects.isNull(orderId)) {
            return emptyList();
        }

        return orderLineItems.stream()
                .filter(orderLineItem -> orderId.equals(orderLineItem.getOrderId()))
                .collect(toUnmodifiableList());
    }
}
