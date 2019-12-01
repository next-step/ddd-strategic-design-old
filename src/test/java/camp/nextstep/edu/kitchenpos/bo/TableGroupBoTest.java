package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import camp.nextstep.edu.kitchenpos.model.TableGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableGroupBoTest {

    @Mock private OrderDao orderDao;
    @Mock private OrderTableDao orderTableDao;
    @Mock private TableGroupDao tableGroupDao;

    @InjectMocks
    private TableGroupBo tableGroupBo;

    @DisplayName("주문테이블 목록을 가지고 있다")
    @Test
    void hasList() {
        // given
        final TableGroup tableGroup = createTableGroup(1L);

        // when
        // then
        assertThrows(IllegalArgumentException.class,
                () -> tableGroupBo.create(tableGroup));
    }


    @DisplayName("테이블묶음 추가는 주문테이블 2개 이상만 가능하다")
    @Test
    void graterThen2() {
        // given
        final List<OrderTable> orderTables = Arrays.asList(
                createOrderTable(1L, false), createOrderTable(2L, false));
        final TableGroup tableGroup = createTableGroup(1L, orderTables.toArray(new OrderTable[0]));

        given(orderTableDao.findAllByIdIn(any())).willReturn(orderTables);
        given(tableGroupDao.save(tableGroup)).willReturn(tableGroup);

        // when
        final TableGroup actual = tableGroupBo.create(tableGroup);

        // then
        assertThat(actual.getOrderTables()).hasSize(2);
    }

    @DisplayName("테이블묶음을 추가할 수 있다")
    @Test
    void create() {
        // given
        final TableGroup tableGroup = createTableGroup(
                1L, createOrderTable(1L, false, 1L), createOrderTable(2L, false, 2L));

        given(tableGroupDao.save(tableGroup)).willReturn(tableGroup);

        // when
        final TableGroup actual = tableGroupBo.create(tableGroup);

        // then
        assertThat(actual).isNotNull();
    }

    @DisplayName("테이블묶음 추가할 때 주문테이블이 비어있지 않고 소속된 테이블묶음이 없어야 한다")
    @Test
    void validationOrderTables() {
        // given
        final List<OrderTable> orderTables = Arrays.asList(
                createOrderTable(1L, true, 1L), createOrderTable(2L, false, 2L));
        final TableGroup tableGroup = createTableGroup(1L, orderTables.toArray(new OrderTable[0]));

        given(orderTableDao.findAllByIdIn(any())).willReturn(orderTables);

        // when
        // then
        assertThrows(IllegalArgumentException.class,
                () -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("테이블묶음에 속한 모든 주문테이블이 요리중 또는 식사중 상태가 아니면 테이블묶음을 삭제할 수 있다")
    @Test
    void delete() {
        // given
        final List<OrderTable> orderTables = Arrays.asList(
                createOrderTable(1L, true, 1L), createOrderTable(2L, false, 2L));
        final TableGroup tableGroup = createTableGroup(1L, orderTables.toArray(new OrderTable[0]));

        given(orderTableDao.findAllByTableGroupId(any())).willReturn(orderTables);
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(any(), any())).willReturn(false);

        // when
        tableGroupBo.delete(tableGroup.getId());

        // then
        assertThat(orderTables).allMatch(orderTable -> orderTable.getTableGroupId() == null);
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

    private TableGroup createTableGroup(final long id, final OrderTable... orderTables) {
        final TableGroup tableGroup = new TableGroup();
        tableGroup.setId(id);
        tableGroup.setOrderTables(Arrays.asList(orderTables));
        return tableGroup;
    }
}