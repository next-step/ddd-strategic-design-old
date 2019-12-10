package camp.nextstep.edu.kitchenpos.ordertable.domain;

import java.util.List;
import java.util.Optional;

public interface OrderTableRepository {

    OrderTable save(final OrderTable entity);

    Optional<OrderTable> findById(final Long id);

    List<OrderTable> findAll();

    List<OrderTable> findAllByIdIn(final List<Long> ids);

    List<OrderTable> findAllByTableGroupId(final Long tableGroupId);

}
