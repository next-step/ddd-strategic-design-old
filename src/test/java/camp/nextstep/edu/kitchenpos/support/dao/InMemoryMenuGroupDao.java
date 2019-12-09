package camp.nextstep.edu.kitchenpos.support.dao;

import camp.nextstep.edu.kitchenpos.menugroup.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.menugroup.model.MenuGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.unmodifiableList;

public final class InMemoryMenuGroupDao implements MenuGroupDao {

    private final List<MenuGroup> menuGroups = new ArrayList<>();

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        menuGroups.add(menuGroup);
        return menuGroup;
    }

    @Override
    public Optional<MenuGroup> findById(final Long id) {
        if (Objects.isNull(id)) {
            return Optional.empty();
        }

        return menuGroups.stream()
                .filter(menuGroup -> id.equals(menuGroup.getId()))
                .findAny();
    }

    @Override
    public List<MenuGroup> findAll() {
        return unmodifiableList(menuGroups);
    }

    @Override
    public boolean existsById(final Long id) {
        if (Objects.isNull(id)) {
            return false;
        }

        return menuGroups.stream()
                .map(MenuGroup::getId)
                .anyMatch(id::equals);
    }
}
