package camp.nextstep.edu.kitchenpos.menu.domain;

import java.util.List;
import java.util.Optional;

public interface MenuDao {

    Menu save(final Menu menu);

    Optional<Menu> findById(final Long id);

    List<Menu> findAll();
}
