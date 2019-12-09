package camp.nextstep.edu.kitchenpos.support.dao;

import camp.nextstep.edu.kitchenpos.product.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.product.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.unmodifiableList;

public final class InMemoryProductDao implements ProductDao {

    private final List<Product> products = new ArrayList<>();

    @Override
    public Product save(final Product product) {
        products.add(product);
        return product;
    }

    @Override
    public Optional<Product> findById(final Long id) {
        if (Objects.isNull(id)) {
            return Optional.empty();
        }

        return products.stream()
                .filter(product -> Objects.nonNull(product.getId()))
                .filter(product -> product.getId().equals(id))
                .findAny();
    }

    @Override
    public List<Product> findAll() {
        return unmodifiableList(products);
    }
}
