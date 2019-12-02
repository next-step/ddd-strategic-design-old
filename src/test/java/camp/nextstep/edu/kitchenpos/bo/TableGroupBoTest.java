package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.Order;
import camp.nextstep.edu.kitchenpos.model.OrderStatus;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import camp.nextstep.edu.kitchenpos.model.TableGroup;
import camp.nextstep.edu.kitchenpos.support.dao.InMemoryOrderDao;
import camp.nextstep.edu.kitchenpos.support.dao.InMemoryOrderTableDao;
import camp.nextstep.edu.kitchenpos.support.dao.InMemoryTableGroupDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("`주문 테이블`들을 묶을 수 있는 단위를 뜻한다.")
class TableGroupBoTest {

    private OrderDao orderDao;
    private OrderTableDao orderTableDao;
    private TableGroupDao tableGroupDao;

    private TableGroupBo tableGroupBo;

    @BeforeEach
    void setUp() {
        orderDao = new InMemoryOrderDao();
        orderTableDao = new InMemoryOrderTableDao();
        tableGroupDao = new InMemoryTableGroupDao();

        tableGroupBo = new TableGroupBo(orderDao, orderTableDao, tableGroupDao);
    }

    @DisplayName("`테이블 그룹` 등록 시 `주문 테이블`이 비어있다면 예외처리 한다.")
    @Test
    void create_emptyOrderTables() {
        // given
        final TableGroup tableGroup = new TableGroup();
        tableGroup.setOrderTables(emptyList());

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("`테이블 그룹` 등록 시 `주문 테이블`이 1개라면 예외처리 한다.")
    @Test
    void create_singleOrderTables() {
        // given
        final OrderTable orderTable = new OrderTable();

        final TableGroup tableGroup = new TableGroup();
        tableGroup.setOrderTables(List.of(orderTable));

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("`테이블 그룹` 등록 시 `주문 테이블`이 비어있다면 예외처리 한다.")
    @Test
    void create_orderTableEmpty() {
        // given
        final OrderTable orderTable1 = new OrderTable();
        orderTable1.setEmpty(true);
        orderTableDao.save(orderTable1);

        final OrderTable orderTable2 = new OrderTable();
        orderTable2.setEmpty(true);
        orderTableDao.save(orderTable2);

        final TableGroup tableGroup = new TableGroup();
        tableGroup.setOrderTables(List.of(orderTable1, orderTable2));

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("`테이블 그룹` 등록 시 `주문 테이블`이 이미 `테이블 그룹`에 속해있다면 예외처리 한다.")
    @Test
    void create_orderTableHaveOrderGroup() {
        // given
        final OrderTable orderTable1 = new OrderTable();
        orderTable1.setTableGroupId(1L);
        orderTableDao.save(orderTable1);

        final OrderTable orderTable2 = new OrderTable();
        orderTable2.setTableGroupId(2L);
        orderTableDao.save(orderTable2);

        final TableGroup tableGroup = new TableGroup();
        tableGroup.setOrderTables(List.of(orderTable1, orderTable2));

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("빈 `테이블 그룹`을 등록한다.")
    @Test
    void create_empty() {
        // given
        final LocalDateTime startAt = LocalDateTime.now();
        final OrderTable orderTable1 = new OrderTable();
        final OrderTable orderTable2 = new OrderTable();

        final TableGroup tableGroup = new TableGroup();
        tableGroup.setOrderTables(List.of(orderTable1, orderTable2));

        // when
        final TableGroup savedTableGroup = tableGroupBo.create(tableGroup);
        final LocalDateTime endAt = LocalDateTime.now();

        // then
        assertThat(savedTableGroup.getCreatedDate()).isBetween(startAt, endAt);
    }

    @DisplayName("`테이블 그룹` 등록 시 `주문 테이블`은 등록된 `테이블 그룹`에 포함된다.")
    @Test
    void create_withOrderTable() {
        // given
        final OrderTable orderTable1 = new OrderTable();
        orderTableDao.save(orderTable1);

        final OrderTable orderTable2 = new OrderTable();
        orderTableDao.save(orderTable2);

        final TableGroup tableGroup = new TableGroup();
        tableGroup.setId(1L);
        tableGroup.setOrderTables(List.of(orderTable1, orderTable2));

        // when
        tableGroupBo.create(tableGroup);

        // then
        assertThat(orderTable1.getTableGroupId()).isNotNull();
        assertThat(orderTable2.getTableGroupId()).isNotNull();
    }

    @DisplayName("`테이블 그룹` 제거 시 `주문 테이블`이 미리 등록 돼 있지 않다면 예외처리 한다.")
    @Test
    void delete_notExistsOrderTable() {
        // given
        final long tableGroupId = 1L;

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.delete(tableGroupId));
    }

    /*
        for (final OrderTable savedOrderTable : savedOrderTables) {
            savedOrderTable.setTableGroupId(null);
            orderTableDao.save(savedOrderTable);
        }
         */

    @DisplayName("`테이블 그룹` 제거 시 `주문 테이블`의 `주문` 상태가 완료되지 않았다면 예외처리 한다.")
    @EnumSource(value = OrderStatus.class, names = "COMPLETION", mode = EnumSource.Mode.EXCLUDE)
    @ParameterizedTest
    void delete_orderNotCompletion(final OrderStatus orderStatus) {
        // given
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setTableGroupId(1L);
        orderTableDao.save(orderTable);

        final Order order = new Order();
        order.setOrderTableId(orderTable.getId());
        order.setOrderStatus(orderStatus.name());
        orderDao.save(order);

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableGroupBo.delete(orderTable.getTableGroupId()));
    }

    @DisplayName("`테이블 그룹` 제거 시 `주문 테이블`에 `테이블 그룹` 의 정보를 제거한다.")
    @Test
    void delete() {
        // given
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setTableGroupId(1L);
        orderTableDao.save(orderTable);

        final Order order = new Order();
        order.setOrderTableId(orderTable.getId());
        order.setOrderStatus(OrderStatus.COMPLETION.name());
        orderDao.save(order);

        // when
        tableGroupBo.delete(order.getOrderTableId());

        // then
        assertThat(orderTable.getTableGroupId()).isNull();
    }
}