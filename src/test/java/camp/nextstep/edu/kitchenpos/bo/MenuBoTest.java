package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.menu.bo.MenuBo;
import camp.nextstep.edu.kitchenpos.menu.dao.MenuDao;
import camp.nextstep.edu.kitchenpos.menu.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.menu.dao.MenuProductDao;
import camp.nextstep.edu.kitchenpos.product.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.menu.model.menu.Menu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static camp.nextstep.edu.kitchenpos.Fixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuBoTest {
    private final MenuDao menuDao = new InMemoryMenuDao();

    @Mock
    private MenuGroupDao menuGroupDao;

    @Mock
    private MenuProductDao menuProductDao;

    @Mock
    private ProductDao productDao;

    private MenuBo menuBo;

    @BeforeEach
    void setUp() {
        menuBo = new MenuBo(menuDao, menuGroupDao, menuProductDao, productDao);
    }

    @DisplayName("1 개 이상의 등록된 상품으로 메뉴를 등록할 수 있다.")
    @Test
    void create() {
        // given
        final Menu expected = menu();
        given(menuGroupDao.existsById(anyLong())).willReturn(true);
        given(productDao.findById(anyLong())).willReturn(Optional.of(friedChicken()));

        // when
        final Menu actual = menuBo.create(expected);

        // then
        assertMenu(expected, actual);
    }

    @DisplayName("메뉴는 특정 메뉴 그룹에 속해야 한다.")
    @ParameterizedTest
    @NullSource
    void create(final Long menuGroupId) {
        // given
        final Menu expected = menu();
        expected.setMenuGroupId(menuGroupId);
    }

    private void assertMenu(final Menu expected, final Menu actual) {
        assertThat(actual).isNotNull();
        assertAll(
                () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
                () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice()),
                () -> assertThat(actual.getMenuGroupId()).isEqualTo(expected.getMenuGroupId()),
                () -> assertThat(actual.getMenuProducts())
                        .containsExactlyInAnyOrderElementsOf(expected.getMenuProducts())
        );
    }
}