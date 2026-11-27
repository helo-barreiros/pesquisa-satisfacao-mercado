// Tela de gerenciar, escolher questões  e convidar

const idPesquisa = Number(parametro('id'));
let escolhidas = [];      // [{idQuestao, obrigatoria, enunciado, tipo, tema}]
let bancoQuestoes = [];
let bloqueada = false;

async function iniciar() {
    await iniciarPagina({ admin: true, ativo: 'pesquisas.html' });
    const [temas, grupos, respondentes] = await Promise.all([
        API.get('/api/admin/temas'), API.get('/api/admin/grupos'), API.get('/api/admin/respondentes'),
    ]);
    preencherSelect(document.getElementById('filtro-tema'), temas, { vazio: 'Todos os temas' });
    preencherSelect(document.getElementById('grupo'), grupos);
    preencherSelect(document.getElementById('respondente'),
        respondentes.map((u) => ({ id: u.id, nome: `${u.nome} (${u.grupo})` })));

    document.getElementById('filtro-tema').onchange = carregarBanco;
    let espera;
    document.getElementById('filtro-busca').oninput = () => { clearTimeout(espera); espera = setTimeout(carregarBanco, 300); };
    document.getElementById('salvar-questoes').onclick = salvarQuestoes;
    document.getElementById('convidar-grupo').onclick = () =>
        convidar({ grupos: [Number(document.getElementById('grupo').value)] });
    document.getElementById('convidar-pessoa').onclick = () =>
        convidar({ usuarios: [Number(document.getElementById('respondente').value)] });

    await carregarPesquisa();
    await carregarBanco();
    await carregarConvidados();
}
async function carregarPesquisa() {
    const det = await API.get(`/api/admin/pesquisas/${idPesquisa}`);
    const p = det.pesquisa;
    const s = situacao(p);
    document.title = `${p.titulo} · Pesquisa de Satisfação`;
    document.getElementById('titulo').textContent = p.titulo;
    document.getElementById('resumo').innerHTML =
        `${fmtDataHora(p.inicio)} até ${fmtDataHora(p.fim)} · <span class="etiqueta ${s.classe}">${s.texto}</span>
     ${p.anonima ? '<span class="etiqueta anonima">anônima</span>' : '<span class="etiqueta">identificada</span>'}
     ${p.descricao ? '<br>' + esc(p.descricao) : ''}`;
    document.getElementById('aviso-anonima').hidden = !p.anonima;

    bloqueada = det.temRespostas;
    document.getElementById('bloqueio').hidden = !bloqueada;
    document.getElementById('salvar-questoes').disabled = bloqueada;
    escolhidas = det.questoes.map((q) => ({
        idQuestao: q.idQuestao, obrigatoria: q.obrigatoria, enunciado: q.enunciado, tipo: q.tipo, tema: q.tema,
    }));
    desenharEscolhidas();
}

function desenharEscolhidas() {
    document.getElementById('qtd-escolhidas').textContent = `(${escolhidas.length})`;
    const lista = document.getElementById('escolhidas');
    if (escolhidas.length === 0) {
        lista.innerHTML = '<li style="display:block" class="vazio">Nenhuma questão escolhida. Adicione pelo banco ao lado.</li>';
        desenharBanco();
        return;
    }
    lista.innerHTML = escolhidas.map((q, i) => `
    <li>
      <span class="pos">${i + 1}</span>
      <div>${esc(q.enunciado)}<div class="meta">${esc(q.tema)} · ${NOME_TIPO[q.tipo] || q.tipo}</div></div>
      <div class="botoes">
        <label><input type="checkbox" data-obrig="${i}" ${q.obrigatoria ? 'checked' : ''} ${bloqueada ? 'disabled' : ''}>obrigatória</label>
        <button type="button" class="secundario mini" data-subir="${i}" ${bloqueada || i === 0 ? 'disabled' : ''} title="Subir">↑</button>
        <button type="button" class="secundario mini" data-descer="${i}" ${bloqueada || i === escolhidas.length - 1 ? 'disabled' : ''} title="Descer">↓</button>
        <button type="button" class="perigo mini" data-tirar="${i}" ${bloqueada ? 'disabled' : ''} title="Tirar">✕</button>
      </div>
    </li>`).join('');

    lista.querySelectorAll('[data-obrig]').forEach((c) => (c.onchange = () => (escolhidas[c.dataset.obrig].obrigatoria = c.checked)));
    lista.querySelectorAll('[data-subir]').forEach((b) => (b.onclick = () => mover(Number(b.dataset.subir), -1)));
    lista.querySelectorAll('[data-descer]').forEach((b) => (b.onclick = () => mover(Number(b.dataset.descer), 1)));
    lista.querySelectorAll('[data-tirar]').forEach((b) => (b.onclick = () => { escolhidas.splice(Number(b.dataset.tirar), 1); desenharEscolhidas(); }));
    desenharBanco();
}

function mover(i, delta) {
    const j = i + delta;
    [escolhidas[i], escolhidas[j]] = [escolhidas[j], escolhidas[i]];
    desenharEscolhidas();
}

async function carregarBanco() {
    const params = new URLSearchParams();
    const tema = document.getElementById('filtro-tema').value;
    const busca = document.getElementById('filtro-busca').value;
    if (tema) params.set('tema', tema);
    if (busca) params.set('busca', busca);
    bancoQuestoes = await API.get('/api/admin/questoes?' + params);
    desenharBanco();
}

function desenharBanco() {
    const ids = new Set(escolhidas.map((q) => q.idQuestao));
    const corpo = document.getElementById('banco');
    if (bancoQuestoes.length === 0) {
        corpo.innerHTML = '<tr><td class="vazio">Nenhuma questão encontrada.</td></tr>';
        return;
    }
    corpo.innerHTML = bancoQuestoes.map((q) => `
    <tr>
      <td>${esc(q.enunciado)}<div class="sub" style="margin:0;font-size:12.5px">${esc(q.tema)} · ${NOME_TIPO[q.tipo] || q.tipo}</div></td>
      <td style="width:1%"><button type="button" class="secundario mini" data-add="${q.id}"
        ${ids.has(q.id) || bloqueada ? 'disabled' : ''}>${ids.has(q.id) ? 'Adicionada' : 'Adicionar'}</button></td>
    </tr>`).join('');
    corpo.querySelectorAll('[data-add]').forEach((b) => (b.onclick = () => {
        const q = bancoQuestoes.find((x) => x.id === Number(b.dataset.add));
        escolhidas.push({ idQuestao: q.id, obrigatoria: q.tipo !== 'TEXTO_LIVRE', enunciado: q.enunciado, tipo: q.tipo, tema: q.tema });
        desenharEscolhidas();
    }));
}

async function salvarQuestoes() {
    try {
        await API.put(`/api/admin/pesquisas/${idPesquisa}/questoes`,
            escolhidas.map((q) => ({ idQuestao: q.idQuestao, obrigatoria: q.obrigatoria })));
        aviso('Questões salvas.');
        await carregarPesquisa();
    } catch (e) {
        aviso(e.message, 'erro');
    }
}

async function carregarConvidados() {
    const convidados = await API.get(`/api/admin/pesquisas/${idPesquisa}/convites`);
    const responderam = convidados.filter((c) => c.dtResp).length;
    document.getElementById('resumo-convites').textContent =
        `${convidados.length} convidado(s) · ${responderam} já responderam · ${convidados.length - responderam} pendente(s)`;
    const corpo = document.getElementById('convidados');
    if (convidados.length === 0) {
        corpo.innerHTML = '<tr><td colspan="6" class="vazio">Ninguém convidado ainda.</td></tr>';
        return;
    }
    corpo.innerHTML = convidados.map((c) => `
    <tr>
      <td>${esc(c.nome)}</td><td>${esc(c.email)}</td><td>${esc(c.grupo)}</td>
      <td>${fmtData(c.dtConvite)}</td>
      <td>${c.dtResp ? `<span class="etiqueta ok">respondeu em ${fmtData(c.dtResp)}</span>` : '<span class="etiqueta pendente">pendente</span>'}</td>
      <td>${c.dtResp ? '' : `<button type="button" class="perigo mini" data-remover="${c.idUsuario}">Retirar</button>`}</td>
    </tr>`).join('');
    corpo.querySelectorAll('[data-remover]').forEach((b) => (b.onclick = async () => {
        try {
            await API.del(`/api/admin/pesquisas/${idPesquisa}/convites/${b.dataset.remover}`);
            aviso('Convite retirado.');
            carregarConvidados();
        } catch (e) {
            aviso(e.message, 'erro');
        }
    }));
}

async function convidar(pedido) {
    try {
        const { novos } = await API.post(`/api/admin/pesquisas/${idPesquisa}/convites`, pedido);
        aviso(novos ? `${novos} convite(s) criado(s).` : 'Essas pessoas já estavam convidadas.');
        carregarConvidados();
    } catch (e) {
        aviso(e.message, 'erro');
    }
}

iniciar();