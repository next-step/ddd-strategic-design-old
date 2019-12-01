package camp.nextstep.edu.kitchenpos.bo;

import static camp.nextstep.edu.kitchenpos.bo.MockBuilder.mockEmptyOrderTable;
import static camp.nextstep.edu.kitchenpos.bo.MockBuilder.mockNotEmptyOrderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import camp.nextstep.edu.kitchenpos.model.TableGroup;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TableGroupBoTest {

    @InjectMocks
    private TableGroupBo tableGroupBo;
    @Mock
    private OrderDao orderDao;
    @Mock
    private OrderTableDao orderTableDao;
    @Mock
    private TableGroupDao tableGroupDao;

    @DisplayName("테이블 그룹을 생성할 수 있다")
    @Test
    void create() {
        //given
        OrderTable orderTable_1 = mockNotEmptyOrderTable(1000L);
        OrderTable orderTable_2 = mockNotEmptyOrderTable(2000L);

        List<OrderTable> orderTables = Arrays.asList(orderTable_1, orderTable_2);
        TableGroup request = new TableGroup();
        request.setOrderTables(orderTables);

        when(orderTableDao.findAllByIdIn(any())).thenReturn(orderTables);

        when(tableGroupDao.save(any())).thenAnswer(invocation -> {
            TableGroup created = invocation.getArgument(0);
            created.setId(200L);
            return created;
        });
        when(orderTableDao.save(any())).thenAnswer(invocation -> {
            OrderTable created = invocation.getArgument(0);
            created.setId(200L);
            return created;
        });

        //when
        TableGroup result = tableGroupBo.create(request);
        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getCreatedDate()).isNotNull();
        assertThat(result.getOrderTables())
            .allMatch(it -> it.getTableGroupId().equals(result.getId()));
    }

    @DisplayName("2개 이하 주문 테이블 목록이 주어졌을 때 테이블 그룹 생성 실패")
    @Test
    void given_number_of_order_tables_smaller_than_2_then_create_table_group_fail() {
        //given
        OrderTable orderTable_1 = mockNotEmptyOrderTable(1000L);

        List<OrderTable> orderTables = Arrays.asList(orderTable_1);
        TableGroup request = new TableGroup();
        request.setOrderTables(orderTables);

        //then
        assertThatIllegalArgumentException().isThrownBy(() ->
            tableGroupBo.create(request)
        );
    }

    @DisplayName("포함할 주문 테이블이 하나라도 빈 상태이면 테이블 그룹 생성 실패")
    @Test
    void given_any_order_table_is_empty_then_create_table_group_fail() {
        //given
        List<OrderTable> orderTables = Arrays.asList(
            mockNotEmptyOrderTable(1000L),
            mockEmptyOrderTable(2000L)
        );
        when(orderTableDao.findAllByIdIn(any())).thenReturn(orderTables);

        TableGroup request = new TableGroup();
        request.setOrderTables(orderTables);

        //then
        assertThatIllegalArgumentException().isThrownBy(() ->
            tableGroupBo.create(request)
        );
    }

    @DisplayName("포함할 주문 테이블이 하나라도 다른 테이블 그룹에 속해있는 경우 테이블 그룹 생성 실패")
    @Test
    void given_any_order_table_already_has_table_group_then_create_table_group_fail() {
        //given
        OrderTable orderTableHavingTableGroup = mockNotEmptyOrderTable(1000L);
        orderTableHavingTableGroup.setTableGroupId(300L);

        List<OrderTable> orderTables = Arrays.asList(
            mockNotEmptyOrderTable(2000L),
            orderTableHavingTableGroup
        );
        when(orderTableDao.findAllByIdIn(any())).thenReturn(orderTables);

        TableGroup request = new TableGroup();
        request.setOrderTables(orderTables);

        //then
        assertThatIllegalArgumentException().isThrownBy(() ->
            tableGroupBo.create(request)
        );
    }

    @DisplayName("특정 테이블 그룹을 삭제할 수 있다")
    @Test
    void delete() {
        //given
        Long tableGroupId = 100L;
        List<OrderTable> savedOrderTables = Arrays.asList(
            mockNotEmptyOrderTable(1L),
            mockNotEmptyOrderTable(2L));
        when(orderTableDao.findAllByTableGroupId(eq(tableGroupId))).thenReturn(savedOrderTables);

        when(orderDao.existsByOrderTableIdAndOrderStatusIn(anyLong(), any()))
            .thenReturn(false);
        //when
        tableGroupBo.delete(tableGroupId);

        //then
        assertThat(savedOrderTables).allMatch(x -> Objects.isNull(x.getTableGroupId()));

    }

    @DisplayName("주문 테이블이 한 개도 없을 때 특정 테이블 그룹 삭제 실패")
    @Test
    void given_order_table_is_empty_then_delete_table_group_fails() {
        //given
        Long tableGroupId = 100L;
        List<OrderTable> emptyOrderTables = Collections.emptyList();
        when(orderTableDao.findAllByTableGroupId(eq(tableGroupId))).thenReturn(emptyOrderTables);

        //then
        assertThatIllegalArgumentException().isThrownBy(() ->
            tableGroupBo.delete(tableGroupId)
        );
    }

    @DisplayName("삭제하려는 테이블 그룹에 속하는 주문 테이블 중"
        + " ID가 가장 작은 주문테이블에 요리중/식사중인 주문이 있는 경우 "
        + " 특정 테이블 그룹 삭제 실패")
    @Test
    void given_any_order_table_is_not_completed_then_delete_table_group_fails() {
        //given
        Long tableGroupId = 100L;
        OrderTable queriedFirstOrderTable = mockNotEmptyOrderTable(1L);
        List<OrderTable> savedOrderTables = Arrays.asList(queriedFirstOrderTable);
        when(orderTableDao.findAllByTableGroupId(eq(tableGroupId))).thenReturn(savedOrderTables);

        when(orderDao
            .existsByOrderTableIdAndOrderStatusIn(eq(queriedFirstOrderTable.getId()), any()))
            .thenReturn(true);
        //then
        assertThatIllegalArgumentException().isThrownBy(() ->
            tableGroupBo.delete(tableGroupId)
        );
    }
}