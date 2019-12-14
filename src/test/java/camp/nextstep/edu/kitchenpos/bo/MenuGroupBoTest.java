package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupBoTest {
    @Mock
    private MenuGroupDao menuGroupDao;
    @InjectMocks
    private MenuGroupBo bo;
    private MenuGroup menuGroup;
    private List<MenuGroup> menuGroups;

    @BeforeEach
    void setUp() {
        menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroup.setName("두마리메뉴");

        menuGroups = new ArrayList<>();
        menuGroups.add(menuGroup);
    }

    @Test
    void createMenuGroup() {
        given(menuGroupDao.save(menuGroup)).willReturn(menuGroup);

        MenuGroup savedMenuGroup = bo.create(menuGroup);

        assertThat(savedMenuGroup).isSameAs(menuGroup);
    }

    @Test
    void listMenuGroupTest() {
        given(menuGroupDao.findAll()).willReturn(menuGroups);

        List<MenuGroup> menuGroups = bo.list();

        assertThat(menuGroups).hasSize(1);
        assertThat(menuGroups).containsExactly(menuGroup);
    }
}
