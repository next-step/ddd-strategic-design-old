package camp.nextstep.edu.kitchenpos.bo;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import camp.nextstep.edu.kitchenpos.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.model.MenuGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuGroupBoTest {

    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupBo menuGroupBo;

    @Test
    @DisplayName("메뉴그룹은 이름이 있어야 등록할 수 있다.")
    void add(){

        MenuGroup menuGroup = this.createMenuGroup();
        when(menuGroupDao.save(any())).thenReturn(menuGroup);


        MenuGroup actual = menuGroupBo.create(menuGroup);


        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(menuGroup);
    }

    @Test
    @DisplayName("메뉴그룹의 전체 목록을 조회할 수 있다.")
    void menugroup_list(){

        MenuGroup menuGroup = this.createMenuGroup();
        when(menuGroupDao.findAll()).thenReturn(Arrays.asList(menuGroup));


        List<MenuGroup> menuGroups = menuGroupBo.list();


        assertThat(menuGroups).isNotNull();
        assertThat(menuGroups.size()).isEqualTo(1);
    }


    private MenuGroup createMenuGroup(){

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("menuGroup");

        return menuGroup;
    }
}