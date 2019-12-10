package camp.nextstep.edu.kitchenpos.ordertable.bo;

import camp.nextstep.edu.kitchenpos.order.domain.OrderRepository;
import camp.nextstep.edu.kitchenpos.order.domain.OrderStatus;
import camp.nextstep.edu.kitchenpos.ordertable.domain.OrderTable;
import camp.nextstep.edu.kitchenpos.ordertable.domain.OrderTableRepository;
import camp.nextstep.edu.kitchenpos.tablegroup.domain.TableGroupRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TableBo {
    private final OrderRepository orderRepository;
    private final OrderTableRepository orderTableRepository;
    private final TableGroupRepository tableGroupRepository;

    public TableBo(final OrderRepository orderRepository, final OrderTableRepository orderTableRepository, final TableGroupRepository tableGroupDao) {
        this.orderRepository = orderRepository;
        this.orderTableRepository = orderTableRepository;
        this.tableGroupRepository = tableGroupDao;
    }

    @Transactional
    public OrderTable create(final OrderTable orderTable) {
        return orderTableRepository.save(orderTable);
    }

    public List<OrderTable> list() {
        return orderTableRepository.findAll();
    }

    @Transactional
    public OrderTable changeEmpty(final long orderTableId, final OrderTable orderTable) {
        final OrderTable savedOrderTable = orderTableRepository.findById(orderTableId)
                .orElseThrow(IllegalArgumentException::new);

        if (Objects.nonNull(savedOrderTable.getTableGroupId())) {
            throw new IllegalArgumentException();
        }

        if (orderRepository.existsByOrderTableIdAndOrderStatusIn(
                orderTableId, Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))) {
            throw new IllegalArgumentException();
        }

        savedOrderTable.setEmpty(orderTable.isEmpty());

        return orderTableRepository.save(savedOrderTable);
    }

    @Transactional
    public OrderTable changeNumberOfGuests(final long orderTableId, final OrderTable orderTable) {
        final int numberOfGuests = orderTable.getNumberOfGuests();

        if (numberOfGuests < 0) {
            throw new IllegalArgumentException();
        }

        final OrderTable savedOrderTable = orderTableRepository.findById(orderTableId)
                .orElseThrow(IllegalArgumentException::new);

        if (savedOrderTable.isEmpty()) {
            throw new IllegalArgumentException();
        }

        savedOrderTable.setNumberOfGuests(numberOfGuests);

        return orderTableRepository.save(savedOrderTable);
    }
}
