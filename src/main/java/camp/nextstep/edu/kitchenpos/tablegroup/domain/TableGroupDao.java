package camp.nextstep.edu.kitchenpos.tablegroup.domain;

import java.util.List;
import java.util.Optional;

public interface TableGroupDao {

    TableGroup save(final TableGroup entity);

    Optional<TableGroup> findById(final Long id);

    List<TableGroup> findAll();

}
