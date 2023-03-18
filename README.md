# Запуск контейнера для тестов

Запуск этого контейнера, позволит не использовать testcontainers в тестах на разработческой машине и сэкономить порядка 1 секунды на запуске теста.

```shell
docker run --rm --detach --tmpfs /var -p 54311:5432 --env PGDATA=/var/lib/postgresql/data-no-mounted --env POSTGRES_DB=q6 --env POSTGRES_USER=q6 --env POSTGRES_PASSWORD=password --name q6-pg-tests postgres:15 
```