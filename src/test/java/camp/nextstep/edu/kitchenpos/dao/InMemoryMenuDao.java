package camp.nextstep.edu.kitchenpos.dao;

import camp.nextstep.edu.kitchenpos.model.Menu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryMenuDao implements MenuDao {

    private static final Logger log = LoggerFactory.getLogger(InMemoryMenuDao.class);

    private Long id = 0L;
    private Map<Long, Menu> menus = new ConcurrentHashMap<>();

    @Override
    public Menu save(Menu menu) {
        menu.setId(id);
        menus.put(menu.getId(), menu);
        log.debug("Menu ({})", menu);
        id += 1L;
        return menu;
    }

    @Override
    public Optional<Menu> findById(Long id) {
        return Optional.ofNullable(menus.get(id));
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(menus.values());
    }
}
