package camp.nextstep.edu.kitchenpos.support.dao;

import camp.nextstep.edu.kitchenpos.menuproduct.dao.MenuProductDao;
import camp.nextstep.edu.kitchenpos.menuproduct.model.MenuProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableList;

public class InMemoryMenuProductDao implements MenuProductDao {

    private final List<MenuProduct> menuProducts = new ArrayList<>();

    @Override
    public MenuProduct save(final MenuProduct menuProduct) {
        menuProducts.add(menuProduct);
        return menuProduct;
    }

    @Override
    public Optional<MenuProduct> findById(final Long id) {
        if (Objects.isNull(id)) {
            return Optional.empty();
        }

        return menuProducts.stream()
                .filter(menuProduct -> id.equals(menuProduct.getSeq()))
                .findAny();
    }

    @Override
    public List<MenuProduct> findAll() {
        return unmodifiableList(menuProducts);
    }

    @Override
    public List<MenuProduct> findAllByMenuId(final Long menuId) {
        if (Objects.isNull(menuId)) {
            return emptyList();
        }

        return menuProducts.stream()
                .filter(menuProduct -> menuId.equals(menuProduct.getMenuId()))
                .collect(toUnmodifiableList());
    }
}
