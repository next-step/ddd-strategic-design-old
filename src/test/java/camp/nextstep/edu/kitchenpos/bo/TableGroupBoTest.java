package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.OrderStatus;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import camp.nextstep.edu.kitchenpos.model.TableGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableGroupBoTest {
    @Mock
    private OrderDao orderDao;
    @Mock
    private OrderTableDao orderTableDao;
    @Mock
    private TableGroupDao tableGroupDao;
    @InjectMocks
    private TableGroupBo bo;
    private TableGroup tableGroup;
    private List<OrderTable> orderTables;

    @BeforeEach
    void dummySetUp() {
        tableGroup = new TableGroup();
        tableGroup.setId(1L);
        tableGroup.setCreatedDate(LocalDateTime.now());

        OrderTable orderTable_1 = new OrderTable();
        orderTable_1.setId(1L);
        orderTable_1.setTableGroupId(tableGroup.getId());
        orderTable_1.setEmpty(false);
        orderTable_1.setNumberOfGuests(4);

        OrderTable orderTable_2 = new OrderTable();
        orderTable_2.setId(2L);
        orderTable_2.setTableGroupId(tableGroup.getId());
        orderTable_1.setEmpty(false);
        orderTable_1.setNumberOfGuests(2);

        orderTables = Arrays.asList(orderTable_1, orderTable_2);
        tableGroup.setOrderTables(orderTables);

    }

    @DisplayName("빈 주문 테이블 리스트를 갖고 있는 테이블 그룹을 생성할 수 없다.")
    @Test
    void canNotCreateTableGroup_whenOrderTableIsEmpty() {
        tableGroup.setOrderTables(Collections.emptyList());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> bo.create(tableGroup));
    }

    @DisplayName("크기가 2 미만의 주문 테이블 리스트를 갖고 있는 테이블 그룹을 생성할 수 없다.")
    @Test
    void canNotCreateTableGroup_whenOrderTableSizeLessTwo() {
        tableGroup.setOrderTables(Collections.singletonList(new OrderTable()));

        assertThat(tableGroup.getOrderTables())
                .hasSize(1);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> bo.create(tableGroup));
    }

    @DisplayName("크기가 2 이상인 주문 테이블 리스트를 갖고 있는 테이블 그룹을 생성할 수 있다.")
    @Test
    void canNotCreateTableGroup_whenOrderTableSizeMoreThanTwo() {
        assertThat(orderTables).hasSize(2);
        given(tableGroupDao.save(tableGroup)).willReturn(tableGroup);

        tableGroup.setOrderTables(orderTables);

        bo.create(tableGroup);
    }

    @DisplayName("주문 테이블에서 해당 테이블 그룹이 검색되지 않으면 테이블 그룹을 삭제 할 수 없다.")
    @Test
    void canNotDeleteTableGroup_whenOrderTableListIsEmpty() {
        given(orderTableDao.findAllByTableGroupId(anyLong()))
                .willReturn(Collections.emptyList());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> bo.delete(anyLong()));
    }

    @DisplayName("주문 테이블번호로 검색한 주문의 상태가 조리중이거나 식사중이면 테이블 그룹을 삭제할 수 없다.")
    @Test
    void canNotDeleteTableGroup_whenOrderStatusIsCookingAndMeal() {
        assertThat(orderTables).hasSize(2);
        given(orderTableDao.findAllByTableGroupId(anyLong()))
                .willReturn(orderTables);
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(
                orderTables.stream()
                        .findAny()
                        .get()
                        .getId(),
                Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))
        ).willReturn(true);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> bo.delete(anyLong()));
    }

    @DisplayName("주문 테이블번호로 검색한 주문의 상태가 조리중이거나 식사중이 아니면 테이블 그룹을 삭제할 수 있다.")
    @Test
    void canDeleteTableGroup_whenOrderStatusIsNotCookingAndMeal() {
        assertThat(orderTables).hasSize(2);
        Long orderTableId = orderTables.stream()
                .findAny()
                .get()
                .getId();

        given(orderTableDao.findAllByTableGroupId(anyLong()))
                .willReturn(orderTables);
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(
                orderTableId,
                Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))
        ).willReturn(false);

        bo.delete(anyLong());
    }

}
