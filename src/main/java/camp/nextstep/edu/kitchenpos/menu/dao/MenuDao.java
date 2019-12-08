package camp.nextstep.edu.kitchenpos.menu.dao;

import camp.nextstep.edu.kitchenpos.menu.model.Menu;

import java.util.List;
import java.util.Optional;

public interface MenuDao {

    Menu save(Menu menu);

    Optional<Menu> findById(Long id);

    List<Menu> findAll();
}
