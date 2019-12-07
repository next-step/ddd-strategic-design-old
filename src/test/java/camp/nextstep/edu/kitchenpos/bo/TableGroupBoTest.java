package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import camp.nextstep.edu.kitchenpos.model.TableGroup;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith({MockitoExtension.class})
class TableGroupBoTest {

  @Mock
  private OrderTableDao orderTableDao;

  @Mock
  private OrderDao orderDao;

  @Mock
  private TableGroupDao tableGroupDao;

  @InjectMocks
  private TableGroupBo tableGroupBo;

  @DisplayName("테이블 그룹을 등록할 수 있다.")
  @Test
  public void create() throws Exception {
    //given
    List<OrderTable> orderTables = Arrays.asList(
        createOrderTable(1L, 1L),
        createOrderTable(2L, 2L)
    );

    TableGroup tableGroup = createTableGroup(1L, orderTables);
    given(tableGroupDao.save(tableGroup)).willReturn(tableGroup);

    //when
    TableGroup actual = tableGroupBo.create(tableGroup);

    //then
    assertThat(actual).isNotNull();
  }

  @DisplayName("주문 테이블 목록은 2개 이상이어야 한다.")
  @Test
  public void orderTablesMustBeAtLeastTwo() throws Exception {
    //given
    List<OrderTable> orderTables = Arrays.asList(createOrderTable(1L, 1L));
    TableGroup tableGroup = createTableGroup(1L, orderTables);

    //when

    //then
    assertThrows(IllegalArgumentException.class,
        () -> tableGroupBo.create(tableGroup));
  }

  @DisplayName("주문 테이블 각각이 빈 테이블이어야 한다.")
  @Test
  public void orderTableIsEmpty() throws Exception {
    //given
    OrderTable orderTable = createOrderTable(1L, 1L);
    orderTable.setEmpty(true);
    OrderTable orderTable2 = createOrderTable(2L, 2L);
    orderTable2.setEmpty(false);

    List<OrderTable> orderTables = Arrays.asList(orderTable, orderTable2);
    TableGroup tableGroup = createTableGroup(1L, orderTables);

    given(orderTableDao.findAllByIdIn(any())).willReturn(orderTables);
    //when

    //then
    assertThrows(IllegalArgumentException.class,
        () -> tableGroupBo.create(tableGroup));
  }

  @DisplayName("테이블 그룹을 삭제할 수 있다.")
  @Test
  public void delete() throws Exception {
    //given
    List<OrderTable> orderTables = Arrays.asList(
        createOrderTable(1L, 1L),
        createOrderTable(2L, 2L)
    );

    TableGroup tableGroup = createTableGroup(1L, orderTables);

    given(orderTableDao.findAllByTableGroupId(tableGroup.getId())).willReturn(orderTables);
    given(orderDao.existsByOrderTableIdAndOrderStatusIn(any(), any())).willReturn(false);

    //when
    tableGroupBo.delete(tableGroup.getId());

    //then
    assertThat(orderTables).allMatch(orderTable -> orderTable.getTableGroupId() == null);
  }

  private TableGroup createTableGroup(Long id, List<OrderTable> orderTables) {
    final TableGroup tableGroup = new TableGroup();
    tableGroup.setId(id);
    tableGroup.setOrderTables(orderTables);
    return tableGroup;
  }

  private OrderTable createOrderTable(Long id, Long tableGroupId) {
    final OrderTable orderTable = new OrderTable();
    orderTable.setId(id);
    orderTable.setTableGroupId(tableGroupId);
    return orderTable;
  }

}
