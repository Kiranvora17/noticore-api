package com.noticore.noticore_api.repository;

import com.noticore.noticore_api.entity.TenantDomains;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TenantDomainsRepository extends JpaRepository<TenantDomains, UUID> {

    boolean existsByDomainNameAndTenants_Id(String domainName, UUID tenantId);
}
