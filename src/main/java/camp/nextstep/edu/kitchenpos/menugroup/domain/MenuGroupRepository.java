package camp.nextstep.edu.kitchenpos.menugroup.domain;

import java.util.List;
import java.util.Optional;

public interface MenuGroupRepository {

    MenuGroup save(final MenuGroup entity);

    Optional<MenuGroup> findById(final Long id);

    List<MenuGroup> findAll();

    boolean existsById(final Long id);

}
