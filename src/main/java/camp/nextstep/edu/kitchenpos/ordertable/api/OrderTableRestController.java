package camp.nextstep.edu.kitchenpos.ordertable.api;

import camp.nextstep.edu.kitchenpos.ordertable.application.OrderTableService;
import camp.nextstep.edu.kitchenpos.ordertable.domain.OrderTable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class OrderTableRestController {
    private final OrderTableService orderTableService;

    public OrderTableRestController(final OrderTableService orderTableService) {
        this.orderTableService = orderTableService;
    }

    @PutMapping("/api/tables")
    public ResponseEntity<OrderTable> create(@RequestBody final OrderTable orderTable) {
        final OrderTable created = orderTableService.create(orderTable);
        final URI uri = URI.create("/api/tables/" + created.getId());
        return ResponseEntity.created(uri)
                .body(created)
                ;
    }

    @GetMapping("/api/tables")
    public ResponseEntity<List<OrderTable>> list() {
        return ResponseEntity.ok()
                .body(orderTableService.list())
                ;
    }

    @PutMapping("/api/tables/{orderTableId}/empty")
    public ResponseEntity<OrderTable> changeEmpty(
            @PathVariable final long orderTableId,
            @RequestBody final OrderTable orderTable
    ) {
        return ResponseEntity.ok()
                .body(orderTableService.changeEmpty(orderTableId, orderTable))
                ;
    }

    @PutMapping("/api/tables/{orderTableId}/number-of-guests")
    public ResponseEntity<OrderTable> changeNumberOfGuests(
            @PathVariable final long orderTableId,
            @RequestBody final OrderTable orderTable
    ) {
        return ResponseEntity.ok()
                .body(orderTableService.changeNumberOfGuests(orderTableId, orderTable))
                ;
    }
}
