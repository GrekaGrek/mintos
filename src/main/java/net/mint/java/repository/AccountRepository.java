package net.mint.java.repository;

import net.mint.java.domain.AccountEntity;
import net.mint.java.domain.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    List<AccountEntity> findByCustomerId(Long id);
}
