package camp.nextstep.edu.kitchenpos.table.dao;

import camp.nextstep.edu.kitchenpos.table.model.tablegroup.TableGroup;

import java.util.List;
import java.util.Optional;

public interface TableGroupDao {
    TableGroup save(TableGroup entity);

    Optional<TableGroup> findById(Long id);

    List<TableGroup> findAll();
}
