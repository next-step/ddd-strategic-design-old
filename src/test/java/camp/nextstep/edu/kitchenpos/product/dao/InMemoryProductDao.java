package camp.nextstep.edu.kitchenpos.product.dao;

import camp.nextstep.edu.kitchenpos.product.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.product.model.Product;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryProductDao implements ProductDao {
    private final Map<Long, Product> data = new HashMap<>();
    public InMemoryProductDao() {
    }

    public Product save(final Product product) {
        data.put(product.getId(), product);
        return product;
    }

    public Optional<Product> findById(final Long id) {
        return Optional.ofNullable(data.get(id));
    }

    public List<Product> findAll() {
        return new ArrayList<>(data.values());
    }
}
