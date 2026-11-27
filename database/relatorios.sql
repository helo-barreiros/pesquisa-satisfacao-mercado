
-- relatório 1 - Satisfação média por tema

SELECT t.nome                                   AS tema,
       ROUND(AVG(r.valor_escala), 2)            AS media,
       COUNT(*)                                 AS respostas,
       ROUND(100.0 * SUM(CASE WHEN r.valor_escala >= 4 THEN 1 ELSE 0 END) / COUNT(*), 1)
                                                AS pct_satisfeitos
FROM resposta  r
JOIN questao   q ON q.id_questao = r.id_questao
JOIN tema      t ON t.id_tema    = q.id_tema
JOIN submissao s ON s.id_sub     = r.id_sub
WHERE r.valor_escala IS NOT NULL
  AND s.dt_envio BETWEEN DATE '2026-01-01' AND DATE '2026-12-31'   -- filtro: período
  AND s.id_grupo IN (1, 2, 3)                                      -- filtro: grupos escolhidos
GROUP BY t.id_tema, t.nome
HAVING COUNT(*) >= 5                       -- ignora temas com poucas respostas
ORDER BY media DESC;


-- relatorio 2 - Ranking das questões com pior avaliação

SELECT RANK() OVER (ORDER BY AVG(r.valor_escala))  AS posicao,
       q.enunciado,
       t.nome                                      AS tema,
       ROUND(AVG(r.valor_escala), 2)               AS media,
       ROUND(100.0 * SUM(CASE WHEN r.valor_escala <= 2 THEN 1 ELSE 0 END) / COUNT(*), 1)
                                                   AS pct_insatisfeitos,
       COUNT(*)                                    AS respostas
FROM resposta r
JOIN questao  q ON q.id_questao = r.id_questao
JOIN tema     t ON t.id_tema    = q.id_tema
WHERE r.valor_escala IS NOT NULL
GROUP BY q.id_questao, q.enunciado, t.nome
HAVING AVG(r.valor_escala) < (SELECT AVG(r2.valor_escala)       -- subconsulta: média geral
                              FROM resposta r2
                              WHERE r2.valor_escala IS NOT NULL)
ORDER BY posicao, q.id_questao
FETCH FIRST 10 ROWS ONLY;


-- relatorio 3 - Participação nas pesquisas

SELECT p.titulo,
       to_char(p.inicio, 'DD/MM/YYYY')         AS inicio,
       to_char(p.fim, 'DD/MM/YYYY')            AS fim,
       COUNT(c.id_usuario)                     AS convidados,
       COUNT(c.dt_resp)                        AS responderam,
       COUNT(c.id_usuario) - COUNT(c.dt_resp)  AS pendentes,
       ROUND(100.0 * COUNT(c.dt_resp) / NULLIF(COUNT(c.id_usuario), 0), 1) AS taxa
FROM pesquisa p
LEFT JOIN convite c ON c.id_pesquisa = p.id_pesquisa
WHERE LOWER(p.titulo) LIKE LOWER('%' || 'satisfação' || '%')     -- filtro: texto da busca
GROUP BY p.id_pesquisa, p.titulo, p.inicio, p.fim
ORDER BY p.inicio;
