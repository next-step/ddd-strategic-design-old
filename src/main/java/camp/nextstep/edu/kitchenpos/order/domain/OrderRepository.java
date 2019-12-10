package camp.nextstep.edu.kitchenpos.order.domain;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(final Order entity);

    Optional<Order> findById(final Long id);

    List<Order> findAll();

    boolean existsByOrderTableIdAndOrderStatusIn(final Long orderTableId, final List<String> orderStatuses);

}
