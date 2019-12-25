package camp.nextstep.edu.kitchenpos.menu;

import java.util.List;
import java.util.Optional;

public interface MenuDao {
    Menu save(Menu menu);

    Optional<Menu> findById(Long id);

    List<Menu> findAll();
}
