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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableBoTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @InjectMocks
    private TableBo tableBo;

    @DisplayName("주문테이블을 생성할 수 있다")
    @Test
    void createSuccess() {
        // given
        OrderTable expected = makeOrderTable(0L, 0L, false, 5);

        given(orderTableDao.save(any())).willReturn(expected);

        // when
        OrderTable actual = tableBo.create(expected);

        // then
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getTableGroupId()).isEqualTo(expected.getTableGroupId());
        assertThat(actual.isEmpty()).isEqualTo(expected.isEmpty());
        assertThat(actual.getNumberOfGuests()).isEqualTo(expected.getNumberOfGuests());
    }

    @DisplayName("주문테이블 목록을 조회할 수 있다")
    @Test
    void list() {
        // given
        OrderTable orderTable1 = makeOrderTable(0L, 0L, false, 5);
        OrderTable orderTable2 = makeOrderTable(1L, 0L, false, 5);

        List<OrderTable> orderTables = Arrays.asList(orderTable1, orderTable2);

        given(orderTableDao.findAll()).willReturn(orderTables);

        // when
        assertThat(tableBo.list()).hasSize(2);
    }

    @DisplayName("주문테이블의 상태를 빈 상태로 변경할 수 있다")
    @Test
    void changeSuccess_tableStatus() {
        // given
        OrderTable orderTable = makeOrderTable(0L, null, false, 5);
        OrderTable expected = makeOrderTable(0L, null, true, 5);

        given(orderTableDao.findById(any())).willReturn(Optional.of(orderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(any(Long.class), any(List.class))).willReturn(false);
        given(orderTableDao.save(any())).willReturn(expected);

        // when
        OrderTable result = tableBo.changeEmpty(0L, expected);

        // then
        assertThat(result.isEmpty()).isEqualTo(expected.isEmpty());
    }

    @DisplayName("존재하지 않는 주문테이블번호에 대해서는 주문테이블을 빈상태로 변경할 수 없다")
    @Test
    void changeFail_orderTableNotExist() {
        // given
        OrderTable orderTable = makeOrderTable(0L, null, false, 5);

        given(orderTableDao.findById(any())).willReturn(Optional.ofNullable(null));

        // when
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            tableBo.changeEmpty(0L, orderTable);
        });
    }

    @DisplayName("주문의 상태가 요리중이거나 식사중인 경우 주문테이블을 빈상태로 변경할 수 없다")
    @Test
    void changeFail_statusIsCookingOrMeal() {
        // given
        OrderTable orderTable = makeOrderTable(0L, null, false, 5);

        given(orderTableDao.findById(any())).willReturn(Optional.of(orderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(any(Long.class), any(List.class))).willReturn(true);

        // when
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            tableBo.changeEmpty(0L, orderTable);
        });
    }

    @DisplayName("주문테이블의 손님 수를 변경할 수 있다")
    @Test
    void changeSuccess_numberOfGuests() {
        // given
        long orderTableId = 0L;
        OrderTable orderTable = makeOrderTable(orderTableId, 0L, false, 3);
        OrderTable expected = makeOrderTable(orderTableId, 0L, false, 5);

        given(orderTableDao.findById(any(Long.class))).willReturn(Optional.ofNullable(orderTable));
        given(orderTableDao.save(any())).willReturn(expected);

        // when
        OrderTable actual = tableBo.changeNumberOfGuests(orderTableId, expected);

        // then
        assertThat(actual.getNumberOfGuests()).isEqualTo(expected.getNumberOfGuests());
    }

    @DisplayName("주문테이블의 손님 수는 음수로 변경할 수 없다")
    @Test
    void changeFail_numberOfGuestIsNegative() {
        // given
        long orderTableId = 0L;
        int negativeNumber = -1;

        OrderTable expected = makeOrderTable(orderTableId, 0L, false, negativeNumber);

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> {
            tableBo.changeNumberOfGuests(orderTableId, expected);
        });
    }

    @DisplayName("존재하지 않는 주문테이블번호에 대해서는 주문테이블 손님 수를 변경할 수 없다")
    @Test
    void changeFail_numberOfGuests_orderTableNotExist() {
        // given
        long orderTableId = 0L;

        OrderTable expected = makeOrderTable(0L, 0L, false, 3);

        given(orderTableDao.findById(any(Long.class))).willReturn(Optional.ofNullable(null));

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> {
            tableBo.changeNumberOfGuests(orderTableId, expected);
        });
    }

    @DisplayName("주문테이블이 비어있는 경우는 손님 수를 변경할 수 없다")
    @Test
    void changeFail_orderTableisEmpty() {
        // given
        long orderTableId = 0L;

        OrderTable expected = makeOrderTable(0L, 0L, true, 3);

        given(orderTableDao.findById(any(Long.class))).willReturn(Optional.ofNullable(null));

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> {
            tableBo.changeNumberOfGuests(orderTableId, expected);
        });
    }

    public OrderTable makeOrderTable(Long id, Long tableGroupId,
                                     boolean empty, int numberOfGuests) {

        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setTableGroupId(tableGroupId);
        orderTable.setEmpty(empty);
        orderTable.setNumberOfGuests(numberOfGuests);

        return orderTable;
    }
}