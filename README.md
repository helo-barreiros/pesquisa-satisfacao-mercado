# Pesquisa de Satisfação do Mercado

Projeto de **Banco de Dados I** · UEL · Prof. Dr. Anderson Paulo Avila Santos

Sistema web em que a gerência de um mercado cadastra questões, monta pesquisas, convida clientes e funcionários para responder e vê os resultados em relatórios.

**Integrante:** Heloísa Barreiros da Silva · 202501220682

## Natureza e particularidade

**Natureza:** pesquisa de satisfação.

**Particularidade:**

- As questões usam uma **escala de 1 a 5** e são **agrupadas por tema**: atendimento, limpeza, preços, filas etc. Também há questões de múltipla escolha e de texto livre.
- As pesquisas podem ser **anônimas**:
    - a tabela `convite` guarda **quem** já respondeu;
    - as tabelas `submissao` e `resposta` guardam **o que** foi respondido;
    - não existe ligação entre elas, e um gatilho no banco impede gravar o usuário numa submissão de pesquisa anônima.
- Cada pessoa responde cada pesquisa **uma única vez**, e só dentro do período da pesquisa.
- Os relatórios comparam **temas** e **grupos** de respondentes (Clube de Vantagens, cliente eventual e funcionário) num **período** escolhido.

## Modelagem

![DER](<docs/Diagrama pesquisa mercado.png>)

O esquema relacional e a justificativa da 3FN, tabela por tabela, estão em [docs/Esquema +3FN.pdf](<docs/Esquema +3FN.pdf>).

## Arquivos

| Arquivo | Conteúdo |
|---|---|
| `docs/Diagrama pesquisa mercado.png` | DER (notação de Chen) |
| `docs/Esquema +3FN.pdf` | esquema relacional e justificativa da 3FN |
| `database/schema.sql` | cria o banco do zero, com todas as restrições |
| `database/carga.sql` | dados de teste (rodar depois do `schema.sql`) |
| `database/relatorios.sql` | consultas dos relatórios, comentadas |
| `db-local/` | PostgreSQL local, sem instalação |
| `src/main/java/...` | back-end: controllers, services, DAOs (JDBC) e models |
| `src/main/resources/static/` | telas em HTML, CSS e JavaScript |

## Como rodar

### 1. Banco de dados

Escolha **uma** das opções.

**Opção A: banco local (mais simples).** Precisa só do Node.js 18+, sem instalar o PostgreSQL.

1. Dê dois cliques em `db-local/iniciar.cmd` e deixe a janela aberta enquanto usa o sistema.
2. Na primeira vez, ele instala as dependências e roda o `schema.sql` e o `carga.sql` sozinho.
3. Para voltar aos dados iniciais, feche a janela e use `db-local/resetar.cmd`.

A aplicação já vem configurada para esse banco. Não precisa mudar nada.

**Opção B: Neon (PostgreSQL na nuvem) ou outro PostgreSQL.**

1. Crie um projeto no [Neon](https://neon.tech) e abra o **SQL Editor**.
2. Cole e execute o `database/schema.sql` e depois o `database/carga.sql`, nessa ordem.
3. Na tela do Neon, clique em **Connect**, **desligue "Connection pooling"** e copie a string de conexão, que tem o formato
   `postgresql://USUARIO:SENHA@HOST/BANCO?sslmode=require`.
4. No IntelliJ, em **Run → Edit Configurations → Environment variables**, defina:
   ```
   DB_URL=jdbc:postgresql://HOST/BANCO?sslmode=require
   DB_USER=USUARIO
   DB_PASSWORD=SENHA
   DB_POOL_SIZE=5
   ```
   A URL começa com `jdbc:postgresql://` e **não** leva usuário nem senha.
5. Para voltar aos dados iniciais, rode de novo o `schema.sql` e o `carga.sql`.

### 2. Aplicação

Precisa do **Java 21 ou superior**.

1. Abra a pasta do projeto no IntelliJ. Ele reconhece o `pom.xml` sozinho.
2. Execute a classe `PesquisaApplication`, ou rode `mvn spring-boot:run` no terminal.
3. Abra **http://localhost:8090** no navegador.

### 3. Login

O login é só com o e-mail. A tela inicial tem botões de acesso rápido.

| Papel | E-mail | O que faz |
|---|---|---|
| Administração | `admin@mercado.com` | cadastra questões e pesquisas, convida usuários e vê os relatórios |
| Respondente | `heloisa.barreiros@email.com` (ou outro e-mail do `carga.sql`) | responde as pesquisas para as quais foi convidado |

Para testar uma resposta, use a **"Pesquisa Black Friday 2026"**: ela está aberta até 05/12/2026, e vários convidados ainda não responderam (por exemplo, `felipe.costa@email.com`).

## Relatórios

As consultas comentadas estão em `database/relatorios.sql`. Na tela, os valores de exemplo viram filtros.

Recursos SQL usados em cada relatório:

| Recurso | Rel. 1 | Rel. 2 | Rel. 3 |
|---|:-:|:-:|:-:|
| Junção interna (JOIN) | ✓ | ✓ | |
| Junção externa (LEFT JOIN) | | | ✓ |
| Agregação e agrupamento (COUNT, AVG, SUM, GROUP BY) | ✓ | ✓ | ✓ |
| HAVING | ✓ | ✓ | |
| Subconsulta | | ✓ | |
| Ordenação e ranking (ORDER BY, RANK, FETCH FIRST) | ✓ | ✓ | ✓ |
| Filtros (datas, IN, LIKE) | ✓ BETWEEN, IN | | ✓ LIKE |

### 1. Satisfação média por tema

**Objetivo:** comparar a média de cada tema num período, para os grupos escolhidos.

**Recursos:** JOIN, AVG, COUNT, SUM, GROUP BY, HAVING, ORDER BY, filtro de datas (BETWEEN) e IN.

![Relatório 1](docs/relatorio-01.png)

```sql
SELECT t.nome AS tema,
       ROUND(AVG(r.valor_escala), 2) AS media,
       COUNT(*) AS respostas,
       ROUND(100.0 * SUM(CASE WHEN r.valor_escala >= 4 THEN 1 ELSE 0 END) / COUNT(*), 1) AS pct_satisfeitos
FROM resposta r
JOIN questao   q ON q.id_questao = r.id_questao
JOIN tema      t ON t.id_tema    = q.id_tema
JOIN submissao s ON s.id_sub     = r.id_sub
WHERE r.valor_escala IS NOT NULL
  AND s.dt_envio BETWEEN DATE '2026-01-01' AND DATE '2026-12-31'
  AND s.id_grupo IN (1, 2, 3)
GROUP BY t.id_tema, t.nome
HAVING COUNT(*) >= 5
ORDER BY media DESC;
```

### 2. Ranking das questões com pior avaliação

**Objetivo:** mostrar as 10 questões com pior média, entre as que estão abaixo da média geral.

**Recursos:** JOIN, AVG, COUNT, SUM, GROUP BY, HAVING com subconsulta, RANK() e FETCH FIRST.

![Relatório 2](docs/relatorio-02.png)

```sql
SELECT RANK() OVER (ORDER BY AVG(r.valor_escala)) AS posicao,
       q.enunciado,
       t.nome AS tema,
       ROUND(AVG(r.valor_escala), 2) AS media,
       ROUND(100.0 * SUM(CASE WHEN r.valor_escala <= 2 THEN 1 ELSE 0 END) / COUNT(*), 1) AS pct_insatisfeitos,
       COUNT(*) AS respostas
FROM resposta r
JOIN questao q ON q.id_questao = r.id_questao
JOIN tema    t ON t.id_tema    = q.id_tema
WHERE r.valor_escala IS NOT NULL
GROUP BY q.id_questao, q.enunciado, t.nome
HAVING AVG(r.valor_escala) < (SELECT AVG(valor_escala) FROM resposta WHERE valor_escala IS NOT NULL)
ORDER BY posicao, q.id_questao
FETCH FIRST 10 ROWS ONLY;
```

### 3. Participação nas pesquisas

**Objetivo:** mostrar quantos convidados responderam cada pesquisa, com busca pelo título.

**Recursos:** LEFT JOIN (mantém pesquisas sem convites), COUNT, GROUP BY, ORDER BY e LIKE.

![Relatório 3](docs/relatorio-03.png)

```sql
SELECT p.titulo,
       to_char(p.inicio, 'DD/MM/YYYY') AS inicio,
       to_char(p.fim, 'DD/MM/YYYY') AS fim,
       COUNT(c.id_usuario) AS convidados,
       COUNT(c.dt_resp) AS responderam,
       COUNT(c.id_usuario) - COUNT(c.dt_resp) AS pendentes,
       ROUND(100.0 * COUNT(c.dt_resp) / NULLIF(COUNT(c.id_usuario), 0), 1) AS taxa
FROM pesquisa p
LEFT JOIN convite c ON c.id_pesquisa = p.id_pesquisa
WHERE LOWER(p.titulo) LIKE LOWER('%satisfação%')
GROUP BY p.id_pesquisa, p.titulo, p.inicio, p.fim
ORDER BY p.inicio;
```

## Tecnologias e arquitetura

| Parte | Tecnologia |
|---|---|
| Banco de dados | PostgreSQL (local com PGlite, ou Neon) |
| Back-end | Java 21+ (testado no 25) com Spring Boot e JDBC (`JdbcTemplate`), DAOs próprios, sem JPA |
| Front-end | HTML, CSS e JavaScript; gráficos de barras feitos com HTML/CSS |

O código segue as camadas **tela → controller → service → DAO → banco**. As gravações que mexem em mais de uma tabela rodam numa **transação** (`@Transactional`): cadastrar uma questão com as alternativas, montar a pesquisa, convidar e responder. Se algum passo falhar, nada fica gravado.
