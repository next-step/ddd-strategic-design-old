package camp.nextstep.edu.kitchenpos.controller;

import camp.nextstep.edu.kitchenpos.bo.MenuGroupBo;
import camp.nextstep.edu.kitchenpos.model.MenuGroup;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
public class MenuGroupRestController {
    private final MenuGroupBo menuGroupBo;

    public MenuGroupRestController(final MenuGroupBo menuGroupBo) {
        this.menuGroupBo = menuGroupBo;
    }

    @PostMapping("/api/menu-groups")
    public ResponseEntity<MenuGroup> create(@RequestBody final MenuGroup menuGroup) {
        final MenuGroup created = menuGroupBo.create(menuGroup);
        final URI uri = URI.create("/api/menu-groups/" + created.getId());
        return ResponseEntity.created(uri)
                .body(created)
                ;
    }

    @GetMapping("/api/menus-groups")
    public ResponseEntity<List<MenuGroup>> list() {
        return ResponseEntity.ok()
                .body(menuGroupBo.list())
                ;
    }
}
