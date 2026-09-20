# API Agencia de Viagens (Desafio 2 - Desenvolvimento de Sistemas Web)

Evolucao da API RESTful de destinos turisticos: os dados deixaram de ser mantidos em memoria e passaram a ser
persistidos em **PostgreSQL** com **Spring Data JPA**, e a API passou a contar com **autenticacao** e
**autorizacao por perfil de acesso** usando **Spring Security**.

## Sumario

1. [Stack e requisitos](#stack-e-requisitos)
2. [Arquitetura em camadas](#arquitetura-em-camadas)
3. [Modelo de dados](#modelo-de-dados)
4. [Configuracao do banco de dados](#configuracao-do-banco-de-dados)
5. [Configuracao da aplicacao](#configuracao-da-aplicacao)
6. [Como executar](#como-executar)
7. [Usuarios e perfis de teste](#usuarios-e-perfis-de-teste)
8. [Seguranca: como autenticar](#seguranca-como-autenticar)
9. [Matriz de permissoes](#matriz-de-permissoes)
10. [Endpoints e exemplos de uso](#endpoints-e-exemplos-de-uso)
11. [Respostas de erro](#respostas-de-erro)
12. [Memoria x persistencia em banco](#memoria-x-persistencia-em-banco)
13. [Atendimento aos criterios de avaliacao](#atendimento-aos-criterios-de-avaliacao)

## Stack e requisitos

| Item | Versao |
|------|--------|
| Java | 17 ou superior |
| Spring Boot | 3.3.4 |
| Spring Web, Spring Data JPA, Spring Security, Bean Validation | starters do Boot 3.3.4 |
| PostgreSQL | 14 ou superior |
| Maven | 3.9 ou superior |
| springdoc-openapi (Swagger UI) | 2.6.0 |

Verifique o ambiente antes de iniciar:

```bash
java -version
mvn -version
psql --version
```

## Arquitetura em camadas

A separacao entre controller, service e repository foi mantida. O controller nunca acessa o banco diretamente:
ele conversa apenas com a camada de servico, que usa os repositories do Spring Data JPA.

```
src/main/java/com/senai/agenciaviagens
├── AgenciaViagensApplication.java
├── config
│   ├── SecurityConfig.java          regras de acesso, BCrypt, HTTP Basic, handlers 401 e 403
│   ├── OpenApiConfig.java           documentacao interativa com autenticacao
│   └── DataSeeder.java              carga inicial de perfis, usuarios e destinos
├── controller
│   ├── DestinoController.java       CRUD protegido de destinos
│   ├── DestinoPublicoController.java vitrine publica (sem autenticacao)
│   ├── AvaliacaoController.java     avaliacoes dos destinos
│   ├── UsuarioController.java       gestao de usuarios (somente ADMIN)
│   └── AuthController.java          login e dados do usuario autenticado
├── domain                           entidades JPA: Destino, Avaliacao, Usuario, Perfil
├── repository                       interfaces Spring Data JPA
├── dto                              records de entrada e saida com Bean Validation
├── mapper                           conversao entre entidade e DTO
├── service                          regras de negocio e transacoes
├── security
│   └── UsuarioDetailsService.java   carrega usuarios e perfis direto do banco
└── exception                        excecoes de dominio e handler global
```

## Modelo de dados

As tabelas sao criadas automaticamente pelo Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

| Tabela | Descricao | Relacionamentos |
|--------|-----------|-----------------|
| `destinos` | destino turistico comercializado pela agencia | 1 para N com `avaliacoes` |
| `avaliacoes` | nota de 1 a 5 e comentario de um usuario sobre um destino | N para 1 com `destinos` e com `usuarios` |
| `usuarios` | credenciais e dados do usuario da API | N para N com `perfis` |
| `perfis` | perfis de acesso `ROLE_ADMIN` e `ROLE_USER` | N para N com `usuarios` |
| `usuario_perfis` | tabela de juncao entre usuarios e perfis | - |

Regras de integridade aplicadas:

- `destinos.nome` e unico.
- `usuarios.username` e unico.
- `avaliacoes` possui chave unica composta por `destino_id` e `usuario_id`, ou seja, cada usuario avalia um destino uma unica vez.
- excluir um destino remove suas avaliacoes em cascata.

## Configuracao do banco de dados

### Opcao 1: Docker Compose (mais rapido)

```bash
docker compose up -d
```

O container sobe o PostgreSQL 16 na porta 5432 com banco `agencia_viagens`, usuario `postgres` e senha `postgres`,
que sao exatamente os valores padrao da aplicacao.

### Opcao 2: PostgreSQL instalado localmente

```bash
psql -U postgres -c "CREATE DATABASE agencia_viagens;"
```

Ou execute o script pronto:

```bash
psql -U postgres -f scripts/01_criar_banco.sql
```

Nao e necessario criar tabelas manualmente. Ao subir a aplicacao, o Hibernate cria o schema e o `DataSeeder`
insere perfis, usuarios de teste e quatro destinos iniciais.

## Configuracao da aplicacao

Arquivo `src/main/resources/application.properties`. Todos os valores sensiveis aceitam variaveis de ambiente,
com valores padrao para o ambiente local:

```properties
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:agencia_viagens}
spring.datasource.username=${DB_USER:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
spring.jpa.show-sql=true
```

| Variavel | Padrao | Funcao |
|----------|--------|--------|
| `DB_HOST` | localhost | host do PostgreSQL |
| `DB_PORT` | 5432 | porta do PostgreSQL |
| `DB_NAME` | agencia_viagens | nome do banco |
| `DB_USER` | postgres | usuario do banco |
| `DB_PASSWORD` | postgres | senha do banco |
| `SEED_ENABLED` | true | habilita a carga inicial de dados |
| `SEED_ADMIN_USER` / `SEED_ADMIN_PASSWORD` | admin / admin123 | credenciais do administrador de teste |
| `SEED_USER_USER` / `SEED_USER_PASSWORD` | maria / user123 | credenciais do usuario comum de teste |
| `SERVER_PORT` | 8080 | porta da API |

Exemplo alterando credenciais sem editar o arquivo:

```bash
DB_USER=agencia_app DB_PASSWORD=agencia123 mvn spring-boot:run
```

## Como executar

```bash
git clone <url-do-repositorio>
cd agencia-viagens-api

docker compose up -d

mvn clean spring-boot:run
```

Gerando e executando o jar:

```bash
mvn clean package
java -jar target/agencia-viagens-api-2.0.0.jar
```

A API sobe em `http://localhost:8080`.
Documentacao interativa em `http://localhost:8080/swagger-ui.html` (use o botao Authorize para informar usuario e senha).

Teste rapido de disponibilidade, sem autenticacao:

```bash
curl http://localhost:8080/api/publico/destinos
```

## Usuarios e perfis de teste

Criados automaticamente na primeira execucao, com senha gravada em hash BCrypt.

| Username | Senha | Perfis | Uso sugerido |
|----------|-------|--------|--------------|
| `admin` | `admin123` | ADMIN e USER | cadastrar, atualizar e excluir destinos, gerenciar usuarios |
| `maria` | `user123` | USER | consultar destinos e avaliar |

As senhas nunca sao armazenadas em texto puro. O `BCryptPasswordEncoder` gera o hash no cadastro e o Spring Security
valida o hash no login.

## Seguranca: como autenticar

A API e stateless e usa autenticacao HTTP Basic. Cada requisicao protegida envia o cabecalho `Authorization`:

```bash
curl -u admin:admin123 http://localhost:8080/api/auth/me
```

Equivalente com o cabecalho montado manualmente:

```bash
curl -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" http://localhost:8080/api/auth/me
```

Resposta:

```json
{
  "username": "admin",
  "nome": "Administrador da Agencia",
  "perfis": ["ADMIN", "USER"],
  "autenticado": true
}
```

Fluxo interno da autenticacao:

1. o filtro do Spring Security le o cabecalho `Authorization`;
2. o `UsuarioDetailsService` busca o usuario na tabela `usuarios` pelo username;
3. os perfis vinculados em `usuario_perfis` viram authorities (`ROLE_ADMIN`, `ROLE_USER`);
4. o `BCryptPasswordEncoder` compara a senha enviada com o hash salvo;
5. as regras do `SecurityConfig` e as anotacoes `@PreAuthorize` decidem se a operacao e permitida.

## Matriz de permissoes

| Metodo e rota | Publico | USER | ADMIN |
|---------------|:-------:|:----:|:-----:|
| `GET /api/publico/destinos` | sim | sim | sim |
| `GET /api/publico/destinos/{id}` | sim | sim | sim |
| `POST /api/auth/login` | nao | sim | sim |
| `GET /api/auth/me` | nao | sim | sim |
| `GET /api/auth/perfil` | nao | sim | sim |
| `GET /api/destinos` | nao | sim | sim |
| `GET /api/destinos/{id}` | nao | sim | sim |
| `POST /api/destinos` | nao | nao | sim |
| `PUT /api/destinos/{id}` | nao | nao | sim |
| `DELETE /api/destinos/{id}` | nao | nao | sim |
| `GET /api/destinos/{id}/avaliacoes` | nao | sim | sim |
| `POST /api/destinos/{id}/avaliacoes` | nao | sim | sim |
| `DELETE /api/avaliacoes/{id}` | nao | nao | sim |
| `GET /api/usuarios` | nao | nao | sim |
| `POST /api/usuarios` | nao | nao | sim |
| `PATCH /api/usuarios/{id}/status` | nao | nao | sim |
| `DELETE /api/usuarios/{id}` | nao | nao | sim |

A protecao e aplicada em duas camadas: regras por rota no `SecurityFilterChain` e anotacoes `@PreAuthorize` nos
controllers, garantindo que as operacoes sensiveis fiquem restritas ao perfil ADMIN.

## Endpoints e exemplos de uso

O arquivo `requests.http` na raiz do projeto contem todas as chamadas prontas para o IntelliJ e para a extensao
REST Client do VS Code.

### Vitrine publica

```bash
curl http://localhost:8080/api/publico/destinos
```

### Acesso sem credenciais a um recurso protegido

```bash
curl -i http://localhost:8080/api/destinos
```

```
HTTP/1.1 401
{"status":401,"erro":"Nao autenticado","mensagem":"Informe usuario e senha validos no cabecalho Authorization","caminho":"/api/destinos"}
```

### Listar destinos com perfil USER

```bash
curl -u maria:user123 http://localhost:8080/api/destinos
```

```json
[
  {
    "id": 3,
    "nome": "Bariloche",
    "pais": "Argentina",
    "cidade": "San Carlos de Bariloche",
    "descricao": "Destino de montanha com lagos, esqui no inverno e trilhas no verao",
    "precoMedio": 5400.00,
    "duracaoDias": 7,
    "ativo": true,
    "notaMedia": 4.50,
    "totalAvaliacoes": 2,
    "criadoEm": "2026-09-20T10:15:32"
  }
]
```

### Filtros de consulta

```bash
curl -u maria:user123 "http://localhost:8080/api/destinos?pais=Brasil"
curl -u maria:user123 "http://localhost:8080/api/destinos?busca=serra"
```

### Cadastrar destino com perfil ADMIN

```bash
curl -u admin:admin123 -X POST http://localhost:8080/api/destinos \
  -H "Content-Type: application/json" \
  -d '{
        "nome": "Ilha Grande",
        "pais": "Brasil",
        "cidade": "Angra dos Reis",
        "descricao": "Trilhas e praias sem transito de carros",
        "precoMedio": 2890.00,
        "duracaoDias": 5,
        "ativo": true
      }'
```

Retorna `201 Created` com o cabecalho `Location` apontando para o novo recurso.

### Tentativa de cadastro com perfil USER

```bash
curl -i -u maria:user123 -X POST http://localhost:8080/api/destinos \
  -H "Content-Type: application/json" \
  -d '{"nome":"Teste","pais":"Brasil","cidade":"Teste","precoMedio":100.00,"duracaoDias":2}'
```

```
HTTP/1.1 403
{"status":403,"erro":"Acesso negado","mensagem":"O perfil autenticado nao possui permissao para esta operacao","caminho":"/api/destinos"}
```

### Atualizar e excluir destino

```bash
curl -u admin:admin123 -X PUT http://localhost:8080/api/destinos/1 \
  -H "Content-Type: application/json" \
  -d '{"nome":"Fernando de Noronha","pais":"Brasil","cidade":"Fernando de Noronha","descricao":"Pacote atualizado","precoMedio":8190.00,"duracaoDias":6,"ativo":true}'

curl -u admin:admin123 -X DELETE http://localhost:8080/api/destinos/5
```

### Avaliar um destino

```bash
curl -u maria:user123 -X POST http://localhost:8080/api/destinos/1/avaliacoes \
  -H "Content-Type: application/json" \
  -d '{"nota": 5, "comentario": "Viagem impecavel do inicio ao fim"}'
```

```json
{
  "id": 1,
  "nota": 5,
  "comentario": "Viagem impecavel do inicio ao fim",
  "destinoId": 1,
  "destinoNome": "Fernando de Noronha",
  "autor": "maria",
  "criadoEm": "2026-09-20T10:32:04"
}
```

O autor da avaliacao vem do usuario autenticado, nunca do corpo da requisicao.

### Cadastrar um novo usuario (apenas ADMIN)

```bash
curl -u admin:admin123 -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{
        "username": "joao",
        "senha": "joao123456",
        "nome": "Joao Cliente",
        "email": "joao@agenciaviagens.com",
        "perfis": ["USER"]
      }'
```

A senha e gravada com hash BCrypt e o novo usuario ja pode autenticar na API.

## Respostas de erro

| Situacao | Status |
|----------|--------|
| campos invalidos no corpo da requisicao | 400 |
| regra de negocio violada, como nome de destino duplicado ou avaliacao repetida | 400 |
| credenciais ausentes ou invalidas | 401 |
| perfil sem permissao para a operacao | 403 |
| destino, avaliacao ou usuario inexistente | 404 |

Exemplo de erro de validacao:

```json
{
  "timestamp": "2026-09-20T10:41:12",
  "status": 400,
  "erro": "Validacao falhou",
  "mensagem": "Existem campos invalidos na requisicao",
  "caminho": "/api/destinos",
  "campos": {
    "nome": "O nome do destino e obrigatorio",
    "duracaoDias": "A duracao minima e de 1 dia"
  }
}
```

## Memoria x persistencia em banco

| Aspecto | Versao anterior (memoria) | Versao atual (PostgreSQL) |
|---------|---------------------------|---------------------------|
| Onde os dados ficam | lista em memoria dentro da aplicacao | tabelas do PostgreSQL |
| Ao reiniciar a aplicacao | todos os dados sao perdidos | os dados permanecem |
| Varias instancias da API | cada instancia com dados proprios | todas compartilham o mesmo banco |
| Geracao de identificadores | contador manual | `GenerationType.IDENTITY` no banco |
| Consultas e filtros | percorrendo listas em Java | query methods e JPQL do Spring Data JPA |
| Integridade dos dados | validada apenas no codigo | chaves unicas, chaves estrangeiras e restricoes no banco |
| Transacoes | inexistentes | controladas com `@Transactional` |

## Atendimento aos criterios de avaliacao

| Criterio | Onde esta implementado |
|----------|------------------------|
| Integracao com PostgreSQL | `application.properties`, `docker-compose.yml`, `scripts/01_criar_banco.sql` |
| Mapeamento das entidades JPA | `domain/Destino.java`, `domain/Avaliacao.java`, `domain/Usuario.java`, `domain/Perfil.java` |
| Uso de Spring Data JPA | pacote `repository`, com query methods e JPQL, utilizados somente pelos services |
| Adaptacao da camada de servico | `service/DestinoService.java`, `service/AvaliacaoService.java`, `service/UsuarioService.java` |
| Autenticacao | `security/UsuarioDetailsService.java` e `config/SecurityConfig.java` com BCrypt e HTTP Basic |
| Autorizacao por perfil | regras por rota no `SecurityConfig` e `@PreAuthorize` nos controllers |
| Protecao de operacoes sensiveis | cadastro, atualizacao, exclusao e gestao de usuarios restritos ao perfil ADMIN |
| Organizacao em camadas | controller, service, repository, domain, dto, mapper, config, exception |
| Tratamento de erros | `exception/ApiExceptionHandler.java` |
| Documentacao tecnica | este README, o `requests.http` e o Swagger UI |
