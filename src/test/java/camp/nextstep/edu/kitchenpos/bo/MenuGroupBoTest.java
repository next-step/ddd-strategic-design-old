package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.menu.bo.MenuGroupBo;
import camp.nextstep.edu.kitchenpos.menu.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.menu.model.menugroup.MenuGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static camp.nextstep.edu.kitchenpos.Fixtures.menuGroup;

class MenuGroupBoTest {
    private final MenuGroupDao menuGroupDao = new InMemoryMenuGroupDao();

    private MenuGroupBo menuGroupBo;

    @BeforeEach
    void setUp() {
        menuGroupBo = new MenuGroupBo(menuGroupDao);
    }

    @DisplayName("메뉴 그룹을 등록할 수 있다.")
    @Test
    void create() {
        // given
        final MenuGroup menuGroup = menuGroup();

        // when
        menuGroupBo.create(menuGroup);
    }
}