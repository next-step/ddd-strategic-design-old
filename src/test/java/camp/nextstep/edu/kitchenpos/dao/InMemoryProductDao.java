package camp.nextstep.edu.kitchenpos.dao;

import camp.nextstep.edu.kitchenpos.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryProductDao implements ProductDao {

    private static final Logger log = LoggerFactory.getLogger(InMemoryProductDao.class);

    private Long id = 0L;
    private Map<Long, Product> products = new ConcurrentHashMap<>();

    @Override
    public Product save(Product entity) {
        entity.setId(id);
        products.put(entity.getId(), entity);
        id += 1L;
        log.debug("Product ({})", entity);
        return entity;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }
}
