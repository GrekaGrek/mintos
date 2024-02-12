package net.mint.java.repository;

import net.mint.java.domain.TransferEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<TransferEntity, Long> {
    Page<TransferEntity> findBySourceAccountIdOrderByDateTimeDesc(Long accountId, Pageable pageable);
}

