package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.model.OrderStatus;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableBoTest {

    @InjectMocks
    private TableBo tableBo;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @DisplayName("등록정보를 입력하지 않은 경우 테이블 등록 시 기본정보로 손님 인원은 0명, 테이블 상태는 채워짐으로 등록한다")
    @Test
    void createWhenDefaultSaveInfo() {
        // given
        OrderTable orderTable = new OrderTable();
        given(orderTableDao.save(orderTable)).willReturn(orderTable);

        // when
        OrderTable actual = tableBo.create(orderTable);

        // then
        assertAll(
                () -> assertThat(actual.getNumberOfGuests()).isEqualTo(0),
                () -> assertThat(actual.isEmpty()).isFalse()
        );
    }

    @DisplayName("테이블 등록 시 성공한다")
    @Test
    void create() {
        // given
        OrderTable orderTable = ofOrderTable(4, true);
        given(orderTableDao.save(orderTable)).willReturn(orderTable);

        // when
        OrderTable actual = tableBo.create(orderTable);


        // then
        assertAll(
                () -> assertThat(actual.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests()),
                () -> assertThat(actual.isEmpty()).isTrue()
        );
    }

    @DisplayName("등록된 모든 테이블을 조회한다")
    @Test
    void list() {
        // given
        List<OrderTable> orderTables = Arrays.asList(ofOrderTable(0, false),
                                                     ofOrderTable(4, true));
        given(orderTableDao.findAll()).willReturn(orderTables);

        // when
        List<OrderTable> actual = tableBo.list();

        // then
        assertThat(actual).hasSize(2);
    }

    @DisplayName("테이블의 상태를 변경한다")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void changeEmpty(boolean changedTableStatus) {
        // given
        long orderTableId = 1L;
        OrderTable orderTable = ofOrderTable(orderTableId, 4, changedTableStatus);

        given(orderTableDao.findById(orderTableId)).willReturn(Optional.of(orderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(orderTableId,
                                                            Arrays.asList(OrderStatus.COOKING.name(),
                                                                          OrderStatus.MEAL.name()))).willReturn(false);
        given(orderTableDao.save(orderTable)).willReturn(orderTable);


        // when
        OrderTable actual = tableBo.changeEmpty(orderTableId, orderTable);

        // then
        assertThat(actual.isEmpty()).isEqualTo(changedTableStatus);
    }

    @DisplayName("미등록된 테이블인 경우 테이블의 상태로 변경 할 수 없다")
    @Test
    void changeEmptyWhenNotFoundOrderTable_exception() {
        // given
        long orderTableId = 1L;
        OrderTable orderTable = new OrderTable();
        orderTable.setTableGroupId(1L);

        given(orderTableDao.findById(orderTableId)).willReturn(Optional.empty());


        // exception
        assertThatIllegalArgumentException().isThrownBy(() -> tableBo.changeEmpty(orderTableId, orderTable));
    }

    @DisplayName("테이블이 그룹에 포함되는 경우 테이블의 상태를 변경 할 수 없다")
    @Test
    void changeEmptyWhenIncludeTableGroup_exception() {
        // given
        long orderTableId = 1L;
        OrderTable orderTable = new OrderTable();
        orderTable.setTableGroupId(1L);

        given(orderTableDao.findById(orderTableId)).willReturn(Optional.of(orderTable));

        // exception
        assertThatIllegalArgumentException().isThrownBy(() -> tableBo.changeEmpty(orderTableId, orderTable));
    }

    @DisplayName("테이블이 식사 완료하지 않은 경우 테이블 상태를 변경 할 수 없다")
    @Test
    void changeEmptyWhenDoesNotCompleteOrderStatus_exception() {
        // given
        long orderTableId = 1L;
        OrderTable orderTable = new OrderTable();

        given(orderTableDao.findById(orderTableId)).willReturn(Optional.of(orderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(orderTableId,
                                                            Arrays.asList(OrderStatus.COOKING.name(),
                                                                          OrderStatus.MEAL.name()))).willReturn(true);
        // exception
        assertThatIllegalArgumentException().isThrownBy(() -> tableBo.changeEmpty(orderTableId, orderTable));
    }

    @DisplayName("테이블의 손님 인원을 변경한다")
    @Test
    void changeNumberOfGuests() {
        // given
        long orderTableId = 1L;
        int changedNumberOfGuests = 4;
        OrderTable orderTable = ofOrderTable(changedNumberOfGuests, false);

        given(orderTableDao.findById(orderTableId)).willReturn(Optional.of(orderTable));
        given(orderTableDao.save(orderTable)).willReturn(orderTable);

        // when
        OrderTable actual = tableBo.changeNumberOfGuests(orderTableId, orderTable);

        // then
        assertThat(actual.getNumberOfGuests()).isEqualTo(changedNumberOfGuests);
    }

    @DisplayName("테이블의 (손님)인원 수는 0명이상만 변경이 가능하다")
    @Test
    void changeNumberOfGuestsWhenChangedNumberOfGuestsLessThanZero_exception() {
        // given
        long orderTableId = 1L;
        int changedNumberOfGuests = -1;
        OrderTable orderTable = ofOrderTable(changedNumberOfGuests, false);

        // exception
        assertThatIllegalArgumentException().isThrownBy(() -> tableBo.changeNumberOfGuests(orderTableId, orderTable));
    }

    @DisplayName("등록된 테이블이 아닌경우 테이블 상태를 변경 할 수 없다")
    @Test
    void changeNumberOfGuestsWhenNotFoundOrderTable_exception() {
        // given
        long orderTableId = 1L;
        int changedNumberOfGuests = 0;
        OrderTable orderTable = ofOrderTable(changedNumberOfGuests, false);

        given(orderTableDao.findById(orderTableId)).willReturn(Optional.empty());

        // exception
        assertThatIllegalArgumentException().isThrownBy(() -> tableBo.changeNumberOfGuests(orderTableId, orderTable));
    }

    @DisplayName("테이블이 비었다면 테이블 상태를 변경 할 수 없다")
    @Test
    void changeNumberOfGuestsWhenTableEmpty_exception() {
        // given
        long orderTableId = 1L;
        boolean emptyOfOrderTable = true;
        OrderTable orderTable = ofOrderTable(0, emptyOfOrderTable);

        given(orderTableDao.findById(orderTableId)).willReturn(Optional.of(orderTable));

        // exception
        assertThatIllegalArgumentException().isThrownBy(() -> tableBo.changeNumberOfGuests(orderTableId, orderTable));
    }

    private OrderTable ofOrderTable(int numberOfGuests, boolean empty) {
        return ofOrderTable(0L, numberOfGuests, empty);
    }

    private OrderTable ofOrderTable(long orderTableId, int numberOfGuests, boolean empty) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(orderTableId);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(empty);
        return orderTable;
    }
}