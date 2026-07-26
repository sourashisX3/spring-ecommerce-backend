package com.example.ecommerce_backend.modules.returns.service;

import com.example.ecommerce_backend.modules.returns.entity.ReturnStatus;
import com.example.ecommerce_backend.modules.returns.exception.ReturnStatusNotFoundException;
import com.example.ecommerce_backend.modules.returns.repository.ReturnStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReturnStatusService {

    @Autowired
    private ReturnStatusRepository returnStatusRepository;

    @Transactional
    public boolean toggleStatus(String uuid, boolean isActive) {
        ReturnStatus status = returnStatusRepository.findByUuid(uuid)
                .orElseThrow(() -> new ReturnStatusNotFoundException("Return status not found: " + uuid));
        if (status.isActive() == isActive) {
            return false;
        }
        status.setActive(isActive);
        returnStatusRepository.save(status);
        return true;
    }
}
