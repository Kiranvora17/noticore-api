CREATE TABLE "tenants"(
    "id" UUID NOT NULL,
    "rapidapi_username" VARCHAR(255) NOT NULL,
    "creation_date" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "modified_date" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL
);
ALTER TABLE
    "tenants" ADD PRIMARY KEY("id");
ALTER TABLE
    "tenants" ADD CONSTRAINT "tenants_rapidapi_username_unique" UNIQUE("rapidapi_username");
CREATE TABLE "tenant_domains"(
    "id" UUID NOT NULL,
    "tenant_id" UUID NOT NULL,
    "domain_name" VARCHAR(255) NOT NULL,
    "dkim_token" TEXT NOT NULL,
    "status" VARCHAR(255) CHECK
        ("status" IN('PENDING', 'VERIFIED', 'FAILED')) NOT NULL,
        "creation_date" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
        "modified_date" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL
);
ALTER TABLE
    "tenant_domains" ADD PRIMARY KEY("id");
CREATE INDEX "tenant_domains_status_index" ON
    "tenant_domains"("status");
CREATE TABLE "email_notifications"(
    "id" UUID NOT NULL,
    "tenant_id" UUID NOT NULL,
    "domain" UUID NOT NULL,
    "from_email" VARCHAR(255) NOT NULL,
    "to_email" VARCHAR(255) NOT NULL,
    "subject" VARCHAR(255) NOT NULL,
    "body" TEXT NOT NULL,
    "status" VARCHAR(255) CHECK
        ("status" IN('QUEUED', 'PROCESSING', 'DELIVERED', 'FAILED', 'PERMANENTLY_FAILED')) NOT NULL,
        "retry_count" INTEGER NOT NULL DEFAULT 0,
        "next_retry_at" TIMESTAMP(0) WITHOUT TIME ZONE NULL,
        "error_message" TEXT NULL,
        "creation_date" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
        "modified_date" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL
);
ALTER TABLE
    "email_notifications" ADD PRIMARY KEY("id");
CREATE INDEX "email_notifications_status_index" ON
    "email_notifications"("status");
CREATE INDEX "email_notifications_next_retry_at_index" ON
    "email_notifications"("next_retry_at");
CREATE TABLE "notification_attempts"(
    "id" UUID NOT NULL,
    "notification_id" UUID NOT NULL,
    "attempted_at" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "status" VARCHAR(255) CHECK
        ("status" IN('SUCCESS', 'FAILED')) NOT NULL,
        "error_message" TEXT NULL
);
ALTER TABLE
    "notification_attempts" ADD PRIMARY KEY("id");
ALTER TABLE
    "tenant_domains" ADD CONSTRAINT "tenant_domains_tenant_id_foreign" FOREIGN KEY("tenant_id") REFERENCES "tenants"("id");
ALTER TABLE
    "email_notifications" ADD CONSTRAINT "email_notifications_domain_foreign" FOREIGN KEY("domain") REFERENCES "tenant_domains"("id");
ALTER TABLE
    "notification_attempts" ADD CONSTRAINT "notification_attempts_notification_id_foreign" FOREIGN KEY("notification_id") REFERENCES "email_notifications"("id");
ALTER TABLE
    "email_notifications" ADD CONSTRAINT "email_notifications_tenant_id_foreign" FOREIGN KEY("tenant_id") REFERENCES "tenants"("id");