package camp.nextstep.edu.kitchenpos.tablegroup.bo;

import camp.nextstep.edu.kitchenpos.order.domain.OrderRepository;
import camp.nextstep.edu.kitchenpos.order.domain.OrderStatus;
import camp.nextstep.edu.kitchenpos.ordertable.domain.OrderTable;
import camp.nextstep.edu.kitchenpos.ordertable.domain.OrderTableRepository;
import camp.nextstep.edu.kitchenpos.tablegroup.domain.TableGroup;
import camp.nextstep.edu.kitchenpos.tablegroup.domain.TableGroupRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Component
public class TableGroupBo {
    private final OrderRepository orderRepository;
    private final OrderTableRepository orderTableRepository;
    private final TableGroupRepository tableGroupRepository;

    public TableGroupBo(final OrderRepository orderRepository, final OrderTableRepository orderTableRepository, final TableGroupRepository tableGroupRepository) {
        this.orderRepository = orderRepository;
        this.orderTableRepository = orderTableRepository;
        this.tableGroupRepository = tableGroupRepository;
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

        final List<OrderTable> savedOrderTables = orderTableRepository.findAllByIdIn(orderTableIds);
        for (final OrderTable savedOrderTable : savedOrderTables) {
            if (savedOrderTable.isEmpty() || Objects.nonNull(savedOrderTable.getTableGroupId())) {
                throw new IllegalArgumentException();
            }
        }

        tableGroup.setCreatedDate(LocalDateTime.now());

        final TableGroup savedTableGroup = tableGroupRepository.save(tableGroup);

        final Long tableGroupId = savedTableGroup.getId();
        for (final OrderTable savedOrderTable : savedOrderTables) {
            savedOrderTable.setTableGroupId(tableGroupId);
            orderTableRepository.save(savedOrderTable);
        }
        savedTableGroup.setOrderTables(savedOrderTables);

        return savedTableGroup;
    }

    @Transactional
    public void delete(final Long tableGroupId) {
        final List<OrderTable> savedOrderTables = orderTableRepository.findAllByTableGroupId(tableGroupId);
        final OrderTable orderTable = savedOrderTables.stream()
                .sorted(Comparator.comparingLong(OrderTable::getId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        if (orderRepository.existsByOrderTableIdAndOrderStatusIn(
                orderTable.getId(), Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))) {
            throw new IllegalArgumentException();
        }

        for (final OrderTable savedOrderTable : savedOrderTables) {
            savedOrderTable.setTableGroupId(null);
            orderTableRepository.save(savedOrderTable);
        }
    }
}
