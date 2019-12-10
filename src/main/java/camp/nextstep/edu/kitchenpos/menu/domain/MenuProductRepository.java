package camp.nextstep.edu.kitchenpos.menu.domain;

import java.util.List;
import java.util.Optional;

public interface MenuProductRepository {

    MenuProduct save(final MenuProduct entity);

    Optional<MenuProduct> findById(final Long id);

    List<MenuProduct> findAll();

    List<MenuProduct> findAllByMenuId(final Long menuId);
}
