package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableBoTest {

    @Mock private OrderDao orderDao;
    @Mock private OrderTableDao orderTableDao;
    @Mock private TableGroupDao tableGroupDao;

    @InjectMocks
    private TableBo tableBo;

    @DisplayName("주문테이블을 추가할 수 있다")
    @Test
    void create() {
        // given
        final OrderTable orderTable = createOrderTable(1L, false, 1L);

        given(orderTableDao.save(orderTable)).willReturn(orderTable);

        // when
        final OrderTable actual = tableBo.create(orderTable);

        // then
        assertThat(actual).isNotNull();
    }

    @DisplayName("모든 주문테이블을 조회할 수 있다")
    @Test
    void list() {
        // given
        final List<OrderTable> orderTables = Arrays.asList(
                createOrderTable(1L, false, 1L), createOrderTable(2L, false, 1L));

        given(orderTableDao.findAll()).willReturn(orderTables);

        // when
        final List<OrderTable> actual = tableBo.list();

        // then
        assertThat(actual).hasSize(orderTables.size());
    }

    @DisplayName("테이블묶음번호를 가지고 있다")
    @Test
    void hasTableGroupId() {
        // given
        final OrderTable orderTable = createOrderTable(1L, false, 999L);

        given(orderTableDao.findById(orderTable.getId())).willReturn(Optional.of(orderTable));

        // when
        // then
        assertThrows(IllegalArgumentException.class,
                () -> tableBo.changeEmpty(orderTable.getId(), orderTable));
    }

    @DisplayName("주문테이블이 비어있지 않고 손님이 0명 이상이면 손님 인원을 변경할 수 있다")
    @Test
    void changeNumberOfGuests() {
        // given
        final OrderTable orderTable = createOrderTable(1L, false, 999L);

        given(orderTableDao.findById(orderTable.getId())).willReturn(Optional.of(orderTable));
        given(orderTableDao.save(orderTable)).willReturn(orderTable);

        // when
        orderTable.setNumberOfGuests(10);
        tableBo.changeNumberOfGuests(orderTable.getId(), orderTable);

        // then
        assertThat(orderTable.getNumberOfGuests()).isEqualTo(10);
    }

    @DisplayName("빈 주문테이블인지 확인할 수 있다")
    @Test
    void isEmpty() {
        // given
        final OrderTable orderTable = createOrderTable(1L, false, 999L);

        // when
        final boolean actual = orderTable.isEmpty();

        // then
        assertThat(actual).isFalse();
    }

    @DisplayName("테이블묶음에 포함되지 않고 요리중 또는 식사중 상태가 아니면 주문테이블를 비울 수 있다")
    @Test
    void changeEmpty() {
        // given
        final OrderTable orderTable = createOrderTable(1L, false);

        given(orderTableDao.findById(orderTable.getId())).willReturn(Optional.of(orderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(any(), any())).willReturn(false);
        given(orderTableDao.save(orderTable)).willReturn(orderTable);

        // when
        orderTable.setEmpty(true);
        final OrderTable actual = tableBo.changeEmpty(orderTable.getId(), orderTable);

        // then
        assertThat(actual.isEmpty()).isTrue();
    }

    private OrderTable createOrderTable(final long id, final boolean isEmpty) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setEmpty(isEmpty);
        return orderTable;
    }

    private OrderTable createOrderTable(final long id, final boolean isEmpty, final long tableGroupId) {
        final OrderTable orderTable = createOrderTable(id, isEmpty);
        orderTable.setTableGroupId(tableGroupId);
        return orderTable;
    }
}