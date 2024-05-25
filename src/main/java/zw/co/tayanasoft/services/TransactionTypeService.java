package zw.co.tayanasoft.services;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import zw.co.tayanasoft.data.TransactionType;
import zw.co.tayanasoft.data.TransactionTypeRepository;

@Service
public class TransactionTypeService {

    private final TransactionTypeRepository repository;

    public TransactionTypeService(TransactionTypeRepository repository) {
        this.repository = repository;
    }

    public Optional<TransactionType> get(Long id) {
        return repository.findById(id);
    }

    public TransactionType update(TransactionType entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<TransactionType> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<TransactionType> list(Pageable pageable, Specification<TransactionType> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
