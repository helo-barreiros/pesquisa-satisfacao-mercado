// Tela de relatórios (RF7): cada relatório tem um gráfico de barras e uma tabela.

async function iniciar() {
  await iniciarPagina({ admin: true, ativo: 'relatorios.html' });

  // filtros do relatório 1
  const hoje = new Date();
  document.getElementById('inicio').value = `${hoje.getFullYear()}-01-01`;
  document.getElementById('fim').value = `${hoje.getFullYear()}-12-31`;
  const grupos = await API.get('/api/admin/grupos');
  document.getElementById('grupos').innerHTML = grupos.map((g) =>
    `<label><input type="checkbox" name="grupo" value="${g.id}" checked>${esc(g.nome)}</label>`).join('');

  document.getElementById('filtro-temas').onsubmit = (ev) => { ev.preventDefault(); carregarTemas(); };
  document.getElementById('filtro-participacao').onsubmit = (ev) => { ev.preventDefault(); carregarParticipacao(); };

  carregarTemas();
  carregarRanking();
  carregarParticipacao();
}


// itens = [{ rotulo, valor, texto }], max = valor que ocupa a barra inteira
function desenharBarras(idGrafico, itens, max) {
  document.getElementById(idGrafico).innerHTML = itens.map((i) => `
    <div class="barra" title="${esc(i.rotulo)}: ${esc(i.texto)}">
      <span class="rotulo">${esc(i.rotulo)}</span>
      <span class="trilho"><span class="cor-verde" style="width:${(100 * i.valor) / max}%"></span></span>
      <span class="num">${esc(i.texto)}</span>
    </div>`).join('');
}

function numero(v, casas = 2) {
  return Number(v).toLocaleString('pt-BR', { minimumFractionDigits: casas, maximumFractionDigits: casas });
}

function linhaVazia(idTabela, colunas, texto) {
  document.getElementById(idTabela).innerHTML = `<tr><td colspan="${colunas}" class="vazio">${texto}</td></tr>`;
}

// ---------- Relatório 1 ----------
async function carregarTemas() {
  const grupos = [...document.querySelectorAll('[name="grupo"]:checked')].map((c) => c.value);
  const params = new URLSearchParams({
    inicio: document.getElementById('inicio').value,
    fim: document.getElementById('fim').value,
    grupos: grupos.join(','),
  });
  let linhas;
  try {
    linhas = await API.get('/api/admin/relatorios/temas?' + params);
  } catch (e) {
    aviso(e.message, 'erro');
    return;
  }
  desenharBarras('grafico-temas', linhas.map((l) => ({ rotulo: l.tema, valor: l.media, texto: numero(l.media) })), 5);
  if (linhas.length === 0) {
    linhaVazia('tabela-temas', 4, 'Nenhuma resposta nesse período para esses grupos.');
    return;
  }
  document.getElementById('tabela-temas').innerHTML = linhas.map((l) => `
    <tr><td>${esc(l.tema)}</td><td class="num">${numero(l.media)}</td>
      <td class="num">${numero(l.pct_satisfeitos, 1)}%</td><td class="num">${l.respostas}</td></tr>`).join('');
}

// ---------- Relatório 2 ----------
async function carregarRanking() {
  const linhas = await API.get('/api/admin/relatorios/ranking');
  desenharBarras('grafico-ranking',
    linhas.map((l) => ({ rotulo: `${l.posicao}º ${l.enunciado}`, valor: l.media, texto: numero(l.media) })), 5);
  if (linhas.length === 0) {
    linhaVazia('tabela-ranking', 6, 'Ainda não há respostas suficientes.');
    return;
  }
  document.getElementById('tabela-ranking').innerHTML = linhas.map((l) => `
    <tr><td class="num">${l.posicao}º</td><td>${esc(l.enunciado)}</td><td>${esc(l.tema)}</td>
      <td class="num">${numero(l.media)}</td><td class="num">${numero(l.pct_insatisfeitos, 1)}%</td>
      <td class="num">${l.respostas}</td></tr>`).join('');
}

// ---------- Relatório 3 ----------
async function carregarParticipacao() {
  const busca = document.getElementById('busca').value;
  const linhas = await API.get('/api/admin/relatorios/participacao?' + new URLSearchParams({ busca }));

  // parte verde = responderam, parte cinza = pendentes (100% = convidados)
  document.getElementById('grafico-participacao').innerHTML = linhas.map((l) => {
    const verde = l.convidados ? (100 * l.responderam) / l.convidados : 0;
    const taxa = l.taxa === null ? 'sem convites' : `${numero(l.taxa, 1)}%`;
    return `
      <div class="barra" title="${esc(l.titulo)}: ${l.responderam} de ${l.convidados} responderam">
        <span class="rotulo">${esc(l.titulo)}</span>
        <span class="trilho">
          <span class="cor-verde" style="width:${verde}%"></span>
          <span class="cor-cinza" style="width:${l.convidados ? 100 - verde : 0}%"></span>
        </span>
        <span class="num">${taxa}</span>
      </div>`;
  }).join('');

  if (linhas.length === 0) {
    linhaVazia('tabela-participacao', 6, 'Nenhuma pesquisa com esse título.');
    return;
  }
  document.getElementById('tabela-participacao').innerHTML = linhas.map((l) => `
    <tr><td>${esc(l.titulo)}</td><td>${l.inicio} a ${l.fim}</td>
      <td class="num">${l.convidados}</td><td class="num">${l.responderam}</td><td class="num">${l.pendentes}</td>
      <td class="num">${l.taxa === null ? '—' : numero(l.taxa, 1) + '%'}</td></tr>`).join('');
}

iniciar();
