package camp.nextstep.edu.kitchenpos.tablegroup.application;

import camp.nextstep.edu.kitchenpos.order.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.ordertable.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.tablegroup.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.order.model.OrderStatus;
import camp.nextstep.edu.kitchenpos.ordertable.model.OrderTable;
import camp.nextstep.edu.kitchenpos.tablegroup.domain.TableGroup;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class TableGroupService {
    private final OrderDao orderDao;
    private final OrderTableDao orderTableDao;
    private final TableGroupDao tableGroupDao;

    public TableGroupService(final OrderDao orderDao, final OrderTableDao orderTableDao, final TableGroupDao tableGroupDao) {
        this.orderDao = orderDao;
        this.orderTableDao = orderTableDao;
        this.tableGroupDao = tableGroupDao;
    }

    @Transactional
    public TableGroup create(final TableGroup tableGroup) {
        final List<OrderTable> orderTables = tableGroup.getOrderTables();

        if (CollectionUtils.isEmpty(orderTables) || orderTables.size() < 2) {
            throw new IllegalArgumentException();
        }

        final List<Long> orderTableIds = orderTables
                .stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList());

        final List<OrderTable> savedOrderTables = orderTableDao.findAllByIdIn(orderTableIds);
        for (final OrderTable savedOrderTable : savedOrderTables) {
            if (savedOrderTable.isEmpty() || Objects.nonNull(savedOrderTable.getTableGroupId())) {
                throw new IllegalArgumentException();
            }
        }

        tableGroup.setCreatedDate(LocalDateTime.now());

        final TableGroup savedTableGroup = tableGroupDao.save(tableGroup);

        final Long tableGroupId = savedTableGroup.getId();
        for (final OrderTable savedOrderTable : savedOrderTables) {
            savedOrderTable.setTableGroupId(tableGroupId);
            orderTableDao.save(savedOrderTable);
        }
        savedTableGroup.setOrderTables(savedOrderTables);

        return savedTableGroup;
    }

    @Transactional
    public void delete(final Long tableGroupId) {
        final List<OrderTable> savedOrderTables = orderTableDao.findAllByTableGroupId(tableGroupId);
        final OrderTable orderTable = savedOrderTables.stream()
                .sorted(Comparator.comparingLong(OrderTable::getId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        if (orderDao.existsByOrderTableIdAndOrderStatusIn(
                orderTable.getId(), Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))) {
            throw new IllegalArgumentException();
        }

        for (final OrderTable savedOrderTable : savedOrderTables) {
            savedOrderTable.setTableGroupId(null);
            orderTableDao.save(savedOrderTable);
        }
    }
}
