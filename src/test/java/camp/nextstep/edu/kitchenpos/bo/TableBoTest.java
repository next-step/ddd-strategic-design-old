package camp.nextstep.edu.kitchenpos.bo;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class TableBoTest {

  @Mock
  private OrderTableDao orderTableDao;

  @Mock
  private OrderDao orderDao;

  @InjectMocks
  private TableBo tableBo;

  @DisplayName("주문 테이블을 둥록할 수 있다.")
  @Test
  public void create() throws Exception {
    //given
    OrderTable orderTable = new OrderTable();
    orderTable.setNumberOfGuests(3);
    given(orderTableDao.save(any())).willReturn(orderTable);

    //when
    OrderTable actual = tableBo.create(orderTable);

    //then
    assertThat(actual).isNotNull();
    assertThat(actual.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests());
  }

  @DisplayName("주문 테이블 목록을 볼 수 있다")
  @Test
  public void list() throws Exception {
    //given
    List<OrderTable> orderTables = Arrays.asList(new OrderTable(), new OrderTable());
    given(orderTableDao.findAll()).willReturn(orderTables);

    //when
    List<OrderTable> actual = tableBo.list();

    //then
    assertThat(actual).isNotNull();
    assertThat(actual).hasSize(orderTables.size());
  }

  @DisplayName("주문 테이블의 빈 상태 여부를 변경할 수 있다.")
  @Test
  public void changeEmpty() throws Exception {
    //given
    OrderTable orderTable = createOrderTable(1L, false);
    given(orderTableDao.findById(orderTable.getId())).willReturn(Optional.of(orderTable));
    given(orderDao.existsByOrderTableIdAndOrderStatusIn(any(), any())).willReturn(false);
    given(orderTableDao.save(orderTable)).willReturn(orderTable);

    //when
    OrderTable actual = tableBo.changeEmpty(orderTable.getId(), orderTable);

    //then
    assertThat(actual.isEmpty()).isEqualTo(orderTable.isEmpty());
  }

  @DisplayName("테이블 그룹 번호를 가지고 있다.")
  @Test
  public void hasTableGroupId() throws Exception {
    //given
    OrderTable orderTable = createOrderTable(1L, false);
    orderTable.setTableGroupId(1L);
    given(orderTableDao.findById(orderTable.getId())).willReturn(Optional.of(orderTable));

    //when

    //then
    assertThrows(IllegalArgumentException.class,
        () -> tableBo.changeEmpty(orderTable.getId(), orderTable));
  }

  @DisplayName("주문 테이블의 인원수를 변경할 수 있다.")
  @Test
  public void changeNumberOfGuests() throws Exception {
    //given
    OrderTable orderTable = createOrderTable(1L, false);
    orderTable.setNumberOfGuests(3);
    given(orderTableDao.findById(orderTable.getId())).willReturn(Optional.of(orderTable));
    given(orderTableDao.save(orderTable)).willReturn(orderTable);

    //when
    OrderTable actual = tableBo.changeNumberOfGuests(orderTable.getId(), orderTable);

    //then
    assertThat(actual).isNotNull();
  }

  @DisplayName("인원수가 0명보다 작아서는 안 된다.")
  @Test
  public void numberOfGuestsShouldNotBeZero() throws Exception {
    //given
    OrderTable orderTable = createOrderTable(1L, false);
    orderTable.setNumberOfGuests(-1);
    //when

    //then
    assertThrows(IllegalArgumentException.class,
        () -> tableBo.changeNumberOfGuests(orderTable.getId(), orderTable));
  }

  private OrderTable createOrderTable(Long id, boolean empty) {
    final OrderTable orderTable = new OrderTable();
    orderTable.setId(id);
    orderTable.setEmpty(empty);
    return orderTable;
  }

}
