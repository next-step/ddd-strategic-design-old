package camp.nextstep.edu.kitchenpos.order.ui;

import camp.nextstep.edu.kitchenpos.order.bo.OrderBo;
import camp.nextstep.edu.kitchenpos.order.domain.Order;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderRestController {
    private final OrderBo orderBo;

    public OrderRestController(final OrderBo orderBo) {
        this.orderBo = orderBo;
    }

    @PostMapping("/api/orders")
    public ResponseEntity<Order> create(@RequestBody final Order order) {
        final Order created = orderBo.create(order);
        final URI uri = URI.create("/api/orders/" + created.getId());
        return ResponseEntity.created(uri)
                .body(created)
                ;
    }

    @GetMapping("/api/orders")
    public ResponseEntity<List<Order>> list() {
        return ResponseEntity.ok()
                .body(orderBo.list())
                ;
    }

    @PutMapping("/api/orders/{orderId}/order-status")
    public ResponseEntity<Order> changeOrderStatus(
            @PathVariable final long orderId,
            @RequestBody final Order order
    ) {
        return ResponseEntity.ok(orderBo.changeOrderStatus(orderId, order));
    }
}