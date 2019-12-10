package camp.nextstep.edu.kitchenpos.menugroup.bo;

import camp.nextstep.edu.kitchenpos.menugroup.domain.MenuGroup;
import camp.nextstep.edu.kitchenpos.menugroup.domain.MenuGroupRepository;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MenuGroupBo {
    private final MenuGroupRepository menuGroupRepository;

    public MenuGroupBo(final MenuGroupRepository menuGroupRepository) {
        this.menuGroupRepository = menuGroupRepository;
    }

    @Transactional
    public MenuGroup create(final MenuGroup menuGroup) {
        return menuGroupRepository.save(menuGroup);
    }

    public List<MenuGroup> list() {
        return menuGroupRepository.findAll();
    }
}
