package zw.co.tayanasoft.services;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import zw.co.tayanasoft.data.TradingPlatform;
import zw.co.tayanasoft.data.TradingPlatformRepository;

@Service
public class TradingPlatformService {

    private final TradingPlatformRepository repository;

    public TradingPlatformService(TradingPlatformRepository repository) {
        this.repository = repository;
    }

    public Optional<TradingPlatform> get(Long id) {
        return repository.findById(id);
    }

    public TradingPlatform update(TradingPlatform entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<TradingPlatform> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<TradingPlatform> list(Pageable pageable, Specification<TradingPlatform> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
