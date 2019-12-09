package camp.nextstep.edu.kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TableBoTest {

    private static final Long ORDER_TABLE_ID = 1L;
    private static final Long TABLE_GROUP_ID = 2L;

    @Mock
    private OrderTable orderTable;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private OrderDao orderDao;

    @InjectMocks
    private TableBo tableBo;

    @DisplayName("주문테이블을 생성한다.")
    @Test
    void create_success() {
        // Given
        int numberOfGuests = 5;
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(ORDER_TABLE_ID);
        orderTable.setTableGroupId(TABLE_GROUP_ID);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(false);

        given(orderTableDao.save(orderTable)).willReturn(orderTable);

        // When
        final OrderTable saveOrderTable = tableBo.create(orderTable);

        // Then
        assertAll(
                () -> assertThat(saveOrderTable.getId()).isEqualTo(ORDER_TABLE_ID),
                () -> assertThat(saveOrderTable.getTableGroupId()).isEqualTo(TABLE_GROUP_ID),
                () -> assertThat(saveOrderTable.getNumberOfGuests()).isEqualTo(numberOfGuests),
                () -> assertThat(saveOrderTable.isEmpty()).isEqualTo(false));
    }

    @DisplayName("주문테이블 목록을 조회할 수 있다")
    @Test
    void orderTableList() {
        // Given
        given(orderTableDao.findAll()).willReturn(Arrays.asList(orderTable, orderTable, orderTable));

        // When
        final List<OrderTable> orderTableList = tableBo.list();

        // Then
        assertThat(orderTableList).hasSize(3);
    }

    @DisplayName("주문테이블을 비울시에 테이블그룹에 속하면 예외를 발생 한다.")
    @Test
    void changeEmpty_hasTableGroup() {
        // Given
        given(orderTableDao.findById(ORDER_TABLE_ID)).willReturn(Optional.of(orderTable));
        given(orderTable.getTableGroupId()).willReturn(TABLE_GROUP_ID);

        // When
        // Then
        assertThatThrownBy(() -> tableBo.changeEmpty(ORDER_TABLE_ID, orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문테이블을 비울시에 주문상태가 조리중이거나 식사중이면 예외를 발생 한다.")
    @Test
    void changeEmpty_orderStatusIsCookingOrMeal() {
        // Given
        given(orderTableDao.findById(ORDER_TABLE_ID)).willReturn(Optional.of(orderTable));
        given(orderTable.getTableGroupId()).willReturn(null);
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(eq(ORDER_TABLE_ID), anyList())).willReturn(true);

        // When
        // Then
        assertThatThrownBy(() -> tableBo.changeEmpty(ORDER_TABLE_ID, orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문테이블을 비운다.")
    @Test
    void changeEmpty_success() {
        // Given
        given(orderTableDao.findById(ORDER_TABLE_ID)).willReturn(Optional.of(orderTable));
        given(orderTable.getTableGroupId()).willReturn(null);
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(eq(ORDER_TABLE_ID), anyList())).willReturn(false);
        given(orderTable.isEmpty()).willReturn(true);
        given(orderTableDao.save(orderTable)).willReturn(orderTable);

        // When
        OrderTable saveOrderTable = tableBo.changeEmpty(ORDER_TABLE_ID, orderTable);

        // Then
        assertThat(saveOrderTable.isEmpty()).isTrue();
    }

    @DisplayName("주문테이블에 손님수를 변경시에 손님수가 0이하이면 경우 예외를 발생 한다.")
    @Test
    void changeNumberOfGuests_numberOfGuestsIsNegative() {
        // Given
        final int negativeNumberOfGuests = -1;
        given(orderTable.getNumberOfGuests()).willReturn(negativeNumberOfGuests);

        // When
        // Then
        assertThatThrownBy(() -> tableBo.changeNumberOfGuests(ORDER_TABLE_ID, orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문테이블에 손님수를 변경시에 주문테이블이 비어있으면 예외를 발생 한다.")
    @Test
    void changeNumberOfGuests_orderTableIsEmpty() {
        // Given
        final int numberOfGuests = 5;
        given(orderTable.getNumberOfGuests()).willReturn(numberOfGuests);
        given(orderTableDao.findById(ORDER_TABLE_ID)).willReturn(Optional.of(orderTable));
        given(orderTable.isEmpty()).willReturn(true);

        // When
        // Then
        assertThatThrownBy(() -> tableBo.changeNumberOfGuests(ORDER_TABLE_ID, orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문테이블에 손님수를 변경한다.")
    @Test
    void changeNumberOfGuests_success() {
        // Given
        final int numberOfGuests = 5;
        given(orderTable.getNumberOfGuests()).willReturn(numberOfGuests);
        given(orderTableDao.findById(ORDER_TABLE_ID)).willReturn(Optional.of(orderTable));
        given(orderTable.isEmpty()).willReturn(false);
        given(orderTableDao.save(orderTable)).willReturn(orderTable);

        // When
        OrderTable saveOrderTable = tableBo.changeNumberOfGuests(ORDER_TABLE_ID, orderTable);

        // Then
        assertThat(saveOrderTable.getNumberOfGuests()).isEqualTo(numberOfGuests);
    }
}
