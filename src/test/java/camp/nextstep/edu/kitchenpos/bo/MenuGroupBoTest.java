package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.InMemoryMenuGroupDao;
import camp.nextstep.edu.kitchenpos.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MenuGroupBoTest {

    private MenuGroupBo menuGroupBo;

    private MenuGroupDao menuGroupDao;

    @BeforeEach
    void setUp() {
        menuGroupDao = new InMemoryMenuGroupDao();
        menuGroupBo = new MenuGroupBo(menuGroupDao);
    }

    @DisplayName("점주는 메뉴그룹을 등록 할 수 있다")
    @Test
    void createMenuGroup() {
        // given
        MenuGroup menuGroup = createMenuGroup(1L, "추천메뉴");

        // when
        MenuGroup actual = menuGroupBo.create(menuGroup);

        // then
        assertThat(actual).isEqualTo(menuGroup);
    }

    @DisplayName("등록된 모든 메뉴그룹을 조회한다")
    @Test
    void list() {
        // given
        List<MenuGroup> menuGroups = Arrays.asList(createMenuGroup(1L, "추천 메뉴"),
                                                   createMenuGroup(2L, "사이드 메뉴"));
        menuGroups.forEach(menuGroup -> menuGroupDao.save(menuGroup));

        // when
        List<MenuGroup> expectedMenuGroup = menuGroupBo.list();

        // then
        assertThat(expectedMenuGroup).hasSize(2);
        assertThat(expectedMenuGroup).containsExactlyInAnyOrderElementsOf(menuGroups);
    }

    private MenuGroup createMenuGroup(long id, String nameOfCategory) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(nameOfCategory);
        return menuGroup;
    }
}