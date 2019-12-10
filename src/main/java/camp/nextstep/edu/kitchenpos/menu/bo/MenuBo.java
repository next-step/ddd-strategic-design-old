package camp.nextstep.edu.kitchenpos.menu.bo;

import camp.nextstep.edu.kitchenpos.menu.domain.Menu;
import camp.nextstep.edu.kitchenpos.menu.domain.MenuDao;
import camp.nextstep.edu.kitchenpos.menu.domain.MenuProduct;
import camp.nextstep.edu.kitchenpos.menu.domain.MenuProductRepository;
import camp.nextstep.edu.kitchenpos.menugroup.domain.MenuGroupRepository;
import camp.nextstep.edu.kitchenpos.product.domain.Product;
import camp.nextstep.edu.kitchenpos.product.domain.ProductRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MenuBo {
    private final MenuDao menuDao;
    private final MenuGroupRepository menuGroupDao;
    private final MenuProductRepository menuProductDao;
    private final ProductRepository productDao;

    public MenuBo(
            final MenuDao menuDao,
            final MenuGroupRepository menuGroupDao,
            final MenuProductRepository menuProductDao,
            final ProductRepository productDao
    ) {
        this.menuDao = menuDao;
        this.menuGroupDao = menuGroupDao;
        this.menuProductDao = menuProductDao;
        this.productDao = productDao;
    }

    @Transactional
    public Menu create(final Menu menu) {
        final BigDecimal price = menu.getPrice();

        if (Objects.isNull(price) || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException();
        }

        if (!menuGroupDao.existsById(menu.getMenuGroupId())) {
            throw new IllegalArgumentException();
        }

        final List<MenuProduct> menuProducts = menu.getMenuProducts();

        BigDecimal sum = BigDecimal.ZERO;
        for (final MenuProduct menuProduct : menuProducts) {
            final Product product = productDao.findById(menuProduct.getProductId())
                    .orElseThrow(IllegalArgumentException::new);
            sum = sum.add(product.getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())));
        }

        if (price.compareTo(sum) > 0) {
            throw new IllegalArgumentException();
        }

        final Menu savedMenu = menuDao.save(menu);

        final Long menuId = savedMenu.getId();
        final List<MenuProduct> savedMenuProducts = new ArrayList<>();
        for (final MenuProduct menuProduct : menuProducts) {
            menuProduct.setMenuId(menuId);
            savedMenuProducts.add(menuProductDao.save(menuProduct));
        }
        savedMenu.setMenuProducts(savedMenuProducts);

        return savedMenu;
    }

    public List<Menu> list() {
        final List<Menu> menus = menuDao.findAll();

        for (final Menu menu : menus) {
            menu.setMenuProducts(menuProductDao.findAllByMenuId(menu.getId()));
        }

        return menus;
    }
}
