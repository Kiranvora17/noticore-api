package com.noticore.noticore_api.task;

import com.noticore.noticore_api.entity.TenantDomains;
import com.noticore.noticore_api.enums.DomainStatus;
import com.noticore.noticore_api.exception.brevo.BrevoDomainNotFoundException;
import com.noticore.noticore_api.repository.TenantDomainsRepository;
import com.noticore.noticore_api.service.ITenantDomainsPersistenceService;
import com.noticore.noticore_api.service.external.IBrevoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshDomainStatus {

    private final ReentrantLock reentrantLock;
    private final TenantDomainsRepository tenantDomainsRepository;
    private final IBrevoService iBrevoService;
    private final ITenantDomainsPersistenceService iTenantDomainsPersistenceService;

    @Scheduled(fixedDelay = 240000)
    @Async
    public void refreshDomainStatus() {
        if(!reentrantLock.tryLock()) {
            log.info("Another domain status refresh job is already running");
            return;
        }

        try {
            log.info("inside method: refreshDomainStatus()");
            List<TenantDomains> tenantDomains = tenantDomainsRepository
                    .findAllByStatus(DomainStatus.PENDING);

            if(tenantDomains.isEmpty()) {
                log.info("No domains is present to check verification status.");
                return;
            }

            for (TenantDomains domain : tenantDomains) {
                refreshSingleDomain(domain);
            }

            log.info("refresh domain status completed successfully.");
        } catch (Exception e) {
            log.error("Error while refreshing domain verification status: {}", e.getMessage(), e);
        } finally {
            reentrantLock.unlock();
        }
    }

    private void refreshSingleDomain(TenantDomains domain) {
        String domainName = domain.getDomainName();

        try {
            boolean authenticated = iBrevoService.getDomainStatus(domainName);

            if (authenticated) {
                iTenantDomainsPersistenceService.updateDomainVerificationStatus(domain, DomainStatus.VERIFIED);
            } else {
                log.info("Domain {} is still pending verification.", domainName);
            }
        } catch (BrevoDomainNotFoundException e) {
            log.warn("Domain {} not found on Brevo. Marking as FAILED.", domainName);
            iTenantDomainsPersistenceService.updateDomainVerificationStatus(domain, DomainStatus.FAILED);
        } catch (Exception e) {
            log.error("Error while checking domain status for {}: {}", domainName, e.getMessage(), e);
        }
    }

}
