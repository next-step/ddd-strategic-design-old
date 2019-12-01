package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupBoTest {

    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupBo menuGroupBo;

    @DisplayName("메뉴묶음을 추가할 수 있다")
    @Test
    void create() {
        // given
        final MenuGroup menuGroup = createMenuGroup();
        given(menuGroupDao.save(menuGroup)).willReturn(menuGroup);

        // when
        final MenuGroup actual = menuGroupBo.create(menuGroup);

        // then
        assertThat(actual).isNotNull();
    }

    @DisplayName("모든 메뉴묶음을 조회할 수 있다")
    @Test
    void list() {
        // given
        final List<MenuGroup> menuGroups = Arrays.asList(createMenuGroup(), createMenuGroup());
        given(menuGroupDao.findAll()).willReturn(menuGroups);

        // when
        final List<MenuGroup> actual = menuGroupBo.list();

        // then
        assertThat(actual).hasSize(menuGroups.size());
    }

    private MenuGroup createMenuGroup() {
        return new MenuGroup();
    }
}