package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.menu.dao.MenuDao;
import camp.nextstep.edu.kitchenpos.menu.model.menu.Menu;

import java.util.*;

public class InMemoryMenuDao implements MenuDao {
    private final Map<Long, Menu> entities = new HashMap<>();

    @Override
    public Menu save(final Menu entity) {
        entities.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Menu> findById(final Long id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(entities.values());
    }
}
