package zw.co.tayanasoft.services;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import zw.co.tayanasoft.data.Inflation;
import zw.co.tayanasoft.data.InflationRepository;

@Service
public class InflationService {

    private final InflationRepository repository;

    public InflationService(InflationRepository repository) {
        this.repository = repository;
    }

    public Optional<Inflation> get(Long id) {
        return repository.findById(id);
    }

    public Inflation update(Inflation entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Inflation> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Inflation> list(Pageable pageable, Specification<Inflation> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
