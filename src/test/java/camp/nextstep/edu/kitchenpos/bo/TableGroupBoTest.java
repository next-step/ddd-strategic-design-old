package camp.nextstep.edu.kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import camp.nextstep.edu.kitchenpos.order.infra.OrderDao;
import camp.nextstep.edu.kitchenpos.ordertable.domain.OrderTable;
import camp.nextstep.edu.kitchenpos.ordertable.infra.OrderTableDao;
import camp.nextstep.edu.kitchenpos.tablegroup.bo.TableGroupBo;
import camp.nextstep.edu.kitchenpos.tablegroup.domain.TableGroup;
import camp.nextstep.edu.kitchenpos.tablegroup.infra.TableGroupDao;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TableGroupBoTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @InjectMocks
    private TableGroupBo tableGroupBo;


    @Test
    @DisplayName("테이블그룹을 등록할 수 있다.")
    void add(){

        List<OrderTable> orderTables = Arrays.asList(this.createOrderTable(null, null), this.createOrderTable(null, null));
        TableGroup tableGroup = this.createTableGroup(orderTables);
        when(orderTableDao.findAllByIdIn(any())).thenReturn(orderTables);
        when(tableGroupDao.save(any())).thenReturn(tableGroup);


        TableGroup actual = tableGroupBo.create(tableGroup);


        assertThat(actual).isNotNull();
        assertThat(actual.getCreatedDate()).isNotNull();
        assertThat(actual.getOrderTables().size()).isEqualTo(2);
        assertThat(actual.getOrderTables()).containsAnyElementsOf(orderTables);
    }

    @Test
    @DisplayName("그룹에 속한 주문테이블의 목록이 2개 이상이어야 한다.")
    void add_group_size(){

        List<OrderTable> orderTables = Arrays.asList(this.createOrderTable(null, null));
        TableGroup tableGroup = this.createTableGroup(orderTables);


        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @Test
    @DisplayName("주문테이블들은 다른 테이블그룹에 속해 있으면 안된다.")
    void other_group(){

        List<OrderTable> orderTables = Arrays.asList(this.createOrderTable(null, null), this.createOrderTable(1L, 1L));
        TableGroup tableGroup = this.createTableGroup(orderTables);
        when(orderTableDao.findAllByIdIn(any())).thenReturn(orderTables);

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @Test
    @DisplayName("테이블그룹을 삭제할 수 있다.")
    void delete(){

        List<OrderTable> orderTables = Arrays.asList(this.createOrderTable(1L, 1L), this.createOrderTable(2L, 1L));
        when(orderTableDao.findAllByTableGroupId(anyLong())).thenReturn(orderTables);
        when(orderDao.existsByOrderTableIdAndOrderStatusIn(anyLong(), any())).thenReturn(false);


        tableGroupBo.delete(1L);


        assertThat(orderTables).allMatch(orderTable -> orderTable.getTableGroupId() == null);
    }

    @Test
    @DisplayName("테이블그룹에 속한 주문테이블은 완료상태여야 한다.")
    void delete_status_check(){

        List<OrderTable> orderTables = Arrays.asList(this.createOrderTable(1L, 1L), this.createOrderTable(2L, 1L));
        when(orderTableDao.findAllByTableGroupId(anyLong())).thenReturn(orderTables);
        when(orderDao.existsByOrderTableIdAndOrderStatusIn(anyLong(), any())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.delete(1L));
    }

    private TableGroup createTableGroup(List<OrderTable> orderTables){
        TableGroup tableGroup = new TableGroup();
        tableGroup.setId(1L);
        tableGroup.setOrderTables(orderTables);

        return tableGroup;
    }

    private OrderTable createOrderTable(Long id, Long tableGroupId){

        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setTableGroupId(tableGroupId);
        orderTable.setEmpty(false);

        return orderTable;
    }
}