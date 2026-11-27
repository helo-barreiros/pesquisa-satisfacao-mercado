
-- Dados de teste
--Usuário entra só com o email 


-- Grupos 
INSERT INTO grupo (id_grupo, nome, descricao) VALUES
 (1, 'Cliente Clube de Vantagens', 'Clientes cadastrados no programa de fidelidade'),
 (2, 'Cliente Eventual',           'Clientes sem cadastro no programa de fidelidade'),
 (3, 'Funcionário',                'Colaboradores da loja, que também compram nela');


-- Usuários
INSERT INTO usuario (id_usuario, nome, email, papel, id_grupo)
SELECT v.id, v.nome, v.email, v.papel, v.grupo
FROM (VALUES
 ( 1, 'Gerência do Mercado', 'admin@mercado.com',             'ADMIN',       NULL),
 -- Clube de Vantagens
 ( 2, 'Ana Souza',           'ana.souza@email.com',           'RESPONDENTE', 1),
 ( 3, 'Bruno Lima',          'bruno.lima@email.com',          'RESPONDENTE', 1),
 ( 4, 'Carla Mendes',        'carla.mendes@email.com',        'RESPONDENTE', 1),
 ( 5, 'Daniel Rocha',        'daniel.rocha@email.com',        'RESPONDENTE', 1),
 ( 6, 'Eduarda Alves',       'eduarda.alves@email.com',       'RESPONDENTE', 1),
 ( 7, 'Felipe Costa',        'felipe.costa@email.com',        'RESPONDENTE', 1),
 ( 8, 'Gabriela Martins',    'gabriela.martins@email.com',    'RESPONDENTE', 1),
 ( 9, 'Henrique Dias',       'henrique.dias@email.com',       'RESPONDENTE', 1),
 (10, 'Isabela Ribeiro',     'isabela.ribeiro@email.com',     'RESPONDENTE', 1),
 (11, 'João Pereira',        'joao.pereira@email.com',        'RESPONDENTE', 1),
 (12, 'Karina Gomes',        'karina.gomes@email.com',        'RESPONDENTE', 1),
 (13, 'Lucas Ferreira',      'lucas.ferreira@email.com',      'RESPONDENTE', 1),
 (14, 'Mariana Barbosa',     'mariana.barbosa@email.com',     'RESPONDENTE', 1),
 -- Eventuais
 (15, 'Nicolas Araújo',      'nicolas.araujo@email.com',      'RESPONDENTE', 2),
 (16, 'Olívia Cardoso',      'olivia.cardoso@email.com',      'RESPONDENTE', 2),
 (17, 'Paulo Teixeira',      'paulo.teixeira@email.com',      'RESPONDENTE', 2),
 (18, 'Quésia Moreira',      'quesia.moreira@email.com',      'RESPONDENTE', 2),
 (19, 'Rafael Nunes',        'rafael.nunes@email.com',        'RESPONDENTE', 2),
 (20, 'Sofia Correia',       'sofia.correia@email.com',       'RESPONDENTE', 2),
 (21, 'Thiago Pinto',        'thiago.pinto@email.com',        'RESPONDENTE', 2),
 (22, 'Úrsula Castro',       'ursula.castro@email.com',       'RESPONDENTE', 2),
 (23, 'Vinícius Ramos',      'vinicius.ramos@email.com',      'RESPONDENTE', 2),
 (24, 'Yasmin Freitas',      'yasmin.freitas@email.com',      'RESPONDENTE', 2),
 (25, 'Wagner Lopes',        'wagner.lopes@email.com',        'RESPONDENTE', 2),
 -- Funcionários
 (26, 'Adriana Monteiro',    'adriana.monteiro@email.com',    'RESPONDENTE', 3),
 (27, 'Caio Batista',        'caio.batista@email.com',        'RESPONDENTE', 3),
 (28, 'Débora Vieira',       'debora.vieira@email.com',       'RESPONDENTE', 3),
 (29, 'Fábio Carvalho',      'fabio.carvalho@email.com',      'RESPONDENTE', 3),
 (30, 'Juliana Santos',      'juliana.santos@email.com',      'RESPONDENTE', 3),
 (31, 'Marcos Oliveira',     'marcos.oliveira@email.com',     'RESPONDENTE', 3)
) AS v(id, nome, email, papel, grupo);


-- Temas
INSERT INTO tema (id_tema, nome, descricao) VALUES
 ( 1, 'Atendimento',             'Cordialidade e disponibilidade dos funcionários'),
 ( 2, 'Limpeza e Organização',   'Limpeza da loja, corredores e sinalização'),
 ( 3, 'Preços e Promoções',      'Preços, ofertas e programa de fidelidade'),
 ( 4, 'Variedade de Produtos',   'Mix de produtos, marcas e reposição'),
 ( 5, 'Caixas e Filas',          'Tempo de espera e agilidade no pagamento'),
 ( 6, 'Hortifrúti',              'Frutas, verduras e legumes'),
 ( 7, 'Padaria e Açougue',       'Balcões de atendimento e produtos frescos'),
 ( 8, 'Estacionamento e Acesso', 'Vagas, segurança e acesso à loja'),
 ( 9, 'Hábitos de Compra',       'Perfil de compra do cliente (múltipla escolha)'),
 (10, 'Comentários e Sugestões', 'Respostas abertas');


-- Tipos de questão
INSERT INTO tipo_questao (id_tipo, codigo, descricao) VALUES
 (1, 'ESCALA',           'Escala de 1 (muito insatisfeito) a 5 (muito satisfeito)'),
 (2, 'MULTIPLA_ESCOLHA', 'Uma alternativa entre as cadastradas'),
 (3, 'TEXTO_LIVRE',      'Resposta aberta');


INSERT INTO questao (id_questao, id_tema, id_tipo, enunciado, data_criacao) VALUES
 -- Atendimento
 ( 1, 1, 1, 'Os funcionários são cordiais e prestativos.',                              '2026-01-02 10:00'),
 ( 2, 1, 1, 'Quando preciso de ajuda, encontro um funcionário com facilidade.',          '2026-01-02 10:00'),
 ( 3, 1, 1, 'Os funcionários sabem informar sobre os produtos.',                          '2026-01-02 10:00'),
 ( 4, 1, 1, 'De forma geral, como você avalia o atendimento?',                            '2026-01-02 10:00'),
 -- Limpeza e Organização
 ( 5, 2, 1, 'A loja está limpa.',                                                         '2026-01-02 10:00'),
 ( 6, 2, 1, 'Os corredores são organizados e fáceis de percorrer.',                       '2026-01-02 10:00'),
 ( 7, 2, 1, 'Encontro os produtos facilmente nas prateleiras.',                           '2026-01-02 10:00'),
 ( 8, 2, 1, 'Os banheiros estão limpos.',                                                 '2026-04-01 10:00'),
 -- Preços e Promoções
 ( 9, 3, 1, 'Os preços são justos em comparação com outros mercados.',                    '2026-01-02 10:00'),
 (10, 3, 1, 'As promoções são vantajosas.',                                               '2026-01-02 10:00'),
 (11, 3, 1, 'O preço da prateleira confere com o preço cobrado no caixa.',                '2026-01-02 10:00'),
 (12, 3, 1, 'O Clube de Vantagens traz descontos reais.',                                 '2026-07-20 10:00'),
 -- Variedade de Produtos
 (13, 4, 1, 'Encontro tudo o que preciso em uma única compra.',                           '2026-01-02 10:00'),
 (14, 4, 1, 'Há boa variedade de marcas.',                                                '2026-01-02 10:00'),
 (15, 4, 1, 'Raramente encontro produtos em falta.',                                      '2026-01-02 10:00'),
 (16, 4, 1, 'A loja oferece produtos para dietas especiais (sem glúten, sem lactose etc.).','2026-09-25 10:00'),
 -- Caixas e Filas
 (17, 5, 1, 'O tempo de espera na fila do caixa é aceitável.',                            '2026-01-02 10:00'),
 (18, 5, 1, 'Há caixas suficientes abertos nos horários de pico.',                        '2026-01-02 10:00'),
 (19, 5, 1, 'Os operadores de caixa são ágeis.',                                          '2026-01-02 10:00'),
 (20, 5, 1, 'O autoatendimento (self-checkout) é fácil de usar.',                         '2026-07-20 10:00'),
 -- Hortifrúti
 (21, 6, 1, 'As frutas e verduras são frescas.',                                          '2026-01-02 10:00'),
 (22, 6, 1, 'A variedade de frutas e verduras é boa.',                                    '2026-05-20 10:00'),
 (23, 6, 1, 'Os preços do hortifrúti são justos.',                                        '2026-05-20 10:00'),
 -- Padaria e Açougue
 (24, 7, 1, 'O pão é fresco e de boa qualidade.',                                         '2026-01-02 10:00'),
 (25, 7, 1, 'As carnes têm boa aparência e qualidade.',                                   '2026-01-02 10:00'),
 (26, 7, 1, 'O atendimento no balcão do açougue é rápido.',                               '2026-05-20 10:00'),
 (27, 7, 1, 'Os frios são fatiados do jeito que eu peço.',                                '2026-05-20 10:00'),
 -- Estacionamento e Acesso (nunca usadas)
 (28, 8, 1, 'O estacionamento tem vagas suficientes.',                                    '2026-10-01 10:00'),
 (29, 8, 1, 'É fácil chegar à loja de transporte público.',                               '2026-10-01 10:00'),
 -- Hábitos de Compra (múltipla escolha)
 (30, 9, 2, 'Com que frequência você faz compras aqui?',                                  '2026-01-02 10:00'),
 (31, 9, 2, 'Como você ficou sabendo das nossas promoções?',                              '2026-01-02 10:00'),
 (32, 9, 2, 'Em qual período você costuma fazer compras?',                                '2026-07-20 10:00'),
 (33, 9, 2, 'Qual forma de pagamento você mais usa?',                                     '2026-09-25 10:00'),
 -- Comentários (texto livre)
 (34, 10, 3, 'O que você mais gosta no mercado?',                                         '2026-01-02 10:00'),
 (35, 10, 3, 'O que deveríamos melhorar?',                                                '2026-01-02 10:00'),
 (36, 10, 3, 'Que produto você gostaria de encontrar aqui?',                              '2026-05-20 10:00');


-- Alternativas (4 por questão de múltipla escolha)
INSERT INTO alternativa (id_alternativa, id_questao, ordem, texto) VALUES
 ( 1, 30, 1, 'Quase todos os dias'),
 ( 2, 30, 2, 'Algumas vezes por semana'),
 ( 3, 30, 3, 'Uma vez por semana'),
 ( 4, 30, 4, 'Raramente'),
 ( 5, 31, 1, 'Folheto impresso'),
 ( 6, 31, 2, 'Aplicativo do Clube de Vantagens'),
 ( 7, 31, 3, 'Redes sociais'),
 ( 8, 31, 4, 'Na própria loja'),
 ( 9, 32, 1, 'Manhã'),
 (10, 32, 2, 'Tarde'),
 (11, 32, 3, 'Noite'),
 (12, 32, 4, 'Fim de semana'),
 (13, 33, 1, 'Dinheiro'),
 (14, 33, 2, 'Cartão de débito'),
 (15, 33, 3, 'Cartão de crédito'),
 (16, 33, 4, 'Pix');


-- Pesquisas 
INSERT INTO pesquisa (id_pesquisa, titulo, descricao, inicio, fim, anonima, criada_por) VALUES
 (1, 'Satisfação Geral — 1º bimestre 2026',
     'Pesquisa geral de satisfação, antes da reforma da loja.',
     '2026-01-10 08:00', '2026-02-28 22:00', TRUE, 1),
 (2, 'Pesquisa Pós-Reforma da Loja',
     'O que os clientes acharam da loja depois da reforma de março.',
     '2026-04-06 08:00', '2026-04-30 22:00', TRUE, 1),
 (3, 'Hortifrúti, Padaria e Açougue',
     'Avaliação identificada dos setores de frescos, para os membros do Clube.',
     '2026-06-01 08:00', '2026-06-20 22:00', FALSE, 1),
 (4, 'Satisfação Geral — Agosto 2026',
     'Pesquisa geral de satisfação, após a instalação do autoatendimento.',
     '2026-08-03 08:00', '2026-08-31 22:00', TRUE, 1),
 (5, 'Satisfação Geral — Outubro 2026',
     'Pesquisa geral de satisfação do segundo semestre.',
     '2026-10-05 08:00', '2026-10-31 22:00', TRUE, 1),
 (6, 'Pesquisa Black Friday 2026',
     'Como foi a experiência de compra na semana da Black Friday.',
     '2026-11-20 08:00', '2026-12-05 22:00', TRUE, 1);


-- Questões de cada pesquisa 
INSERT INTO pesquisa_questao (id_pesquisa, id_questao, ordem, obrigatoria)
SELECT p.id_pesquisa, q.id_questao, q.ordem::smallint,
       (SELECT id_tipo FROM questao WHERE id_questao = q.id_questao) <> 3  -- texto livre é opcional
FROM (VALUES
 (1, ARRAY[1,2,4,5,6,7,9,10,11,13,14,15,17,18,19,21,24,25,30,31,35]),
 (2, ARRAY[4,5,6,7,8,10,13,15,17,18,34,35]),
 (3, ARRAY[1,3,21,22,23,24,25,26,27,36]),
 (4, ARRAY[1,2,4,5,6,7,9,10,11,12,13,14,15,17,18,19,20,21,24,25,32,34,35]),
 (5, ARRAY[1,2,4,5,6,9,10,12,13,16,17,18,19,20,21,23,24,26,33,35]),
 (6, ARRAY[4,9,10,11,12,15,17,18,20,31,35])
) AS p(id_pesquisa, questoes)
CROSS JOIN LATERAL unnest(p.questoes) WITH ORDINALITY AS q(id_questao, ordem);


-- Convites (anônimo)
INSERT INTO convite (id_pesquisa, id_usuario, dt_convite)
SELECT p.id_pesquisa, u.id_usuario, p.inicio
FROM pesquisa p
JOIN usuario u ON u.papel = 'RESPONDENTE'
WHERE p.id_pesquisa <> 3 OR u.id_grupo = 1;


-- Dados sorteados
SELECT setseed(0.42);

-- Quem respondeu cada pesquisa e em que dia
CREATE TEMP TABLE tmp_quem_respondeu AS
SELECT c.id_pesquisa, c.id_usuario, u.id_grupo, p.anonima,
       (p.inicio::date
        + floor(random() * (LEAST(p.fim::date, DATE '2026-11-25') - p.inicio::date + 1))::int) AS dt_envio,
       random() AS sorteio_hora
FROM convite c
JOIN usuario  u ON u.id_usuario  = c.id_usuario
JOIN pesquisa p ON p.id_pesquisa = c.id_pesquisa
WHERE random() < CASE c.id_pesquisa
                    WHEN 1 THEN 0.75 WHEN 2 THEN 0.80 WHEN 3 THEN 0.85
                    WHEN 4 THEN 0.80 WHEN 5 THEN 0.85 ELSE 0.40 END;

-- convite QUEM respondeu com horário
UPDATE convite c
SET dt_resp = t.dt_envio + make_interval(mins => 540 + floor(t.sorteio_hora * 720)::int)
FROM tmp_quem_respondeu t
WHERE t.id_pesquisa = c.id_pesquisa AND t.id_usuario = c.id_usuario;

-- O QUE foi respondido nas pesquisas anônimas
INSERT INTO submissao (id_pesquisa, id_usuario, id_grupo, dt_envio)
SELECT t.id_pesquisa,
       CASE WHEN t.anonima THEN NULL ELSE t.id_usuario END,
       t.id_grupo,
       t.dt_envio
FROM tmp_quem_respondeu t
ORDER BY random();

-- Média esperada
CREATE TEMP TABLE tmp_cenario AS
SELECT c.id_tema, m.id_pesquisa::int AS id_pesquisa, m.media
FROM (VALUES
 (1, ARRAY[3.6, 3.8, 3.9, 4.0, 4.1, 4.0]),   -- Atendimento
 (2, ARRAY[2.9, 4.1, 4.0, 4.1, 4.2, 3.9]),   -- Limpeza
 (3, ARRAY[3.5, 3.3, 3.0, 2.8, 2.9, 3.7]),   -- Preços
 (4, ARRAY[3.8, 3.9, 3.9, 4.0, 4.1, 3.8]),   -- Variedade
 (5, ARRAY[2.4, 2.5, 2.6, 3.5, 3.7, 2.8]),   -- Caixa
 (6, ARRAY[4.0, 4.1, 4.3, 4.2, 4.4, 4.2]),   -- Hortifrúti
 (7, ARRAY[3.9, 3.8, 4.2, 4.1, 4.0, 4.0])    -- Padaria e Açougue
) AS c(id_tema, medias)
CROSS JOIN LATERAL unnest(c.medias) WITH ORDINALITY AS m(media, id_pesquisa);

-- Diferença de cada grupo em relação à média
CREATE TEMP TABLE tmp_ajuste_grupo (id_grupo INT, ajuste NUMERIC);
INSERT INTO tmp_ajuste_grupo VALUES (1, 0.25), (2, -0.15), (3, -0.35);

-- Respostas: escala (média do cenário + ajuste do grupo + ruído),
-- múltipla escolha (alternativa sorteada) e texto livre
INSERT INTO resposta (id_sub, id_questao, valor_escala, id_alternativa, texto)
WITH base AS MATERIALIZED (
    SELECT s.id_sub, s.id_pesquisa, s.id_grupo,
           q.id_questao, q.id_tipo, q.id_tema,
           random() AS r1, random() AS r2, random() AS r3
    FROM submissao s
    JOIN pesquisa_questao pq ON pq.id_pesquisa = s.id_pesquisa
    JOIN questao q           ON q.id_questao   = pq.id_questao
)
SELECT b.id_sub,
       b.id_questao,
       CASE WHEN b.id_tipo = 1 THEN
            LEAST(5, GREATEST(1, round(c.media + g.ajuste + (b.r1 + b.r2 - 1) * 1.7)))::smallint
       END,
       CASE WHEN b.id_tipo = 2 THEN a.id_alternativa END,
       CASE WHEN b.id_tipo = 3 THEN
            CASE b.id_questao
              WHEN 34 THEN (ARRAY[
                  'O hortifrúti é sempre fresco.',
                  'Atendimento simpático, principalmente na padaria.',
                  'A loja ficou muito mais bonita e organizada.',
                  'Os descontos do Clube de Vantagens.',
                  'É perto de casa e tem de tudo.',
                  'O pão francês quentinho no fim da tarde.'])[1 + floor(b.r2 * 6)::int]
              WHEN 35 THEN (ARRAY[
                  'Abrir mais caixas no horário de pico.',
                  'As filas no sábado estão muito longas.',
                  'Os preços subiram muito nos últimos meses.',
                  'Repor os produtos em falta mais rápido.',
                  'Etiquetas de preço desatualizadas em alguns corredores.',
                  'Melhorar a limpeza dos banheiros.',
                  'Mais funcionários nos corredores para ajudar.',
                  'Mais promoções em produtos de limpeza.'])[1 + floor(b.r2 * 8)::int]
              WHEN 36 THEN (ARRAY[
                  'Mais opções de produtos sem lactose.',
                  'Pão integral de fermentação natural.',
                  'Cortes especiais de carne para churrasco.',
                  'Frutas orgânicas.',
                  'Cervejas artesanais da região.'])[1 + floor(b.r2 * 5)::int]
            END
       END
FROM base b
LEFT JOIN tmp_cenario c      ON c.id_pesquisa = b.id_pesquisa AND c.id_tema = b.id_tema
LEFT JOIN tmp_ajuste_grupo g ON g.id_grupo    = b.id_grupo
LEFT JOIN alternativa a      ON b.id_tipo = 2
                            AND a.id_questao = b.id_questao
                            AND a.ordem = 1 + floor(b.r1 * 4)::int
WHERE b.id_tipo <> 3 OR b.r3 < 0.35;

DROP TABLE tmp_quem_respondeu;
DROP TABLE tmp_cenario;
DROP TABLE tmp_ajuste_grupo;


-- Ajusta as sequências das chaves

SELECT setval(pg_get_serial_sequence('grupo', 'id_grupo'),       (SELECT max(id_grupo)       FROM grupo));
SELECT setval(pg_get_serial_sequence('usuario',           'id_usuario'),     (SELECT max(id_usuario)     FROM usuario));
SELECT setval(pg_get_serial_sequence('tema',              'id_tema'),        (SELECT max(id_tema)        FROM tema));
SELECT setval(pg_get_serial_sequence('tipo_questao',      'id_tipo'),        (SELECT max(id_tipo)        FROM tipo_questao));
SELECT setval(pg_get_serial_sequence('questao',           'id_questao'),     (SELECT max(id_questao)     FROM questao));
SELECT setval(pg_get_serial_sequence('alternativa',       'id_alternativa'), (SELECT max(id_alternativa) FROM alternativa));
SELECT setval(pg_get_serial_sequence('pesquisa',          'id_pesquisa'),    (SELECT max(id_pesquisa)    FROM pesquisa));
