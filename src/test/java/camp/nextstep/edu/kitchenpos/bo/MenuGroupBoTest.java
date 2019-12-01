package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.model.MenuGroup;
import camp.nextstep.edu.kitchenpos.support.dao.InMemoryMenuGroupDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("`메뉴`들을 묶을 수 있는 단위를 뜻한다.")
class MenuGroupBoTest {

    private MenuGroupBo menuGroupBo;

    @BeforeEach
    void setUp() {
        menuGroupBo = new MenuGroupBo(new InMemoryMenuGroupDao());
    }

    @DisplayName("`메뉴 그룹`을 등록할 수 있다.")
    @Test
    void create() {
        // given
        final MenuGroup menuGroup = new MenuGroup();

        // when
        final MenuGroup savedMenuGroup = menuGroupBo.create(menuGroup);

        // then
        assertThat(savedMenuGroup).isEqualTo(menuGroup);
    }

    @DisplayName("`메뉴 그룹` 조회 시 등록된 `메뉴 그룹`이 없다면 빈 리스트를 반환한다.")
    @Test
    void list_empty() {
        // when
        final List<MenuGroup> menuGroups = menuGroupBo.list();

        // then
        assertThat(menuGroups).isEmpty();
    }

    @DisplayName("`메뉴 그룹`을 하나 등록 후 조회 시 등록된 `메뉴 그룹` 하나를 반환한다.")
    @Test
    void list_single() {
        // given
        final MenuGroup menuGroup = new MenuGroup();
        menuGroupBo.create(menuGroup);

        // when
        final List<MenuGroup> menuGroups = menuGroupBo.list();

        // then
        assertThat(menuGroups).containsExactly(menuGroup);
    }

    @DisplayName("`메뉴 그룹` 조회 시 등록된 `메뉴 그룹`의 갯수 만큼 반환한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 3, 100, 234})
    void list_many(final int size) {
        // given
        IntStream.range(0, size)
                .mapToObj(ignored -> new MenuGroup())
                .forEach(menuGroupBo::create);

        // when
        final List<MenuGroup> menuGroups = menuGroupBo.list();

        // then
        assertThat(menuGroups).hasSize(size);
    }
}