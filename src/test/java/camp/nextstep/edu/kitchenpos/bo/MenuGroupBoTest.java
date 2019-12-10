package camp.nextstep.edu.kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
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

@ExtendWith({MockitoExtension.class})
class MenuGroupBoTest {

  @Mock
  private MenuGroupDao menuGroupDao;

  @InjectMocks
  private MenuGroupBo menuGroupBo;

  @DisplayName("메뉴 그룹을 등록할 수 있다.")
  @Test
  public void create() throws Exception {
    //given
    MenuGroup menuGroup = createMenuGroup("추천메뉴");

    given(menuGroupDao.save(menuGroup)).willReturn(menuGroup);

    //when
    MenuGroup createdMenuGroup = menuGroupBo.create(menuGroup);

    //then
    assertThat(createdMenuGroup.getName()).isEqualTo(menuGroup.getName());
  }

  @DisplayName("등록된 메뉴 그룹 목록을 볼 수 있다.")
  @Test
  public void list() throws Exception {
    //given
    List<MenuGroup> menuGroups = Arrays.asList(
        createMenuGroup("메뉴1"), createMenuGroup("메뉴2"));

    given(menuGroupDao.findAll()).willReturn(menuGroups);

    //when
    List<MenuGroup> menuGroupList = menuGroupBo.list();

    //then
    assertThat(menuGroupList).hasSize(menuGroups.size());
  }

  private MenuGroup createMenuGroup(String menuName) {
    final MenuGroup menuGroup = new MenuGroup();
    menuGroup.setName(menuName);
    return menuGroup;
  }
}
