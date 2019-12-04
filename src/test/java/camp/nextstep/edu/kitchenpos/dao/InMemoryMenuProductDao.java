package camp.nextstep.edu.kitchenpos.dao;

import camp.nextstep.edu.kitchenpos.model.MenuProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryMenuProductDao implements camp.nextstep.edu.kitchenpos.dao.MenuProductDao {

    private static final Logger log = LoggerFactory.getLogger(InMemoryMenuProductDao.class);

    private Long id = 0L;
    private Map<Long, MenuProduct> menuProducts = new ConcurrentHashMap<>();

    @Override
    public MenuProduct save(MenuProduct entity) {
        entity.setSeq(id);
        menuProducts.put(id, entity);
        id += 1L;
        log.debug("MenuProduct({})", entity);
        return entity;
    }

    @Override
    public Optional<MenuProduct> findById(Long id) {
        return Optional.ofNullable(menuProducts.get(id));
    }

    @Override
    public List<MenuProduct> findAll() {
        return new ArrayList<>(menuProducts.values());
    }

    @Override
    public List<MenuProduct> findAllByMenuId(Long menuId) {
        return menuProducts.values()
                           .stream()
                           .filter(menuProduct -> menuProduct.getMenuId()
                                                             .equals(menuId))
                           .collect(Collectors.toList());
    }
}
