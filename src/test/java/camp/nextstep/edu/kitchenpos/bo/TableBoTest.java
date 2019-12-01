package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class TableBoTest {

    private OrderTable orderTable;

    @Mock
    private OrderTable savedOrderTable;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @InjectMocks
    private TableBo bo;

    @BeforeEach
    void setUp() {
        orderTable = new OrderTable();
    }

    @DisplayName("등록되지 않은 테이블의 비움 여부를 변경할 수 없다")
    @Test
    void changeEmpty_nonExistOrderTable() {
        when(orderTableDao.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bo.changeEmpty(1L, orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("특정 테이블에 속하는 테이블은 비움 여부를 변경할 수 업다")
    @Test
    void changeEmpty_nonNullTableGroup() {
        when(savedOrderTable.getTableGroupId()).thenReturn(1L);
        when(orderTableDao.findById(anyLong())).thenReturn(Optional.of(savedOrderTable));

        assertThatThrownBy(() -> bo.changeEmpty(1L, orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블의 주문이 있고, 그 주문의 주문 진행 상태가 요리 중이거나 식사 중이라면, 테이블의 비움 여부를 변경할 수 없다")
    @Test
    void changeEmpty_orderCookingOrMeal() {
        when(savedOrderTable.getTableGroupId()).thenReturn(null);
        when(orderTableDao.findById(anyLong())).thenReturn(Optional.of(savedOrderTable));
        when(orderDao.existsByOrderTableIdAndOrderStatusIn(anyLong(), anyList()))
                                                            .thenReturn(true);

        assertThatThrownBy(() -> bo.changeEmpty(1L, orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블의 비움 여부 변경 성공")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void changeEmpty_basic(boolean isEmpty) {
        orderTable.setEmpty(isEmpty);

        when(savedOrderTable.getTableGroupId()).thenReturn(null);
        when(orderTableDao.findById(anyLong())).thenReturn(Optional.of(savedOrderTable));
        when(orderDao.existsByOrderTableIdAndOrderStatusIn(anyLong(), anyList()))
                                                            .thenReturn(false);
        when(orderTableDao.save(savedOrderTable)).thenReturn(savedOrderTable);

        bo.changeEmpty(1L, orderTable);

        verify(savedOrderTable, times(1)).setEmpty(isEmpty);
        verify(orderTableDao, times(1)).save(savedOrderTable);
    }

    @DisplayName("테이블 손님 수를 음수로 변경할 수 없다")
    @ParameterizedTest
    @ValueSource(ints = {-1, -2})
    void changeNumberOfGuests_withNegativeGuest(int numberOfGuests) {
        orderTable.setNumberOfGuests(numberOfGuests);

        assertThatThrownBy(() -> bo.changeNumberOfGuests(1L, orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("이미 등록된 테이블만 손님 수를 변경할 수 있다")
    @Test
    void changeNumberOfGuests_nonExistOrderTable() {
        orderTable.setNumberOfGuests(1);

        when(orderTableDao.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bo.changeNumberOfGuests(1L, orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블의 비움 처리되어 있으면, 손님 수를 변경할 수 없다")
    @Test
    void changeNumberOfGuests_emptyOrderTable() {
        orderTable.setNumberOfGuests(1);

        when(savedOrderTable.isEmpty()).thenReturn(true);
        when(orderTableDao.findById(anyLong())).thenReturn(Optional.of(savedOrderTable));

        assertThatThrownBy(() -> bo.changeNumberOfGuests(1L, orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블의 손님 수 변경 성공")
    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void changeNumberOfGuests_basic(int numberOfGuests) {
        orderTable.setNumberOfGuests(numberOfGuests);

        when(savedOrderTable.isEmpty()).thenReturn(false);
        when(orderTableDao.findById(anyLong())).thenReturn(Optional.of(savedOrderTable));
        when(orderTableDao.save(savedOrderTable)).thenReturn(savedOrderTable);

        bo.changeNumberOfGuests(1L, orderTable);

        verify(savedOrderTable, times(1)).setNumberOfGuests(numberOfGuests);
        verify(orderTableDao, times(1)).save(savedOrderTable);
    }

}
