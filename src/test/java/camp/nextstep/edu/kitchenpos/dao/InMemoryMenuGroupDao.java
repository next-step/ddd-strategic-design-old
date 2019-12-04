package camp.nextstep.edu.kitchenpos.dao;

import camp.nextstep.edu.kitchenpos.model.MenuGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryMenuGroupDao implements MenuGroupDao {

    private Logger log = LoggerFactory.getLogger(InMemoryMenuGroupDao.class);

    private Long id = 0L;
    private Map<Long, MenuGroup> menuGroups = new ConcurrentHashMap<>();

    @Override
    public MenuGroup save(MenuGroup entity) {
        entity.setId(id);
        menuGroups.put(entity.getId(), entity);
        log.debug("MenuGroupDao({})", entity);
        id += 1L;
        return entity;
    }

    @Override
    public Optional<MenuGroup> findById(Long id) {
        return Optional.ofNullable(menuGroups.get(id));
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(menuGroups.values());
    }

    @Override
    public boolean existsById(Long id) {
        return menuGroups.containsKey(id);
    }
}
