package zw.co.tayanasoft.services;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import zw.co.tayanasoft.data.Shares;
import zw.co.tayanasoft.data.SharesRepository;

@Service
public class SharesService {

    private final SharesRepository repository;

    public SharesService(SharesRepository repository) {
        this.repository = repository;
    }

    public Optional<Shares> get(Long id) {
        return repository.findById(id);
    }

    public Shares update(Shares entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Shares> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Shares> list(Pageable pageable, Specification<Shares> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
