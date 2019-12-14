package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.OrderStatus;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import camp.nextstep.edu.kitchenpos.model.TableGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableGroupBoTest {

    @InjectMocks
    private TableGroupBo tableGroupBo;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @Mock
    private OrderDao orderDao;

    @DisplayName("2개이상의 테이블을 테이블 그룹으로 등록 한다")
    @Test
    void create() {
        // given
        long tableGroupId = 1L;
        List<OrderTable> orderTables = Arrays.asList(ofOrderTable(1L, 4),
                                                     ofOrderTable(2L, 2));

        TableGroup tableGroup = new TableGroup();
        tableGroup.setId(tableGroupId);
        tableGroup.setOrderTables(orderTables);

        List<Long> orderTableIds = orderTables.stream()
                                              .map(OrderTable::getId)
                                              .collect(Collectors.toList());

        given(orderTableDao.findAllByIdIn(orderTableIds)).willReturn(orderTables);
        given(tableGroupDao.save(tableGroup)).willReturn(tableGroup);
        orderTables.stream()
                   .forEach(orderTable -> given(orderTableDao.save(any())).willReturn(orderTable));

        // when
        TableGroup actual = tableGroupBo.create(tableGroup);

        // then
        assertThat(actual).extracting(TableGroup::getId)
                          .isEqualTo(tableGroup.getId());
        assertThat(actual.getOrderTables()).containsExactlyInAnyOrderElementsOf(orderTables);
    }

    @DisplayName("테이블이 없을 경우 테이블 그룹을 할 수 없다")
    @Test
    void createWhenOrderTablesEmpty_exception() {
        // given
        long tableGroupId = 1L;
        List<OrderTable> orderTables = null;

        TableGroup tableGroup = new TableGroup();
        tableGroup.setId(tableGroupId);
        tableGroup.setOrderTables(orderTables);

        // exception
        assertThatIllegalArgumentException()
                .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("테이블이 2개미만인 경우 테이블 그룹을 등록 할 수 없다")
    @Test
    void createWhenCountOfOrderTablesLessThanTwo_exception() {
        // given
        long tableGroupId = 1L;
        List<OrderTable> orderTables = Arrays.asList(ofOrderTable(1L, 4));

        TableGroup tableGroup = new TableGroup();
        tableGroup.setId(tableGroupId);
        tableGroup.setOrderTables(orderTables);

        // exception
        assertThatIllegalArgumentException()
                .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("미등록된 테이블이 포함되어 있을 경우 테이블 그룹을 등록 할 수 없다")
    @Test
    void createWhenNotFoundOrderTables_exception() {
        // given
        long tableGroupId = 1L;
        OrderTable registeredOrderTable = ofOrderTable(2L, 4);
        List<OrderTable> orderTables = Arrays.asList(ofOrderTable(1L, 4),
                                                     registeredOrderTable);

        TableGroup tableGroup = new TableGroup();
        tableGroup.setId(tableGroupId);
        tableGroup.setOrderTables(orderTables);

        List<Long> orderTableIds = orderTables.stream()
                                              .map(OrderTable::getId)
                                              .collect(Collectors.toList());
        given(orderTableDao.findAllByIdIn(orderTableIds)).willReturn(Arrays.asList(registeredOrderTable));

        // exception
        assertThatNullPointerException()
                .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("다른 테이블 그룹에 속한 테이블 일 경우 테이블 그룹을 등록 할 수 없다")
    @Test
    void createWhenIncludeAnotherTableGroup_exception() {
        // given
        long tableGroupId = 1L;
        List<OrderTable> orderTables = Arrays.asList(ofOrderTable(tableGroupId, 1L, 4),
                                                     ofOrderTable(2L, 4));

        TableGroup tableGroup = new TableGroup();
        tableGroup.setId(tableGroupId);
        tableGroup.setOrderTables(orderTables);

        List<Long> orderTableIds = orderTables.stream()
                                              .map(OrderTable::getId)
                                              .collect(Collectors.toList());
        given(orderTableDao.findAllByIdIn(orderTableIds)).willReturn(orderTables);

        // exception
        assertThatIllegalArgumentException()
                .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("빈 테이블이 포함된 경우 테이블 그룹을 등록 할 수 없다")
    @Test
    void createWhenOrderTableIsEmpty_exception() {
        // given
        long tableGroupId = 1L;
        List<OrderTable> orderTables = Arrays.asList(emptyOrderTable(1L),
                                                     ofOrderTable(2L, 4));

        TableGroup tableGroup = new TableGroup();
        tableGroup.setId(tableGroupId);
        tableGroup.setOrderTables(orderTables);

        List<Long> orderTableIds = orderTables.stream()
                                              .map(OrderTable::getId)
                                              .collect(Collectors.toList());
        given(orderTableDao.findAllByIdIn(orderTableIds)).willReturn(orderTables);

        // exception
        assertThatIllegalArgumentException()
                .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("테이블 그룹에 속한 테이블이 없을 경우 테이블 그룹을 삭제 할 수 없다")
    @Test
    void deleteWhenNotFoundIncludedOrderTable_exception() {
        // given
        long tableGroupId = 1L;
        ArrayList<OrderTable> notFoundedOrderTables = new ArrayList<>();

        given(orderTableDao.findAllByTableGroupId(tableGroupId)).willReturn(notFoundedOrderTables);

        // exception
        assertThatIllegalArgumentException().isThrownBy(() -> tableGroupBo.delete(tableGroupId));
    }

    @DisplayName("테이블 그룹에서 마지막으로 주문한 테이블이 식사완료이어야만 삭제 할 수 있다")
    @Test
    void deleteWhenOrderTableStatusIsNotComplete_exception() {
        // given
        long tableGroupId = 1L;
        List<OrderTable> orderTables = Arrays.asList(ofOrderTable(tableGroupId, 1l, 4),
                                                     ofOrderTable(tableGroupId, 2l, 4));

        given(orderTableDao.findAllByTableGroupId(tableGroupId)).willReturn(orderTables);
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(1l,
                                                            Arrays.asList(OrderStatus.COOKING.name(),
                                                                          OrderStatus.MEAL.name()))).willReturn(true);

        // exception
        assertThatIllegalArgumentException().isThrownBy(() -> tableGroupBo.delete(tableGroupId));
    }

    private OrderTable emptyOrderTable(long orderTableId) {
        return ofOrderTable(null, orderTableId, 0, true);
    }

    private OrderTable ofOrderTable(long orderTableId, int numberOfGuests) {
        return ofOrderTable(null, orderTableId, numberOfGuests, false);
    }

    private OrderTable ofOrderTable(long tableGroupId, long orderTableId, int numberOfGuests) {
        return ofOrderTable(tableGroupId, orderTableId, numberOfGuests, false);
    }

    private OrderTable ofOrderTable(Long tableGroupId, long orderTableId, int numberOfGuests, boolean empty) {
        OrderTable orderTable = new OrderTable();
        orderTable.setTableGroupId(tableGroupId);
        orderTable.setId(orderTableId);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(empty);
        return orderTable;
    }
}