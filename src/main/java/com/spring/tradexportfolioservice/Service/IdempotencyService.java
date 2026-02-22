package com.spring.tradexportfolioservice.Service;

import com.spring.tradexportfolioservice.Enums.IdempotencyStatus;
import com.spring.tradexportfolioservice.Exception.ConcurrentRequestException;
import com.spring.tradexportfolioservice.Models.IdempotencyKey;
import com.spring.tradexportfolioservice.Repository.IdempotencyKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {

    private final IdempotencyKeyRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public IdempotencyKey createOrReturnKey(String keyString, Long userId) {
        if (keyString == null || keyString.trim().isEmpty()) {
            return null;
        }

        Optional<IdempotencyKey> existingKeyOpt = repository.findByKeyValue(keyString);

        if (existingKeyOpt.isPresent()) {
            IdempotencyKey existingKey = existingKeyOpt.get();

            if (existingKey.getStatus() == IdempotencyStatus.PENDING) {
                log.warn("Concurrent request detected for idempotency key: {}", keyString);
                throw new ConcurrentRequestException("Request is already processing. Please wait.");
            }
            return existingKey;
        }

        IdempotencyKey newKey = IdempotencyKey.create(keyString, userId);
        return repository.save(newKey);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markCompleted(String keyString) {
        if (keyString == null || keyString.trim().isEmpty())
            return;

        repository.findByKeyValue(keyString).ifPresent(key -> {
            key.markCompleted();
            repository.save(key);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(String keyString) {
        if (keyString == null || keyString.trim().isEmpty())
            return;

        repository.findByKeyValue(keyString).ifPresent(key -> {
            key.markFailed();
            repository.save(key);
        });
    }
}
