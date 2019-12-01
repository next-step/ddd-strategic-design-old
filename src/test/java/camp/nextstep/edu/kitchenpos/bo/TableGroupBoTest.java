package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import camp.nextstep.edu.kitchenpos.model.TableGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class TableGroupBoTest {

    private List<OrderTable> orderTables;
    private List<OrderTable> savedOrderTables;

    @Mock
    private OrderTable savedOrderTable;

    @Mock
    private TableGroup tableGroup;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @InjectMocks
    private TableGroupBo bo;

    @BeforeEach
    void setUp() {
        OrderTable orderTable1 = new OrderTable();
        orderTable1.setId(1L);
        orderTable1.setNumberOfGuests(5);

        OrderTable orderTable2 = new OrderTable();
        orderTable2.setId(2L);
        orderTable2.setNumberOfGuests(5);

        orderTables = Arrays.asList(orderTable1, orderTable2);

        savedOrderTables = Arrays.asList(savedOrderTable, savedOrderTable);
    }

    @DisplayName("생성하려는 메뉴그룹에 포함된 테이블이 없으면 해당 메뉴그룹을 생성할 수 없다")
    @Test
    void createTest_nullTable() {
        when(tableGroup.getOrderTables()).thenReturn(null);

        assertThatThrownBy(() -> bo.create(tableGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("생성하려는 메뉴그룹에 포함된 테이블이 2개 미만이면 해당 메뉴그룹을 생성할 수 없다")
    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void createTest_zeroOrOneTable(int numberOfTables) {
        when(tableGroup.getOrderTables())
                .thenReturn(Arrays.asList(new OrderTable[numberOfTables]));

        assertThatThrownBy(() -> bo.create(tableGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("생성하려는 메뉴그룹에 포함된 테이블이 비움처리 되어있다면 해당 메뉴그룹을 생성할 수 없다")
    @Test
    void createTest_emptyTable() {
        when(tableGroup.getOrderTables()).thenReturn(orderTables);
        when(orderTableDao.findAllByIdIn(any())).thenReturn(savedOrderTables);
        when(savedOrderTable.isEmpty()).thenReturn(true);

        assertThatThrownBy(() -> bo.create(tableGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("생성하려는 메뉴그룹에 포함된 테이블이 이미 등록된 메뉴그룹에 속한다면 해당 메뉴그룹을 생성할 수 없다")
    @Test
    void createTest_tableWithTableGroup() {
        when(tableGroup.getOrderTables()).thenReturn(orderTables);
        when(orderTableDao.findAllByIdIn(any())).thenReturn(savedOrderTables);
        when(savedOrderTable.isEmpty()).thenReturn(false);
        when(savedOrderTable.getTableGroupId()).thenReturn(1L);

        assertThatThrownBy(() -> bo.create(tableGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("삭제하려는 메뉴그룹에 포함된 테이블이 하나도 없다면 해당 메뉴그룹을 삭제할 수 없다")
    @Test
    void deleteTest_nonExistOrderTable() {
        when(orderTableDao.findAllByTableGroupId(1L)).thenReturn(new ArrayList<>());

        assertThatThrownBy(() -> bo.delete(1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("삭제하려는 메뉴그룹에 포함된 테이블에서 일어난 주문의 주문진행상태가 요리중이거나 식사중이면 해당 메뉴그룹을 삭제할 수 없다")
    @Test
    void deleteTest_orderOfCookingOrMeal() {
        when(orderTableDao.findAllByTableGroupId(1L)).thenReturn(savedOrderTables);
        when(orderDao.existsByOrderTableIdAndOrderStatusIn(any(), anyList()))
                .thenReturn(true);

        assertThatThrownBy(() -> bo.delete(1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴그룹 삭제 성공")
    @Test
    void deleteTest_basic() {
        when(orderTableDao.findAllByTableGroupId(1L)).thenReturn(savedOrderTables);
        when(orderDao.existsByOrderTableIdAndOrderStatusIn(any(), anyList()))
                .thenReturn(false);
        when(orderTableDao.save(savedOrderTable)).thenReturn(savedOrderTable);

        bo.delete(1L);

        int numberOfTables = savedOrderTables.size();

        verify(savedOrderTable, times(numberOfTables)).setTableGroupId(null);
        verify(orderTableDao, times(numberOfTables)).save(savedOrderTable);
    }
}
