package camp.nextstep.edu.kitchenpos.menugroup.dao;

import camp.nextstep.edu.kitchenpos.menugroup.domain.MenuGroup;

import java.util.*;

public class InMemoryMenuGroupDao implements MenuGroupDao {
    private final Map<Long, MenuGroup> data = new HashMap<>();

    @Override
    public MenuGroup save(final MenuGroup menuGroup) {
        data.put(menuGroup.getId(), menuGroup);
        return menuGroup;
    }

    @Override
    public Optional<MenuGroup> findById(final Long id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public boolean existsById(final Long id) {
        return data.containsKey(id);
    }
}
