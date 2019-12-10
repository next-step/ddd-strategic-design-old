package camp.nextstep.edu.kitchenpos.product.bo;

import camp.nextstep.edu.kitchenpos.product.domain.Product;
import camp.nextstep.edu.kitchenpos.product.domain.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProductBo {
    private final ProductRepository productDao;

    public ProductBo(final ProductRepository productDao) {
        this.productDao = productDao;
    }

    @Transactional
    public Product create(final Product product) {
        final BigDecimal price = product.getPrice();

        if (Objects.isNull(price) || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException();
        }

        return productDao.save(product);
    }

    public List<Product> list() {
        return productDao.findAll();
    }
}
