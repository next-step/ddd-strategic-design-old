package camp.nextstep.edu.kitchenpos.support.dao;

import camp.nextstep.edu.kitchenpos.menu.dao.MenuDao;
import camp.nextstep.edu.kitchenpos.menu.model.Menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.unmodifiableList;

public class InMemoryMenuDao implements MenuDao {

    private final List<Menu> menus = new ArrayList<>();

    @Override
    public Menu save(final Menu menu) {
        menus.add(menu);
        return menu;
    }

    @Override
    public Optional<Menu> findById(final Long id) {
        if (Objects.isNull(id)) {
            return Optional.empty();
        }

        return menus.stream()
                .filter(menu -> id.equals(menu.getId()))
                .findAny();
    }

    @Override
    public List<Menu> findAll() {
        return unmodifiableList(menus);
    }
}
