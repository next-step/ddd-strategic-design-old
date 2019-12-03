package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TableBoTest {
    @Mock
    private OrderDao orderDao;
    @Mock
    private OrderTableDao orderTableDao;
    @Mock
    private TableGroupDao tableGroupDao;
    @InjectMocks
    private TableBo bo;
    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setNumberOfGuests(4);
        orderTable.setEmpty(false);
    }

    @DisplayName("새로운 주문 테이블을 생성한다.")
    @Test
    void createNewOrderTable() {
        given(orderTableDao.save(any())).willReturn(new OrderTable());

        OrderTable savedOrderTable = bo.create(any());

        assertThat(savedOrderTable).isNotNull();
    }

    @DisplayName("모든 주문 테이블 리스트를 조회한다.")
    @Test
    void getListAllOrderTables() {
        given(orderTableDao.findAll())
                .willReturn(Collections.singletonList(new OrderTable()));

        List<OrderTable> foundOrderTables = bo.list();

        assertThat(foundOrderTables).hasSize(1);
    }

    @DisplayName("존재하지 않는 주문 테이블을 빈 테이블로 변경할 수 없다")
    @Test
    void canNotChangeEmpty_withEmptyOrderTable() {
        lenient().when(orderTableDao.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> bo.changeEmpty(orderTable.getId(), orderTable));
    }

    @DisplayName("테이블 그룹을 가지고 있는 주문 테이블은 빈 주문 테이블로 변경할 수 없다.")
    @Test
    void canNotChangeEmpty_havingTableGroup() {
        lenient().when(orderTableDao.findById(anyLong()))
                .thenReturn(Optional.of(orderTable));

        orderTable.setTableGroupId(anyLong());

        assertThatIllegalArgumentException().isThrownBy(() -> {
            bo.changeEmpty(orderTable.getId(), orderTable);
        });

//        assertThat(orderTable.getTableGroupId()).isNotNull();
//        assertThat(orderTable.getTableGroupId()).isPositive();
//        assertThat(orderTable.getTableGroupId()).isEqualTo(1L);

//        OrderTable changeEmpty = bo.changeEmpty(orderTable.getId(), orderTable);

//        assertThat(changeEmpty.isEmpty()).isTrue();
    }

    @DisplayName("테이블 그룹을 가지고 있지 않고, 주문 상태가 조리중이거나 식사중이 아닌" +
            " 주문 테이블은 빈 주문 테이블로 변경할 수 있다.")
    @Test
    void canChangeEmpty_haveNotTableGroup() {
        lenient()
                .when(orderTableDao.findById(anyLong()))
                .thenReturn(Optional.of(orderTable));
        lenient()
                .when(orderDao.existsByOrderTableIdAndOrderStatusIn(anyLong(), anyList()))
                .thenReturn(false);
        orderTable.setEmpty(true);
        when(orderTableDao.save(any())).thenReturn(orderTable);

        OrderTable changedOrderTable = bo.changeEmpty(orderTable.getId(), orderTable);

        assertThat(changedOrderTable.isEmpty()).isTrue();
    }

    @DisplayName("테이블의 손님 수가 0 명보다 적으면 변경 할 수 없다.")
    @Test
    void canNotChangeNegativeNumberOfGuests() {
        orderTable.setNumberOfGuests(-1);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> {
                    bo.changeNumberOfGuests(orderTable.getId(), orderTable);
                });
    }

    @DisplayName("해당 테이블이 존재하지 않으면 손님 수 를 변경 할 수 없다.")
    @Test
    void canNotChange_whenNotFoundOrderTable() {
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.empty());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> {
                    bo.changeNumberOfGuests(orderTable.getId(), orderTable);
                });
    }

    @DisplayName("테이블의 손님 수를 변경 할 수 있다.")
    @Test
    void changeNumberOfGuests() {
        assertThat(orderTable.getNumberOfGuests()).isEqualTo(4);
        lenient()
                .when(orderTableDao.findById(anyLong()))
                .thenReturn(Optional.of(orderTable));
        orderTable.setNumberOfGuests(2);
        given(orderTableDao.save(any())).willReturn(orderTable);

        OrderTable changedOrderTable = bo.changeNumberOfGuests(anyLong(), orderTable);

        assertThat(changedOrderTable.getNumberOfGuests()).isEqualTo(2);
    }
}
