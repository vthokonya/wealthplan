package zw.co.tayanasoft.services;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import zw.co.tayanasoft.data.ExchangeRate;
import zw.co.tayanasoft.data.ExchangeRateRepository;

@Service
public class ExchangeRateService {

    private final ExchangeRateRepository repository;

    public ExchangeRateService(ExchangeRateRepository repository) {
        this.repository = repository;
    }

    public Optional<ExchangeRate> get(Long id) {
        return repository.findById(id);
    }

    public ExchangeRate update(ExchangeRate entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<ExchangeRate> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<ExchangeRate> list(Pageable pageable, Specification<ExchangeRate> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
