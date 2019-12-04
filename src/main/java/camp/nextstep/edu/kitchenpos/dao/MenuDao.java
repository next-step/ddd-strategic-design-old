package camp.nextstep.edu.kitchenpos.dao;

import camp.nextstep.edu.kitchenpos.model.Menu;

import java.util.List;
import java.util.Optional;

public interface MenuDao {
    Menu save(Menu menu);

    Optional<Menu> findById(Long id);

    List<Menu> findAll();
}
