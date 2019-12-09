package camp.nextstep.edu.kitchenpos.order.domain;

import java.util.List;
import java.util.Optional;

public interface OrderLineItemDao {

    OrderLineItem save(final OrderLineItem entity);

    Optional<OrderLineItem> findById(final Long id);

    List<OrderLineItem> findAll();

    List<OrderLineItem> findAllByOrderId(final Long orderId);

}
