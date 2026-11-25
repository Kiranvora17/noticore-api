# Deploying Noticore on Azure for Students

The stack is four containers on one Ubuntu VM: the API, PostgreSQL, RabbitMQ and
Caddy (HTTPS). It starts with an empty database.

## Costs

- The subscription has a $100 credit for 12 months and can be renewed yearly while you
  are a student. When the credit runs out the subscription is disabled unless you move
  to pay as you go.
- `B2ats_v2`, `B1s` and `B2pts_v2` VMs include 750 free hours a month for 12 months.
  Use one of these if your allowed region offers it.
- Korea Central did not offer them to this subscription, so the VM is paid from the
  credit. Retail prices looked up on 2026-11-25, for 730 hours:

  | Item | USD per month |
  |---|---|
  | `Standard_B2ts_v2` VM | 9.49 |
  | Standard static public IPv4 | 3.65 |
  | 32 GiB Standard SSD (E4) | 2.40 |
  | **Total** | **15.54**, so $100 lasts about 6 months |

  Disk operations and heavy outbound traffic can add to this.
- Check actual spend under Cost Management.
- Add a budget alert under Cost Management > Budgets.

If the old AWS account still exists, terminate its resources so nothing keeps billing.

## 1. Create the VM

At <https://portal.azure.com>: Virtual machines > Create > Azure virtual machine.

| Setting | Value |
|---|---|
| Subscription | Azure for Students |
| Resource group | new, `noticore` |
| Name | `noticore` |
| Region | Korea Central (or another your subscription allows, see below) |
| Availability options | No infrastructure redundancy required |
| Image | Ubuntu Server 24.04 LTS, x64 |
| Size | `Standard_B2ts_v2` (2 vCPU, 1 GiB) |
| Authentication | SSH public key, generate new |
| Username | `kiran` (the deploy workflow expects this) |
| Inbound ports | SSH (22), HTTP (80), HTTPS (443) |
| OS disk | Standard SSD, image default size (30 GiB) |

Leave auto-shutdown off. Download the `.pem` key when prompted.

**"Requested resource was disallowed by Azure"** means the region is not allowed.
Open Policy > Assignments > "Allowed resource deployment regions" to see the list.

## 2. Give it a hostname

VM > Properties > DNS name label (under public IP address). Enter a label such as
`noticore-yourname` and save. Your hostname is shown on the Overview page:
`<label>.<region>.cloudapp.azure.com`. Check that the public IP is **Static**.

## 3. Prepare the server

In PowerShell, from the repository folder. Set these two variables first:

```powershell
$key    = "$HOME\Downloads\noticore-prod_key.pem"
$server = "<label>.<region>.cloudapp.azure.com"

# Windows OpenSSH rejects keys that other users can read
icacls $key /inheritance:r /grant:r "$($env:USERNAME):(R)"

scp -i $key deploy/setup-server.sh "kiran@${server}:"
ssh -i $key "kiran@$server" "bash setup-server.sh"

scp -i $key -r docker-compose.yml Caddyfile deploy "kiran@${server}:noticore/"
scp -i $key .env.example "kiran@${server}:noticore/.env"
scp -i $key src/main/resources/templates/drawSQL-pgsql-export-2026-04-15.sql "kiran@${server}:noticore/base-schema.sql"
```

## 4. Fill in the settings

```powershell
ssh -i $key "kiran@$server"
```

```bash
cd ~/noticore
sed -i 's/\r$//' .env deploy/*.sh
nano .env
chmod 600 .env
```

| Variable | Value |
|---|---|
| `APP_DOMAIN` | the hostname from step 2, without `https://` |
| `DB_PASSWORD`, `RABBITMQ_PASSWORD` | new random passwords (`openssl rand -hex 24`) |
| `BREVO_API_KEY` | Brevo > SMTP & API > API keys |
| `BREVO_SMTP_USERNAME`, `BREVO_SMTP_PASSWORD` | Brevo > SMTP & API > SMTP (login and SMTP key) |
| `BREVO_WEBHOOK_USERNAME`, `BREVO_WEBHOOK_PASSWORD` | you choose them; reuse them in step 7 |
| `RAPIDAPI_PROXY_SECRET` | RapidAPI provider dashboard |

## 5. Create the database tables (once only)

```bash
cd ~/noticore
docker compose up -d --wait postgres
docker compose exec -T postgres sh -c 'psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d noticore' < base-schema.sql
```

This step is required: the Liquibase changesets only modify these tables, so the API
fails to start on a completely empty database. They run automatically afterwards.

## 6. Deploy from GitHub

In the repository: Settings > Secrets and variables > Actions.

| Secret | Value |
|---|---|
| `VPS_HOST` | the hostname from step 2 |
| `VPS_SSH_KEY` | full contents of the `.pem` file |
| `DOCKER_USERNAME`, `DOCKER_PASSWORD` | your Docker Hub login (check they still work) |

Commit the deployment changes and push to `master`. The workflow builds the image,
copies the compose files to the server and starts everything.

The first start can take a few minutes on a small VM.

## 7. Point external services at the new server

- **Brevo webhook:** set the URL to `https://<APP_DOMAIN>/api/v1/webhooks/brevo` with
  the webhook username and password from `.env`.
- **Brevo authorised IPs:** if this restriction is on, add the VM's public IP address,
  otherwise API calls and sending are refused.
- **RapidAPI:** set the base URL to `https://<APP_DOMAIN>`.

The database is new, so sending domains registered before are unknown to the API and
must be registered again. If Brevo reports that a domain already exists, delete it in
the Brevo dashboard first.

## 8. Check it

```bash
curl https://<APP_DOMAIN>/api/v1/health      # {"status":"UP"}
cd ~/noticore && docker compose ps
docker compose logs --tail=100 app caddy
free -h
```

Then send one test email through RapidAPI and confirm its status becomes `DELIVERED`.

## 9. Back up

On the server, run `crontab -e` and add:

```
0 2 * * * bash /home/kiran/noticore/deploy/backup-db.sh
```

Backups stay on the same VM, so copy them to your PC now and then:

```powershell
scp -i $key "kiran@${server}:noticore/backups/*.dump" .
```

RabbitMQ management UI: `ssh -i $key -L 15672:localhost:15672 "kiran@$server"`,
then open <http://localhost:15672>.
