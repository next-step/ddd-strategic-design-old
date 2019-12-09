package camp.nextstep.edu.kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import camp.nextstep.edu.kitchenpos.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.model.MenuGroup;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuGroupBoTest {

    private static final Long MENU_GROUP_ID = 1L;

    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupBo menuGroupBo;

    @DisplayName("메뉴그룹을 생성한다.")
    @Test
    void create() {
        // Given
        final String menuGroupName = "메뉴그룹명";
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(MENU_GROUP_ID);
        menuGroup.setName(menuGroupName);

        given(menuGroupDao.save(menuGroup)).willReturn(menuGroup);

        // When
        final MenuGroup saveMenuGroup = menuGroupBo.create(menuGroup);

        // Then
        assertAll(
                () -> assertThat(saveMenuGroup.getId()).isEqualTo(MENU_GROUP_ID),
                () -> assertThat(saveMenuGroup.getName()).isEqualTo(menuGroupName));

    }

    @DisplayName("메뉴그룹을 조회한다.")
    @Test
    void list() {
        // Given
        final String menuGroupName = "메뉴그룹명";
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(MENU_GROUP_ID);
        menuGroup.setName(menuGroupName);

        given(menuGroupDao.findAll()).willReturn(Arrays.asList(menuGroup, menuGroup, menuGroup));

        // When
        final List<MenuGroup> menuGroupList = menuGroupBo.list();

        // Then
        assertThat(menuGroupList).hasSize(3);
    }
}
