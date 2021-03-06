package com.github.comrada.crypto.wbc.app.repository;

import com.github.comrada.crypto.wbc.app.entity.WalletEntity;
import com.github.comrada.crypto.wbc.app.entity.WalletId;
import com.github.comrada.crypto.wbc.domain.WalletStatus;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<WalletEntity, WalletId> {

  @Modifying(flushAutomatically = true)
  @Query(value = """
      update Wallet set locked = true
      where id.blockchain = :#{#walletId.blockchain} and id.address = :#{#walletId.address} and
      id.asset = :#{#walletId.asset} and locked = false
      """)
  void lock(WalletId walletId);

  @Modifying(flushAutomatically = true)
  @Query(value = """
      update Wallet set locked = false, checkedAt = :checkedAt
      where id.blockchain = :#{#walletId.blockchain} and id.address = :#{#walletId.address} and
      id.asset = :#{#walletId.asset} and locked = true
      """)
  void unlock(WalletId walletId, Instant checkedAt);

  @Modifying(flushAutomatically = true)
  @Query(value = """
      update Wallet set balance = :#{#walletEntity.balance}, checkedAt = :#{#walletEntity.checkedAt},
      locked = :#{#walletEntity.locked}
      where id.blockchain = :#{#walletEntity.id.blockchain} and id.address = :#{#walletEntity.id.address} and
      id.asset = :#{#walletEntity.id.asset}
      """)
  void update(WalletEntity walletEntity);


  @Modifying(flushAutomatically = true)
  @Query(value = """
      update Wallet set status = :status, locked = false, checkedAt = :checkedAt
      where id.blockchain = :#{#walletId.blockchain} and id.address = :#{#walletId.address} and
      id.asset = :#{#walletId.asset}
      """)
  void changeStatus(WalletId walletId, WalletStatus status, Instant checkedAt);
}
