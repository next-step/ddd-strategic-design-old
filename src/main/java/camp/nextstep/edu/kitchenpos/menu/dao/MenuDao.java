package camp.nextstep.edu.kitchenpos.menu.dao;

import camp.nextstep.edu.kitchenpos.menu.model.menu.Menu;

import java.util.List;
import java.util.Optional;

public interface MenuDao {
    Menu save(Menu entity);

    Optional<Menu> findById(Long id);

    List<Menu> findAll();
}
