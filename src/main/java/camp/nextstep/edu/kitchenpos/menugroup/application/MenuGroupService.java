package camp.nextstep.edu.kitchenpos.menugroup.application;

import camp.nextstep.edu.kitchenpos.menugroup.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.menugroup.domain.MenuGroup;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class MenuGroupService {
    private final MenuGroupDao menuGroupDao;

    public MenuGroupService(final MenuGroupDao menuGroupDao) {
        this.menuGroupDao = menuGroupDao;
    }

    @Transactional
    public MenuGroup create(final MenuGroup menuGroup) {
        return menuGroupDao.save(menuGroup);
    }

    public List<MenuGroup> list() {
        return menuGroupDao.findAll();
    }
}
