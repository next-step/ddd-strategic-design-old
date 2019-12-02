package camp.nextstep.edu.kitchenpos.support.dao;

import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.TableGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.unmodifiableList;
import static java.util.Optional.empty;

public class InMemoryTableGroupDao implements TableGroupDao {

    private final List<TableGroup> tableGroups = new ArrayList<>();

    @Override
    public TableGroup save(final TableGroup tableGroup) {
        tableGroups.add(tableGroup);
        return tableGroup;
    }

    @Override
    public Optional<TableGroup> findById(final Long id) {
        if (Objects.isNull(id)) {
            return empty();
        }

        return tableGroups.stream()
                .filter(tableGroup -> id.equals(tableGroup.getId()))
                .findAny();
    }

    @Override
    public List<TableGroup> findAll() {
        return unmodifiableList(tableGroups);
    }
}
