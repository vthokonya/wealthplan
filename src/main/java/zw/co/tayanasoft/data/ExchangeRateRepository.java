package zw.co.tayanasoft.data;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ExchangeRateRepository
        extends
            JpaRepository<ExchangeRate, Long>,
            JpaSpecificationExecutor<ExchangeRate> {

}
