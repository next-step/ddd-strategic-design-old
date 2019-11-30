package camp.nextstep.edu.kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    @InjectMocks
    private MenuGroupBo menuGroupBo;
    @Mock
    private MenuGroupDao menuGroupDao;

    @DisplayName("메뉴 그룹을 생성할 수 있다")
    @Test
    void create() {
        //given
        MenuGroup request = new MenuGroup();
        request.setName("분식");
        when(menuGroupDao.save(any())).thenAnswer(invocation -> {
            MenuGroup saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        //when
        MenuGroup result = menuGroupBo.create(request);
        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(request.getName());
    }

    @DisplayName("전체 메뉴 그룹을 조회할 수 있다")
    @Test
    void list() {
        //given
        when(menuGroupDao.findAll()).thenReturn(mockMenuGroups());
        //when
        List<MenuGroup> result = menuGroupBo.list();
        //then
        assertThat(result).isNotNull();
    }

    private List<MenuGroup> mockMenuGroups() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroup.setName("분식");
        return Arrays.asList(menuGroup);
    }
}