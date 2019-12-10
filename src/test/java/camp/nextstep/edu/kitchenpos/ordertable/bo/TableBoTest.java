package camp.nextstep.edu.kitchenpos.ordertable.bo;

import camp.nextstep.edu.kitchenpos.order.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.ordertable.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.order.model.OrderStatus;
import camp.nextstep.edu.kitchenpos.ordertable.model.OrderTable;
import camp.nextstep.edu.kitchenpos.ordertable.bo.TableBo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("테이블 Business Object 테스트 클래스")
@ExtendWith(MockitoExtension.class)
class TableBoTest {
    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private OrderTable orderTable;

    @InjectMocks
    private TableBo tableBo;

    private final Long DEFAULT_ID = 1L;

    @DisplayName("주문 테이블은 주문 테이블 번호, 테이블 그룹 번호, 고객 인원수, 테이블 이용여부 속성들을 가지고 있다.")
    @Test
    void hasProperties() {
        String orderTableIdPropertyName = "id";
        String tableGroupIdPropertyName = "tableGroupId";
        String numberOfGuestsPropertyName = "numberOfGuests";
        String emptyPropertyName = "empty";

        assertAll(
                () -> assertThat(orderTable).hasFieldOrProperty(orderTableIdPropertyName),
                () -> assertThat(orderTable).hasFieldOrProperty(tableGroupIdPropertyName),
                () -> assertThat(orderTable).hasFieldOrProperty(numberOfGuestsPropertyName),
                () -> assertThat(orderTable).hasFieldOrProperty(emptyPropertyName)
        );
    }

    @DisplayName("[테이블 생성] 주문 테이블을 생성할 수 있다.")
    @Test
    void create() {
        // given
        OrderTable orderTable = createOrderTable(DEFAULT_ID, null, true);
        given(orderTableDao.save(orderTable)).willReturn(orderTable);

        // when
        OrderTable savedOrderTable = tableBo.create(orderTable);

        // then
        assertAll(
                () -> assertThat(savedOrderTable).isNotNull(),
                () -> assertThat(savedOrderTable).isEqualTo(orderTable)
        );
    }

    @DisplayName("[테이블 조회] 주문 테이블을 조회할 수 있다.")
    @Test
    void list() {
        // given
        final int orderTablesSize = 2;
        List<OrderTable> orderTables = mock(List.class);
        given(orderTables.size()).willReturn(orderTablesSize);
        given(orderTableDao.findAll()).willReturn(orderTables);

        // when
        List<OrderTable> allOrderTables = tableBo.list();

        // then
        assertAll(
                () -> assertThat(allOrderTables).isNotNull(),
                () -> assertThat(allOrderTables).hasSize(orderTablesSize)
        );
    }

    @DisplayName("[주문 테이블 상태 변경] 주문 테이블이 존재하면 성공을 반환한다.")
    @Test
    void whenOrderTableIsExist_thenSuccess() {
        // when
        OrderTable orderTable = createOrderTable(DEFAULT_ID, null, true);
        when(orderTableDao.findById(DEFAULT_ID)).thenReturn(Optional.of(orderTable));
        OrderTable savedOrderTable = orderTableDao.findById(DEFAULT_ID)
                                                  .get();

        // then
        assertThat(savedOrderTable).isNotNull();
    }

    @DisplayName("[주문 테이블 상태 변경] 주문 테이블이 테이블 그룹에 속해있으면 예외를 발생 한다.")
    @Test
    void whenOrderTableHasTableGroupId_thenFail() {
        // given
        OrderTable orderTable = createOrderTable(DEFAULT_ID, DEFAULT_ID, true);
        given(orderTableDao.findById(DEFAULT_ID)).willReturn(Optional.of(orderTable));

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeEmpty(DEFAULT_ID, orderTable));
    }

    @DisplayName("[주문 테이블 상태 변경] 주문 테이블의 테이블 이용 여부를 변경 한다.")
    @Test
    void changeEmpty() {
        // given
        final List<String> orderStatuses = Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name());
        OrderTable beforeOrderTable = createOrderTable(DEFAULT_ID, null, true);
        OrderTable savedOrderTable = createOrderTable(DEFAULT_ID, null, false);

        given(orderTableDao.findById(DEFAULT_ID)).willReturn(Optional.of(savedOrderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(DEFAULT_ID, orderStatuses)).willReturn(false);
        given(orderTableDao.save(savedOrderTable)).willReturn(savedOrderTable);

        // when
        OrderTable changedOrderTable = tableBo.changeEmpty(DEFAULT_ID, beforeOrderTable);

        // then
        assertAll(
                () -> assertThat(changedOrderTable.isEmpty()).isTrue(),
                () -> assertThat(changedOrderTable.isEmpty()).isEqualTo(savedOrderTable.isEmpty())
        );

    }

    @DisplayName("[주문 테이블 인원수 변경] 고객 인원 숫자는 0명 미만 일경우 예외를 발생 한다")
    @Test
    void whenNumberOfGuestIsLessThanZero_thenFail() {
        // given
        final int numberOfGuests = -1;
        given(orderTable.getNumberOfGuests()).willReturn(numberOfGuests);

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeNumberOfGuests(DEFAULT_ID, orderTable));
    }

    @DisplayName("[주문 테이블 인원수 변경] 테이블이 비워져 있으면 예외를 발생 한다")
    @Test
    void whenOrderTableIsEmpty_thenFail() {
        // given
        final int numberOfGuests = 1;
        given(orderTable.getNumberOfGuests()).willReturn(numberOfGuests);
        given(orderTableDao.findById(DEFAULT_ID)).willReturn(Optional.of(createOrderTable(DEFAULT_ID, DEFAULT_ID, true)));

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeNumberOfGuests(DEFAULT_ID, orderTable));
    }

    @DisplayName("[주문 테이블 인원수 변경] 주문 테이블 인원수를 1명으로 변경할 수 있다.")
    @Test
    void changeNumberOfGuests() {
        final int numberOfGuests = 1;
        OrderTable savedOrderTable = createOrderTable(DEFAULT_ID, DEFAULT_ID, false);
        when(orderTable.getNumberOfGuests()).thenReturn(numberOfGuests);
        when(orderTableDao.findById(DEFAULT_ID)).thenReturn(Optional.of(savedOrderTable));
        when(orderTableDao.save(savedOrderTable)).thenReturn(savedOrderTable);

        // when
        OrderTable changedOrder = tableBo.changeNumberOfGuests(DEFAULT_ID, orderTable);

        // then
        assertAll(
                () -> assertThat(changedOrder.getNumberOfGuests()).isNotNull(),
                () -> assertThat(changedOrder.getNumberOfGuests()).isEqualTo(numberOfGuests)
        );
    }

    private OrderTable createOrderTable(Long id, Long tableGroupId, boolean empty) {
        final int numberOfGuests = 4;
        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setTableGroupId(tableGroupId);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(empty);
        return orderTable;
    }
}