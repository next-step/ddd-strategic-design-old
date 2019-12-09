package camp.nextstep.edu.kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import camp.nextstep.edu.kitchenpos.model.TableGroup;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TableGroupBoTest {

    private static final Long TABLE_GROUP_ID = 10L;
    private static final Long ORDER_TABLE_ID = 20L;

    @Mock
    private OrderTable orderTable;

    @Mock
    private TableGroup tableGroup;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @InjectMocks
    private TableGroupBo tableGroupBo;

    @DisplayName("테이블그룹 생성중 주문테이블이 1개 이하면 예외를 발생한다.")
    @Test
    void create_noMoreThanOneOrderTable() {
        // Given
        final List<OrderTable> orderTableList = Arrays.asList(orderTable);
        given(tableGroup.getOrderTables()).willReturn(orderTableList);

        // When
        // Then
        assertThatThrownBy(() -> tableGroupBo.create(tableGroup)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블그룹 생성중 주문테이블이 비어있으면 예외를 발생한다.")
    @Test
    void create_orderTableIsEmpty() {
        // Given
        final List<OrderTable> orderTableList = Arrays.asList(orderTable, orderTable);
        given(tableGroup.getOrderTables()).willReturn(orderTableList);
        given(orderTableDao.findAllByIdIn(anyList())).willReturn(orderTableList);
        given(orderTable.isEmpty()).willReturn(true);

        // When
        // Then
        assertThatThrownBy(() -> tableGroupBo.create(tableGroup)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블그룹 생성중 주문테이블에 이미 테이블그룹이 있으면 예외를 발생한다.")
    @Test
    void create_orderTableHasTableGroup() {
        // Given
        final List<OrderTable> orderTableList = Arrays.asList(orderTable, orderTable);
        given(tableGroup.getOrderTables()).willReturn(orderTableList);
        given(orderTableDao.findAllByIdIn(anyList())).willReturn(orderTableList);
        given(orderTable.isEmpty()).willReturn(false);
        given(orderTable.getTableGroupId()).willReturn(ORDER_TABLE_ID);

        // When
        // Then
        assertThatThrownBy(() -> tableGroupBo.create(tableGroup)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블그룹을 생성한다.")
    @Test
    void create_success() {
        // Given
        Long tableGroupId = 10L;
        TableGroup tableGroup = new TableGroup();
        tableGroup.setId(tableGroupId);

        Long firstOrderTableId = 21L;
        OrderTable firstOrderTable = new OrderTable();
        firstOrderTable.setId(firstOrderTableId);
        firstOrderTable.setEmpty(false);

        Long secondOrderTableId = 22L;
        OrderTable secondOrderTable = new OrderTable();
        secondOrderTable.setId(secondOrderTableId);
        secondOrderTable.setEmpty(false);

        List<OrderTable> orderTableList = Arrays.asList(firstOrderTable, secondOrderTable);
        tableGroup.setOrderTables(orderTableList);

        List<Long> orderTableIdList = Arrays.asList(firstOrderTableId, secondOrderTableId);
        given(orderTableDao.findAllByIdIn(orderTableIdList)).willReturn(orderTableList);
        given(tableGroupDao.save(tableGroup)).willReturn(tableGroup);

        // When
        final TableGroup saveTableGroup = tableGroupBo.create(tableGroup);

        // Then
        assertAll(
                () -> assertThat(saveTableGroup.getId()).isEqualTo(tableGroupId),
                () -> assertThat(saveTableGroup.getOrderTables()).containsExactlyInAnyOrderElementsOf(orderTableList));
    }

    @DisplayName("주문그룹을 삭제시에 주문테이블에 주문상태가 조리중이거나 식사중이면 예외를 발생 한다.")
    @Test
    void delete_orderStatusIsCookingOrMeal() {
        // Given
        given(orderTableDao.findAllByTableGroupId(TABLE_GROUP_ID)).willReturn(Arrays.asList(orderTable));
        given(orderTable.getId()).willReturn(ORDER_TABLE_ID);
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(eq(ORDER_TABLE_ID), anyList())).willReturn(true);

        // When
        // Then
        assertThatThrownBy(() -> tableGroupBo.delete(TABLE_GROUP_ID))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문그룹을 삭제시에 주문테이블에 주문상태가 조리중이거나 식사중이면 예외를 발생 한다.")
    @Test
    void delete_success() {
        // Given
        OrderTable orderTable = new OrderTable();
        orderTable.setId(ORDER_TABLE_ID);
        orderTable.setTableGroupId(TABLE_GROUP_ID);

        given(orderTableDao.findAllByTableGroupId(TABLE_GROUP_ID)).willReturn(Arrays.asList(orderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(eq(ORDER_TABLE_ID), anyList())).willReturn(false);

        // When
        tableGroupBo.delete(TABLE_GROUP_ID);

        // Then
        assertThat(orderTable.getTableGroupId()).isNull();
    }
}
