package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.bo.mock.InMemoryMenuGroupDao;
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
    private MenuGroup menuGroup;

    private MenuGroupBo menuGroupBo;

    private MenuGroupDao menuGroupDao = new InMemoryMenuGroupDao();

    @BeforeEach
    void setup() {
        menuGroupBo = new MenuGroupBo(menuGroupDao);
        menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroup.setName("치킨류");
    }

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
        // when
        MenuGroup actual = menuGroupDao.save(menuGroup);

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual).isSameAs(actual)
        );
    }

    @DisplayName("메뉴 그룹들을 조회할 수 있다")
    @Test
    void list() {
        // given
        final MenuGroup otherMenuGroup = new MenuGroup();
        otherMenuGroup.setId(2L);
        otherMenuGroup.setName("식사류");

        menuGroupDao.save(menuGroup);
        menuGroupDao.save(otherMenuGroup);

        // when
        List<MenuGroup> actual = menuGroupDao.findAll();

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual).containsExactlyInAnyOrder(menuGroup, otherMenuGroup)
        );
    }
}