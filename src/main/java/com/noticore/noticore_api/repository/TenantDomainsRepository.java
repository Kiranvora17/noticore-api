package com.noticore.noticore_api.repository;

import com.noticore.noticore_api.entity.TenantDomains;
import com.noticore.noticore_api.enums.DomainStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantDomainsRepository extends JpaRepository<TenantDomains, UUID> {

    boolean existsByDomainNameAndTenants_Id(String domainName, UUID tenantId);
    List<TenantDomains> findAllByTenants_Id(UUID tenantId);
    Optional<TenantDomains> findByIdAndTenants_Id(UUID domainId, UUID tenantId);
    List<TenantDomains> findAllByStatus(DomainStatus status);
    Optional<TenantDomains> findByDomainName(String domainName);
}
