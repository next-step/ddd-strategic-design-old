package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @DisplayName("주문 테이블은 주문 테이블 번호, 테이블 그룹 번호, 고객 인원수, 테이블 이용여부 속성들을 가지고 있다.")
    @Test
    void hasProperties() {
        String orderTableIdPropertyName = "id";
        String tableGroupIdPropertyName = "tableGroupId";
        String numberOfGuestsPropertyName = "numberOfGuests";
        String emptyPropertyName = "empty";

        assertThat(orderTable).hasFieldOrProperty(orderTableIdPropertyName);
        assertThat(orderTable).hasFieldOrProperty(tableGroupIdPropertyName);
        assertThat(orderTable).hasFieldOrProperty(numberOfGuestsPropertyName);
        assertThat(orderTable).hasFieldOrProperty(emptyPropertyName);
    }

    @DisplayName("[테이블 생성] 주문 테이블을 생성할 수 있다.")
    @Test
    void create() {
        // given
        OrderTable orderTable = createOrderTable(1L, null, true);
        given(orderTableDao.save(any())).willReturn(orderTable);

        // when
        OrderTable savedOrderTable = tableBo.create(any());

        // then
        assertThat(savedOrderTable).isNotNull();
        assertThat(savedOrderTable).isEqualTo(orderTable);
    }

    @DisplayName("[테이블 조회] 주문 테이블을 조회할 수 있다.")
    @Test
    void list() {
        // given
        List<OrderTable> orderTables = mock(List.class);
        given(orderTables.size()).willReturn(2);
        given(orderTableDao.findAll()).willReturn(orderTables);

        // when
        List<OrderTable> allOrderTables = tableBo.list();

        // then
        assertThat(allOrderTables).isNotNull();
        assertThat(allOrderTables.size()).isEqualTo(2);
    }

    @DisplayName("[주문 테이블 상태 변경] 주문 테이블이 존재하면 성공을 반환한다.")
    @Test
    void whenOrderTableIsExist_thenSuccess() {
        // when
        OrderTable orderTable = createOrderTable(1l, null, true);
        when(orderTableDao.findById(any())).thenReturn(Optional.of(orderTable));
        OrderTable savedOrderTable = orderTableDao.findById(any()).get();

        // then
        assertThat(savedOrderTable).isNotNull();
    }

    @DisplayName("[주문 테이블 상태 변경] 주문 테이블이 테이블 그룹에 속해있으면 예외를 발생 한다.")
    @Test
    void whenOrderTableHasTableGroupId_thenFail() {
        // given
        OrderTable orderTable = createOrderTable(1l, 1L, true);
        given(orderTableDao.findById(any())).willReturn(Optional.of(orderTable));

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeEmpty(1l, orderTable));
    }

    @DisplayName("[주문 테이블 상태 변경] 주문 테이블의 테이블 이용 여부를 변경 한다.")
    @Test
    void changeEmpty() {
        // given
        OrderTable beforeOrderTable = createOrderTable(1l, null, true);
        OrderTable savedOrderTable = createOrderTable(1l, null, false);

        given(orderTableDao.findById(any())).willReturn(Optional.of(savedOrderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(any(), any())).willReturn(false);
        given(orderTableDao.save(any())).willReturn(savedOrderTable);

        // when
        OrderTable changedOrderTable = tableBo.changeEmpty(1l, beforeOrderTable);

        // then
        assertThat(changedOrderTable.isEmpty()).isTrue();
        assertThat(changedOrderTable.isEmpty()).isEqualTo(savedOrderTable.isEmpty());
    }

    @DisplayName("[주문 테이블 인원수 변경] 고객 인원 숫자는 0명 미만 일경우 예외를 발생 한다")
    @Test
    void whenNumberOfGuestIsLessThanZero_thenFail() {
        // when
        when(orderTable.getNumberOfGuests()).thenReturn(-1);

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeNumberOfGuests(1l, orderTable));
    }

    @DisplayName("[주문 테이블 인원수 변경] 테이블이 비워져 있으면 예외를 발생 한다")
    @Test
    void whenOrderTableIsEmpty_thenFail() {
        when(orderTable.getNumberOfGuests()).thenReturn(1);
        when(orderTableDao.findById(any())).thenReturn(Optional.of(createOrderTable(1l, 1L, true)));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeNumberOfGuests(1L, orderTable));
    }

    @DisplayName("[주문 테이블 인원수 변경] 주문 테이블 인원수를 1명으로 변경할 수 있다.")
    @Test
    void changeNumberOfGuests() {
        OrderTable savedOrderTable = createOrderTable(1L, 1L, false);
        when(orderTable.getNumberOfGuests()).thenReturn(1);
        when(orderTableDao.findById(any())).thenReturn(Optional.of(savedOrderTable));
        when(orderTableDao.save(any())).thenReturn(savedOrderTable);

        // when
        OrderTable changedOrder = tableBo.changeNumberOfGuests(1L, orderTable);

        // then
        assertThat(changedOrder.getNumberOfGuests()).isNotNull();
        assertThat(changedOrder.getNumberOfGuests()).isEqualTo(1);

    }

    @DisplayName("주문 테이블 객체 생성 테스트 픽스쳐")
    OrderTable createOrderTable(Long id, Long tableGroupId, boolean empty) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setTableGroupId(tableGroupId);
        orderTable.setNumberOfGuests(4);
        orderTable.setEmpty(empty);
        return orderTable;
    }
}