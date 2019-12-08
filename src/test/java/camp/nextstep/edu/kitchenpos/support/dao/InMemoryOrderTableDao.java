package camp.nextstep.edu.kitchenpos.support.dao;

import camp.nextstep.edu.kitchenpos.ordertable.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.ordertable.model.OrderTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableList;

public class InMemoryOrderTableDao implements OrderTableDao {

    private final List<OrderTable> orderTables = new ArrayList<>();

    @Override
    public OrderTable save(final OrderTable orderTable) {
        orderTables.add(orderTable);
        return orderTable;
    }

    @Override
    public Optional<OrderTable> findById(final Long id) {
        if (Objects.isNull(id)) {
            return Optional.empty();
        }

        return orderTables.stream()
                .filter(orderTable -> id.equals(orderTable.getId()))
                .findAny();
    }

    @Override
    public List<OrderTable> findAll() {
        return unmodifiableList(orderTables);
    }

    @Override
    public List<OrderTable> findAllByIdIn(final List<Long> ids) {
        if (ids.isEmpty()) {
            return emptyList();
        }

        return orderTables.stream()
                .filter(orderTable -> ids.contains(orderTable.getId()))
                .collect(toUnmodifiableList());
    }

    @Override
    public List<OrderTable> findAllByTableGroupId(final Long tableGroupId) {
        if (Objects.isNull(tableGroupId)) {
            return emptyList();
        }

        return orderTables.stream()
                .filter(orderTable -> tableGroupId.equals(orderTable.getTableGroupId()))
                .collect(toUnmodifiableList());
    }
}
