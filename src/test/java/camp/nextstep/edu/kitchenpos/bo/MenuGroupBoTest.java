package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("메뉴 그룹 Business Object 테스트 클래스")
@ExtendWith(MockitoExtension.class)
class MenuGroupBoTest {
    @Mock
    private MenuGroup menuGroup;

    @Mock
    private MenuGroupDao menuGroupDao;

    @DisplayName("메뉴 그룹은 메뉴 그룹 번호와 메뉴 그룹명 속성들을 가지고 있다.")
    @Test
    void hasProperties() {
        String menuGroupIdPropertyName = "id";
        String menuGroupNamePropertyName = "name";

        assertAll(
                () -> assertThat(menuGroup).hasFieldOrProperty(menuGroupIdPropertyName),
                () -> assertThat(menuGroup).hasFieldOrProperty(menuGroupNamePropertyName)
        );
    }

    @DisplayName("메뉴 그룹을 생성할 수 있다.")
    @Test
    void create() {
        // given
        given(menuGroupDao.save(menuGroup)).willReturn(menuGroup);

        // when
        MenuGroup savedMenuGroup = menuGroupDao.save(menuGroup);

        // then
        assertThat(savedMenuGroup).isNotNull()
                                  .isEqualTo(menuGroup);
    }

    @DisplayName("메뉴 그룹들을 조회할 수 있다")
    @Test
    void list() {
        // given
        final int menuGroupSize = 3;
        final int menuGroupFirsIndex = 0;
        final Long DEFAULT_ID = 1L;
        final String menuGroupName = "치킨";

        List<MenuGroup> menuGroups = mock(List.class);
        MenuGroup menuGroup =  new MenuGroup();
        menuGroup.setId(DEFAULT_ID);
        menuGroup.setName(menuGroupName);

        menuGroups.add(menuGroup);
        menuGroups.add(new MenuGroup());
        menuGroups.add(new MenuGroup());

        given(menuGroupDao.findAll()).willReturn(menuGroups);
        given(menuGroups.get(menuGroupFirsIndex)).willReturn(menuGroup);
        given(menuGroups.size()).willReturn(menuGroupSize);

        // when
        List<MenuGroup> savedMenuGroups = menuGroupDao.findAll();

        // then
        assertAll(
                () -> assertThat(savedMenuGroups).isNotNull(),
                () -> assertThat(menuGroups.get(menuGroupFirsIndex).getId()).isEqualTo(DEFAULT_ID),
                () -> assertThat(menuGroups.get(menuGroupFirsIndex).getName()).isEqualTo(menuGroupName),
                () -> assertThat(savedMenuGroups).hasSize(menuGroupSize)
        );
    }
}