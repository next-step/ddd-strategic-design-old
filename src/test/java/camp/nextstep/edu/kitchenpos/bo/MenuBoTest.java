package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.MenuDao;
import camp.nextstep.edu.kitchenpos.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Menu;
import camp.nextstep.edu.kitchenpos.model.MenuProduct;
import camp.nextstep.edu.kitchenpos.model.Product;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({MockitoExtension.class})
class MenuBoTest {

  @Mock
  private MenuDao menuDao;

  @Mock
  private MenuGroupDao menuGroupDao;

  @Mock
  private ProductDao productDao;

  @InjectMocks
  private MenuBo menuBo;

  @DisplayName("메뉴를 등록할 수 있다.")
  @Test
  public void create() throws Exception {
    //given
    Menu menu = createMenu(1L, 0, 1L);

    given(menuGroupDao.existsById(any(Long.class))).willReturn(true);
    given(menuDao.save(any())).willReturn(menu);

    //when
    Menu actual = menuBo.create(menu);

    //then
    assertThat(actual).isNotNull();
  }

  @DisplayName("등록된 메뉴 목록을 볼 수 있다.")
  @Test
  public void list() throws Exception {
    //given
    List<Menu> menus = Arrays.asList(new Menu(), new Menu());
    given(menuDao.findAll()).willReturn(menus);

    //when
    final List<Menu> actual = menuBo.list();

    //then
    assertThat(actual).isNotNull();
    assertThat(actual).hasSize(menus.size());
  }

  @DisplayName("메뉴의 가격은 0원 이상이어야 한다.")
  @Test
  public void menuPriceMoreZero() throws Exception {
    //given
    Menu menu = createMenu(1L, -1, 1L);

    //when

    //then
    assertThrows(IllegalArgumentException.class,
        () -> menuBo.create(menu));
  }

  @DisplayName("메뉴 그룹을 가지고 있어야 한다.")
  @Test
  public void hasMenuGroup() throws Exception {
    //given
    Menu menu = createMenu(1L, 0, 1L);
    given(menuGroupDao.existsById(any(Long.class))).willReturn(false);

    //when

    //then
    assertThrows(IllegalArgumentException.class,
        () -> menuBo.create(menu));
  }

  @DisplayName("메뉴 상품 가격의 총합이 메뉴 가격보다 크면 안 된다.")
  @Test
  public void menuProductSum() throws Exception {
    //given
    MenuProduct menuProduct = new MenuProduct();
    menuProduct.setProductId(1L);
    menuProduct.setQuantity(1);

    final Menu menu = createMenu(1L, 1001, 1L, menuProduct);

    Product product = new Product();
    product.setId(1L);
    product.setPrice(BigDecimal.valueOf(1000));

    //when
    given(menuGroupDao.existsById(any(Long.class))).willReturn(true);
    given(productDao.findById(any())).willReturn(Optional.of(product));

    //then
    assertThrows(IllegalArgumentException.class,
        () -> menuBo.create(menu));
  }

  private Menu createMenu(Long id, int price, Long menuGroupId, MenuProduct... menuProducts) {
    final Menu menu = new Menu();
    menu.setId(id);
    menu.setPrice(BigDecimal.valueOf(price));
    menu.setMenuGroupId(menuGroupId);
    menu.setMenuProducts(Arrays.asList(menuProducts));
    return menu;
  }
}
