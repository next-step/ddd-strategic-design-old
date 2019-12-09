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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupBoTest {

    @Mock
    MenuGroupDao menuGroupDao;

    @InjectMocks
    MenuGroupBo menuGroupBo;

    @DisplayName("메뉴 그룹을 추가할 수 있다")
    @Test
    void create() {
        //given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(0L);
        menuGroup.setName("메뉴그룹");

        given(menuGroupDao.save(any())).willReturn(menuGroup);

        // when
        MenuGroup result = menuGroupBo.create(menuGroup);

        // then
        assertThat(result).isNotNull();
    }

    @DisplayName("메뉴 그룹 목록을 조회할 수 있다.")
    @Test
    void list() {
        // given
        given(menuGroupDao.findAll()).willReturn(Arrays.asList(new MenuGroup(), new MenuGroup()));

        // when
        List<MenuGroup> menuGroups = menuGroupBo.list();

        // then
        assertThat(menuGroups.size()).isEqualTo(2);
    }
}