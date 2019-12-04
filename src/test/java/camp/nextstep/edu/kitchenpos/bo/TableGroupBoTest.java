package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import camp.nextstep.edu.kitchenpos.model.TableGroup;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableGroupBoTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @InjectMocks
    private TableGroupBo tableGroupBo;

    @DisplayName("테이블그룹을 생성할 수 있다")
    @Test
    void createSuccess() {
        // given
        OrderTable orderTable1 = new OrderTable();
        orderTable1.setId(0L);
        orderTable1.setEmpty(true);
        orderTable1.setTableGroupId(null);

        OrderTable orderTable2 = new OrderTable();
        orderTable2.setId(0L);
        orderTable2.setEmpty(true);
        orderTable2.setTableGroupId(null);

        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);

        TableGroup expected = makeTableGroup(0L, LocalDateTime.now(), orderTables);

        given(tableGroupDao.save(any())).willReturn(expected);

        // when
        TableGroup actual = tableGroupBo.create(expected);

        // then
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getOrderTables())
                .hasSize(expected.getOrderTables().size());
        assertThat(actual.getCreatedDate()).isEqualTo(expected.getCreatedDate());
    }

    @DisplayName("테이블그룹에 속한 주문테이블들이 두개 이상이거나 존재하지 않는 경우 테이블그룹을 생성할 수 없다")
    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void createFail_orderTablesNotExistAndSizeBelowTwo(int size) {
        // given
        List<OrderTable> orderTables = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            orderTables.add(new OrderTable());
        }

        TableGroup tableGroup = makeTableGroup(0L, LocalDateTime.now(), orderTables);

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> {
            tableGroupBo.create(tableGroup);
        });
    }

    @DisplayName("테이블그룹에 속한 주문테이블 중, 비어있는 테이블이 있으면 테이블그룹을 생성할 수 없다")
    @Test
    void createFail_orderTableIsEmpty() {
        // given
        OrderTable orderTable1 = new OrderTable();
        orderTable1.setId(0L);
        orderTable1.setEmpty(false);

        OrderTable orderTable2 = new OrderTable();
        orderTable2.setId(1L);
        orderTable2.setEmpty(true);

        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);

        TableGroup tableGroup = makeTableGroup(0L, LocalDateTime.now(),
                orderTables);

        given(orderTableDao.findAllByIdIn(any())).willReturn(orderTables);

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> {
           tableGroupBo.create(tableGroup);
        });
    }

    @DisplayName("테이블그룹에 속한 주문테이블 중, 이미 테이블그룹이 지정된 경우는 테이블그룹을 생성할 수 없다")
    @Test
    void createFail_tableGroupAlreadyExist() {
        // given
        OrderTable orderTable1 = new OrderTable();
        orderTable1.setId(0L);
        orderTable1.setEmpty(false);

        OrderTable orderTable2 = new OrderTable();
        orderTable2.setId(1L);
        orderTable2.setTableGroupId(0L);
        orderTable2.setEmpty(false);

        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);

        TableGroup tableGroup = makeTableGroup(0L, LocalDateTime.now(),
                orderTables);

        given(orderTableDao.findAllByIdIn(any())).willReturn(orderTables);

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> {
            tableGroupBo.create(tableGroup);
        });
    }

    @DisplayName("테이블그룹을 삭제할 수 있다")
    @Test
    void deleteSuccess() {
        // given
        OrderTable orderTable1 = new OrderTable();
        orderTable1.setId(0L);
        orderTable1.setTableGroupId(0L);
        orderTable1.setEmpty(false);

        OrderTable orderTable2 = new OrderTable();
        orderTable2.setId(1L);
        orderTable2.setTableGroupId(0L);
        orderTable2.setEmpty(false);

        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);
        TableGroup tableGroup = makeTableGroup(0L, LocalDateTime.now(),
                orderTables);

        given(orderTableDao.findAllByTableGroupId(any())).willReturn(orderTables);
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(any(Long.class), any(List.class)))
                .willReturn(false);

        given(orderTableDao.save(any())).willReturn(any(OrderTable.class));

        // when
        tableGroupBo.delete(0L);

        // then
        assertThat(orderTables.stream()
                .filter(orderTable -> orderTable.getTableGroupId() == null).count())
                .isEqualTo(0);
    }

    @DisplayName("테이블그룹에 속한 주문테이블 중 번호가 제일 빠른 테이블이 존재하지 않으면 테이블그룹을 삭제할 수 없다")
    @Test
    void deleteFail_firstOrderTableNotExist() {
    }

    @DisplayName("주문테이블의 상태가 요리중이거나 식사중인 경우는 테이블그룹을 삭제할 수 없다")
    @Test
    void deleteFail_CookingOrMeal() {
    }

    private TableGroup makeTableGroup(Long id, LocalDateTime localDateTime, List<OrderTable> orderTables) {
        TableGroup tableGroup = new TableGroup();
        tableGroup.setId(0L);
        tableGroup.setOrderTables(orderTables);
        tableGroup.setCreatedDate(localDateTime);
        return tableGroup;
    }
}