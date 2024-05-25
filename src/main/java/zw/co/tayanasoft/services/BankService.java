package zw.co.tayanasoft.services;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import zw.co.tayanasoft.data.Bank;
import zw.co.tayanasoft.data.BankRepository;

@Service
public class BankService {

    private final BankRepository repository;

    public BankService(BankRepository repository) {
        this.repository = repository;
    }

    public Optional<Bank> get(Long id) {
        return repository.findById(id);
    }

    public Bank update(Bank entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Bank> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Bank> list(Pageable pageable, Specification<Bank> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
