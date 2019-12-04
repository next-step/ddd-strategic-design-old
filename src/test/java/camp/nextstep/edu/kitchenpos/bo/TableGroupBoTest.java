package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.OrderStatus;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import camp.nextstep.edu.kitchenpos.model.TableGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("테이블 그룹 Business Object 테스트 클래스")
@ExtendWith(MockitoExtension.class)
class TableGroupBoTest {
    @Mock
    private TableGroup tableGroup;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @InjectMocks
    private TableGroupBo tableGroupBo;

    private final Long DEFAULT_ID = 1L;

    private final Long SECOND_ORDER_TABLE_ID = 2L;

    private final List<Long> IDS = Arrays.asList(DEFAULT_ID, SECOND_ORDER_TABLE_ID);

    @DisplayName("테이블 그룹은 테이블 번호, 주문 테이블 속성들을 가지고 있다.")
    @Test
    void hasProperties() {
        String tableGroupIdPropertyName = "id";
        String createdDatePropertyName = "createdDate";
        String orderTablesPropertyName = "orderTables";

        assertAll(
                () -> assertThat(tableGroup).hasFieldOrProperty(tableGroupIdPropertyName),
                () -> assertThat(tableGroup).hasFieldOrProperty(createdDatePropertyName),
                () -> assertThat(tableGroup).hasFieldOrProperty(orderTablesPropertyName)
        );
    }

    @DisplayName("[테이블 그룹 생성] 주문 테이블이 없으면 예외를 발생 한다.")
    @Test
    void whenOrderTableNotExist_thenFail() {
        // given
        given(tableGroup.getOrderTables()).willReturn(null);

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("[테이블 그룹 생성] 주문 테이블 갯수가 2개 미만일 경우 예외를 발생 한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void whenOrderTableIsLessThanTwo_thenFail(int size) {
        //given
        List<OrderTable> orderTables = mock(List.class);
        given(tableGroup.getOrderTables()).willReturn(orderTables);
        given(orderTables.size()).willReturn(size);

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("[테이블 그룹 생성] 주문 테이블의 비어있으면 예외를 발생 한다.")
    @Test
    void whenOrderTableIsEmpty_thenFail() {
        //given

        List<OrderTable> orderTables = new ArrayList<>();
        orderTables.add(createOrderTable(DEFAULT_ID, null, true));
        orderTables.add(createOrderTable(SECOND_ORDER_TABLE_ID, null, true));
        given(tableGroup.getOrderTables()).willReturn(orderTables);
        given(orderTableDao.findAllByIdIn(IDS)).willReturn(orderTables);

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("[테이블 그룹 생성] 주문 테이블의 테이블 그룹이 존재하면 예외를 발생 한다.")
    @Test
    void whenTableGroupExistInOrderTable_thenFail() {
        //given
        List<OrderTable> orderTables = new ArrayList<>();
        orderTables.add(createOrderTable(DEFAULT_ID, DEFAULT_ID, false));
        orderTables.add(createOrderTable(SECOND_ORDER_TABLE_ID, DEFAULT_ID, false));
        given(tableGroup.getOrderTables()).willReturn(orderTables);
        given(orderTableDao.findAllByIdIn(IDS)).willReturn(orderTables);

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("[테이블 그룹 생성] 테이블 그룹 생성시 테이블 그룹 번호가 생성 되면 성공을 반환 한다.")
    @Test
    void whenTableGroupNumberCreate_thenSuccess() {
        // given
        given(tableGroupDao.save(tableGroup)).willReturn(tableGroup);

        // when
        TableGroup savedTableGroup = tableGroupDao.save(tableGroup);
        when(savedTableGroup.getId()).thenReturn(DEFAULT_ID);

        // then
        assertThat(savedTableGroup.getId()).isNotNull()
                                           .isEqualTo(DEFAULT_ID);
    }

    @DisplayName("[테이블 그룹 생성] 생성된 테이블 그룹 번호로 주문 테이블들을 그룹화 하면 성공을 반환 한다.")
    @Test
    void isSameTableGroupId() {
        // given
        List<OrderTable> orderTables = new ArrayList<>();
        orderTables.add(createOrderTable(DEFAULT_ID, null, false));
        orderTables.add(createOrderTable(SECOND_ORDER_TABLE_ID, null, false));

        given(tableGroup.getOrderTables()).willReturn(orderTables);
        given(orderTableDao.findAllByIdIn(IDS)).willReturn(orderTables);

        given(tableGroupDao.save(tableGroup)).willReturn(tableGroup);
        given(tableGroup.getId()).willReturn(DEFAULT_ID);

        // when
        TableGroup savedTableGroup = tableGroupBo.create(tableGroup);

        // then
        final Long[] tableGroupIds = savedTableGroup.getOrderTables()
                .stream()
                .map(OrderTable::getTableGroupId)
                .toArray(Long[]::new);

        assertArrayEquals(new Long[]{DEFAULT_ID, DEFAULT_ID}, tableGroupIds);
    }

    @DisplayName("[테이블 그룹 생성] 테이블 그룹을 생성할 수 있다.")
    @Test
    void create() {
        // given
        List<OrderTable> orderTables = new ArrayList<>();
        orderTables.add(createOrderTable(DEFAULT_ID, null, false));
        orderTables.add(createOrderTable(SECOND_ORDER_TABLE_ID, null, false));

        given(tableGroup.getOrderTables()).willReturn(orderTables);
        given(orderTableDao.findAllByIdIn(IDS)).willReturn(orderTables);

        given(tableGroupDao.save(tableGroup)).willReturn(tableGroup);
        given(tableGroup.getId()).willReturn(DEFAULT_ID);

        // when
        TableGroup savedTableGroup = tableGroupBo.create(tableGroup);

        // then
        assertAll(
                () -> assertThat(savedTableGroup).isNotNull(),
                () -> assertThat(savedTableGroup.getId()).isEqualTo(DEFAULT_ID)
        );
    }

    @DisplayName("[테이블 그룹 삭제] 테이블 그룹에 속한 주문 테이블이 존재하면 성공을 반환 한다.")
    @Test
    void isExistTableGroupIdInOrderTables() {
        // given
        List<OrderTable> orderTables = new ArrayList<>();
        orderTables.add(createOrderTable(DEFAULT_ID, DEFAULT_ID, false));
        orderTables.add(createOrderTable(SECOND_ORDER_TABLE_ID, DEFAULT_ID, false));
        given(orderTableDao.findAllByTableGroupId(DEFAULT_ID)).willReturn(orderTables);

        // when
        List<OrderTable> savedOrderTables = orderTableDao.findAllByTableGroupId(DEFAULT_ID);

        // then
        assertAll(
                () -> assertThat(savedOrderTables).isNotNull(),
                () -> assertThat(savedOrderTables).allMatch(orderTable -> orderTable.getTableGroupId() == DEFAULT_ID)
        );
    }

    @DisplayName("[테이블 그룹 삭제] 테이블 그룹에 속해있는 주문 테이블들중 가장 먼저 주문된 주문 테이블을 갖고온다.")
    @Test
    void getFirstOrderTable() {
        // given
        List<OrderTable> orderTables = new ArrayList<>();
        orderTables.add(createOrderTable(SECOND_ORDER_TABLE_ID, DEFAULT_ID, false));
        orderTables.add(createOrderTable(DEFAULT_ID, DEFAULT_ID, false));
        given(orderTableDao.findAllByTableGroupId(DEFAULT_ID)).willReturn(orderTables);

        // when
        List<OrderTable> savedOrderTables = orderTableDao.findAllByTableGroupId(DEFAULT_ID);
        OrderTable orderTable = savedOrderTables.stream()
                .sorted(Comparator.comparingLong(OrderTable::getId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        // then
        assertAll(
                () -> assertThat(orderTable).isNotNull(),
                () -> assertThat(orderTable.getId()).isEqualTo(DEFAULT_ID)
        );
    }

    @DisplayName("[테이블 그룹 삭제] 주문 테이블의 주문 상태를 삭제 할 수 있다.")
    @Test
    void delete() {
        // given
        final List<String> orderStatuses = Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name());
        List<OrderTable> orderTables = new ArrayList<>();
        orderTables.add(createOrderTable(SECOND_ORDER_TABLE_ID, DEFAULT_ID, false));
        orderTables.add(createOrderTable(DEFAULT_ID, DEFAULT_ID, false));
        given(orderTableDao.findAllByTableGroupId(DEFAULT_ID)).willReturn(orderTables);
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(DEFAULT_ID, orderStatuses)).willReturn(false);

        // when
        tableGroupBo.delete(DEFAULT_ID);

        // then
        assertThat(orderTables).allMatch(orderTable -> orderTable.getTableGroupId() == null);
    }

    private OrderTable createOrderTable(Long id, Long tableGroupId, boolean empty) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setTableGroupId(tableGroupId);
        orderTable.setEmpty(empty);
        return orderTable;
    }
}