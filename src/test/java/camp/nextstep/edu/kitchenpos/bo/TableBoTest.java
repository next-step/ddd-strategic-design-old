package camp.nextstep.edu.kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import camp.nextstep.edu.kitchenpos.order.domain.OrderRepository;
import camp.nextstep.edu.kitchenpos.ordertable.bo.TableBo;
import camp.nextstep.edu.kitchenpos.ordertable.domain.OrderTable;
import camp.nextstep.edu.kitchenpos.ordertable.domain.OrderTableRepository;
import camp.nextstep.edu.kitchenpos.tablegroup.domain.TableGroupRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TableBoTest {

    @Mock
    private OrderRepository orderDao;

    @Mock
    private OrderTableRepository orderTableDao;

    @Mock
    private TableGroupRepository tableGroupDao;

    @InjectMocks
    private TableBo tableBo;

    @Test
    @DisplayName("주문테이블을 등록할 수 있다")
    void add(){

        OrderTable orderTable = this.createOrderTable(2, false);
        when(orderTableDao.save(any())).thenReturn(orderTable);


        OrderTable actual = tableBo.create(orderTable);


        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(orderTable);
    }

    @Test
    @DisplayName("주문테이블의 비어있는 상태로 변경할 수 있다.")
    void ordertable_status_change(){

        OrderTable emptyTable = this.createEmptyOrderTable();
        when(orderDao.existsByOrderTableIdAndOrderStatusIn(anyLong(), any())).thenReturn(false);
        when(orderTableDao.findById(any())).thenReturn(Optional.of(emptyTable));
        when(orderTableDao.save(any())).thenReturn(emptyTable);


        OrderTable actual = tableBo.changeEmpty(1L, emptyTable);


        assertThat(actual).isNotNull();
        assertThat(actual.isEmpty()).isTrue();
        assertThat(actual).isEqualTo(emptyTable);
    }

    @Test
    @DisplayName("주문테이블은 테이블그룹에 속하면 안된다.")
    void ordertable_in_table_group(){

        OrderTable orderTable = this.createOrderTable(2, false);
        when(orderTableDao.findById(any())).thenReturn(Optional.of(orderTable));

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeEmpty(1L, orderTable));
    }

    @Test
    @DisplayName("주문테이블의 비어있는 상태로 변경시, 테이블그룹에 속하지 않고 완료상태여야만 한다.")
    void ordertable_completion_status(){

        OrderTable emptyTable = this.createEmptyOrderTable();
        when(orderDao.existsByOrderTableIdAndOrderStatusIn(anyLong(), any())).thenReturn(true);
        when(orderTableDao.findById(any())).thenReturn(Optional.of(emptyTable));

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeEmpty(1L, emptyTable));
    }

    @Test
    @DisplayName("주문테이블에 변경하고자하는 인원이 0명 이상인 경우만 변경가능하다.")
    void ordertable_change_number(){

        OrderTable emptyTable = this.createOrderTable(-1, false);

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeNumberOfGuests(1L, emptyTable));
    }

    @Test
    @DisplayName("주문테이블이 사용자 변경시 비어있는 상태면 안된다.")
    void ordertable_change_number_isEmpty(){

        OrderTable orderTable = this.createOrderTable(0, true);
        when(orderTableDao.findById(any())).thenReturn(Optional.of(orderTable));

        assertThrows(IllegalArgumentException.class, () -> tableBo.changeNumberOfGuests(1L, orderTable));
    }


    private OrderTable createOrderTable(int value, boolean isEmpty){

        OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setNumberOfGuests(value);
        orderTable.setTableGroupId(2L);
        orderTable.setEmpty(isEmpty);

        return orderTable;
    }

    private OrderTable createEmptyOrderTable(){

        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(0);
        orderTable.setTableGroupId(null);
        orderTable.setEmpty(true);

        return orderTable;
    }


}