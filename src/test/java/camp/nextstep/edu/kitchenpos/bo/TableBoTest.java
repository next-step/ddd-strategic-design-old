package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.order.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.order.model.Order;
import camp.nextstep.edu.kitchenpos.order.model.OrderStatus;
import camp.nextstep.edu.kitchenpos.ordertable.bo.TableBo;
import camp.nextstep.edu.kitchenpos.ordertable.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.ordertable.model.OrderTable;
import camp.nextstep.edu.kitchenpos.support.dao.InMemoryOrderDao;
import camp.nextstep.edu.kitchenpos.support.dao.InMemoryOrderTableDao;
import camp.nextstep.edu.kitchenpos.support.dao.InMemoryTableGroupDao;
import camp.nextstep.edu.kitchenpos.tablegroup.dao.TableGroupDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("`고객`이 `매장`에 `상품`을 `주문`하여 식사할 수 있는 공간을 뜻한다.")
class TableBoTest {

    private OrderDao orderDao;
    private OrderTableDao orderTableDao;
    private TableGroupDao tableGroupDao;

    private TableBo tableBo;

    @BeforeEach
    void setUp() {
        orderDao = new InMemoryOrderDao();
        orderTableDao = new InMemoryOrderTableDao();
        tableGroupDao = new InMemoryTableGroupDao();

        tableBo = new TableBo(orderDao, orderTableDao, tableGroupDao);
    }

    @DisplayName("`테이블`을 등록할 수 있다.")
    @Test
    void create() {
        // given
        final OrderTable orderTable = new OrderTable();

        // when
        final OrderTable savedOrderTable = tableBo.create(orderTable);

        // then
        assertThat(savedOrderTable).isEqualTo(orderTable);
    }

    @DisplayName("`테이블` 조회 시 등록된 `테이블`이 없다면 빈 리스트를 반환한다.")
    @Test
    void list_empty() {
        // when
        final List<OrderTable> orderTables = tableBo.list();

        // then
        assertThat(orderTables).isEmpty();
    }

    @DisplayName("`테이블`을 하나 등록 후 조회 시 등록된 `테이블` 하나를 반환한다.")
    @Test
    void list_single() {
        // given
        final OrderTable orderTable = new OrderTable();
        tableBo.create(orderTable);

        // when
        final List<OrderTable> orderTables = tableBo.list();

        // then
        assertThat(orderTables).containsExactly(orderTable);
    }

    @DisplayName("`테이블` 조회 시 등록된 `테이블`의 갯수 만큼 반환한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 3, 100, 234})
    void list_many(final int size) {
        // given
        IntStream.range(0, size)
                .mapToObj(ignored -> new OrderTable())
                .forEach(tableBo::create);

        // when
        final List<OrderTable> orderTables = tableBo.list();

        // then
        assertThat(orderTables).hasSize(size);
    }

    @DisplayName("`주문 테이블`을 빈 테이블로 상태 변경 시 미리 등록돼 있지 않다면 예외처리 한다.")
    @Test
    void changeEmpty_notExists() {
        // given
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);

        final OrderTable changeOrderTable = new OrderTable();
        changeOrderTable.setEmpty(true);

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeEmpty(orderTable.getId(), changeOrderTable));
    }

    @DisplayName("`주문 테이블`을 빈 테이블로 상태 변경 시 테이블 그룹과 연관돼있다면 예외처리 한다.")
    @Test
    void changeEmpty_existsTableGroup() {
        // given
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setTableGroupId(1L);

        tableBo.create(orderTable);

        final OrderTable changeOrderTable = new OrderTable();
        changeOrderTable.setEmpty(true);

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeEmpty(orderTable.getId(), changeOrderTable));
    }

    @DisplayName("`주문 테이블`을 빈 테이블로 상태 변경 시 완료되지 않은 주문이 존재한다면 예외처리 한다.")
    @EnumSource(value = OrderStatus.class, names = "COMPLETION", mode = EnumSource.Mode.EXCLUDE)
    @ParameterizedTest
    void changeEmpty_orderNotCompletion(final OrderStatus orderStatus) {
        // given
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);

        tableBo.create(orderTable);

        final Order order = new Order();
        order.setOrderTableId(orderTable.getId());
        order.setOrderStatus(orderStatus.name());

        orderDao.save(order);

        final OrderTable changeOrderTable = new OrderTable();
        changeOrderTable.setEmpty(true);

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeEmpty(orderTable.getId(), changeOrderTable));
    }

    @DisplayName("`주문`이 없는 `주문 테이블`을 빈 테이블로 상태 변경한다.")
    @Test
    void changeEmpty_emptyOrder() {
        // given
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);

        tableBo.create(orderTable);

        final OrderTable changeOrderTable = new OrderTable();
        changeOrderTable.setEmpty(true);

        // when
        final OrderTable changedOrderTable = tableBo.changeEmpty(orderTable.getId(), changeOrderTable);

        // then
        assertThat(changedOrderTable.isEmpty()).isTrue();
    }

    @DisplayName("`주문`이 완료된 `주문 테이블`을 빈 테이블로 상태 변경한다.")
    @Test
    void changeEmpty_completionOrder() {
        // given
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);

        tableBo.create(orderTable);

        final Order order = new Order();
        order.setOrderTableId(orderTable.getId());
        order.setOrderStatus(OrderStatus.COMPLETION.name());

        orderDao.save(order);

        final OrderTable changeOrderTable = new OrderTable();
        changeOrderTable.setEmpty(true);

        // when
        final OrderTable changedOrderTable = tableBo.changeEmpty(orderTable.getId(), changeOrderTable);

        // then
        assertThat(changedOrderTable.isEmpty()).isTrue();
    }

    @DisplayName("`주문 테이블`의 `고객` 인원 변경 시 인원의 수가 1명 이상이 아니라면 예외처리 한다.")
    @ParameterizedTest
    @ValueSource(ints = {-123, -1})
    void changeNumberOfGuests_negative(final int numberOfGuests) {
        // given
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);

        tableBo.create(orderTable);

        final OrderTable changeOrderTable = new OrderTable();
        changeOrderTable.setNumberOfGuests(numberOfGuests);

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeNumberOfGuests(orderTable.getId(), changeOrderTable));
    }

    @DisplayName("`주문 테이블`의 `고객` 인원 변경 시 미리 등록돼 있지 않다면 예외처리 한다.")
    @Test
    void changeNumberOfGuests_notExists() {
        // given
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);

        final OrderTable changeOrderTable = new OrderTable();
        changeOrderTable.setNumberOfGuests(0);

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeNumberOfGuests(orderTable.getId(), changeOrderTable));
    }

    @DisplayName("`주문 테이블`의 `고객` 인원 변경 시 `주문 테이블`이 비어있지 않다면 예외처리 한다.")
    @Test
    void changeNumberOfGuests_empty() {
        // given
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setEmpty(false);

        final OrderTable changeOrderTable = new OrderTable();
        changeOrderTable.setNumberOfGuests(0);

        // when / then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> tableBo.changeNumberOfGuests(orderTable.getId(), changeOrderTable));
    }

    @DisplayName("`주문 테이블`의 `고객` 인원 수를 변경할 수 있다.")
    @Test
    void changeNumberOfGuests() {
        // given
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);

        tableBo.create(orderTable);

        final OrderTable changeOrderTable = new OrderTable();
        changeOrderTable.setNumberOfGuests(1);

        // when
        final OrderTable changedOrderTable = tableBo.changeNumberOfGuests(orderTable.getId(), changeOrderTable);

        // then
        assertThat(changedOrderTable.getNumberOfGuests()).isEqualTo(changedOrderTable.getNumberOfGuests());
    }
}