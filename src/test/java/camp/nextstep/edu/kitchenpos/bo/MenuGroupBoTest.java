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

    @InjectMocks
    private MenuGroupBo menuGroupBo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @DisplayName("메뉴그룹은 메뉴 그룹 번호와 메뉴 그룹명 속성들을 가지고 있다.")
    @Test
    void hasProperties() {
        String menuGroupIdPropertyName = "id";
        String menuGroupNamePropertyName = "name";

        assertThat(menuGroup).hasFieldOrProperty(menuGroupIdPropertyName);
        assertThat(menuGroup).hasFieldOrProperty(menuGroupNamePropertyName);
    }

    @DisplayName("메뉴 그룹을 생성할 수 있다.")
    @Test
    void create() {
        // given
        given(menuGroupDao.save(any())).willReturn(menuGroup);

        // when
        MenuGroup savedMenuGroup = menuGroupDao.save(any());

        // then
        assertThat(savedMenuGroup).isNotNull()
                                  .isEqualTo(menuGroup);
    }

    @DisplayName("메뉴 그룹들을 조회할 수 있다")
    @Test
    void list() {
        // given
        List<MenuGroup> menuGroups = mock(List.class);
        MenuGroup menuGroup =  new MenuGroup();
        menuGroup.setId(1L);
        menuGroup.setName("치킨");

        menuGroups.add(menuGroup);
        menuGroups.add(new MenuGroup());
        menuGroups.add(new MenuGroup());

        given(menuGroupDao.findAll()).willReturn(menuGroups);
        given(menuGroups.get(0)).willReturn(menuGroup);
        given(menuGroups.size()).willReturn(3);

        // when
        List<MenuGroup> savedMenuGroups = menuGroupDao.findAll();

        // then
        assertThat(savedMenuGroups).isNotNull();
        assertThat(menuGroups.get(0).getId()).isEqualTo(1L);
        assertThat(menuGroups.get(0).getName()).isEqualTo("치킨");
        assertThat(savedMenuGroups.size()).isEqualTo(3);
    }
}