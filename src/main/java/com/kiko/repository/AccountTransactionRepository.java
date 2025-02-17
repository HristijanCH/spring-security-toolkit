package com.kiko.repository;

import com.kiko.models.AccountTransaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AccountTransactionRepository extends CrudRepository<AccountTransaction, String> {

    List<AccountTransaction> findByCustomerIdOrderByTransactionDtDesc(long customerId);

}
