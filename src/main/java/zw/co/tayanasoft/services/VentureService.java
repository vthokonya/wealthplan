package zw.co.tayanasoft.services;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import zw.co.tayanasoft.data.Venture;
import zw.co.tayanasoft.data.VentureRepository;

@Service
public class VentureService {

    private final VentureRepository repository;

    public VentureService(VentureRepository repository) {
        this.repository = repository;
    }

    public Optional<Venture> get(Long id) {
        return repository.findById(id);
    }

    public Venture update(Venture entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Venture> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Venture> list(Pageable pageable, Specification<Venture> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
