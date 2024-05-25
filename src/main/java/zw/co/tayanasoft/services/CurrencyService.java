package zw.co.tayanasoft.services;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import zw.co.tayanasoft.data.Currency;
import zw.co.tayanasoft.data.CurrencyRepository;

@Service
public class CurrencyService {

    private final CurrencyRepository repository;

    public CurrencyService(CurrencyRepository repository) {
        this.repository = repository;
    }

    public Optional<Currency> get(Long id) {
        return repository.findById(id);
    }

    public Currency update(Currency entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Currency> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Currency> list(Pageable pageable, Specification<Currency> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
