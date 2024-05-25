package zw.co.tayanasoft.services;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import zw.co.tayanasoft.data.StockExchange;
import zw.co.tayanasoft.data.StockExchangeRepository;

@Service
public class StockExchangeService {

    private final StockExchangeRepository repository;

    public StockExchangeService(StockExchangeRepository repository) {
        this.repository = repository;
    }

    public Optional<StockExchange> get(Long id) {
        return repository.findById(id);
    }

    public StockExchange update(StockExchange entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<StockExchange> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<StockExchange> list(Pageable pageable, Specification<StockExchange> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
