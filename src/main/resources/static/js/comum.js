// Funções usadas por todas as telas

const API = {
    async chamar(metodo, url, corpo) {
        const opcoes = { method: metodo, headers: {} };
        if (corpo !== undefined) {
            opcoes.headers['Content-Type'] = 'application/json';
            opcoes.body = JSON.stringify(corpo);
        }
        const resp = await fetch(url, opcoes);
        if (resp.status === 401 && !url.endsWith('/api/login')) {
            location.href = 'index.html';
            throw new Error('Sessão expirada.');
        }
        const texto = await resp.text();
        const dados = texto ? JSON.parse(texto) : null;
        if (!resp.ok) {
            throw new Error((dados && dados.erro) || `Erro ${resp.status}`);
        }
        return dados;
    },
    get(url) { return this.chamar('GET', url); },
    post(url, corpo) { return this.chamar('POST', url, corpo ?? {}); },
    put(url, corpo) { return this.chamar('PUT', url, corpo); },
    del(url) { return this.chamar('DELETE', url); },
};

// Confere login, monta menu e devolve user logado
async function iniciarPagina({ admin = false, ativo = '' } = {}) {
    const eu = await API.get('/api/eu');
    if (admin && eu.papel !== 'ADMIN') {
        location.href = 'minhas.html';
        throw new Error('Acesso só para a administração.');
    }
    montarMenu(eu, ativo);
    return eu;
}

function montarMenu(eu, ativo) {
    const links = eu.papel === 'ADMIN'
        ? [['pesquisas.html', 'Pesquisas'], ['questoes.html', 'Questões'], ['relatorios.html', 'Relatórios']]
        : [['minhas.html', 'Minhas pesquisas']];
    const topo = document.createElement('header');
    topo.className = 'topo';
    topo.innerHTML = `
    <div class="marca">Pesquisa de Satisfação</div>
    <nav>${links.map(([href, nome]) =>
        `<a href="${href}" class="${href === ativo ? 'ativo' : ''}">${nome}</a>`).join('')}</nav>
    <div class="quem">${esc(eu.nome)}${eu.grupo ? ' · ' + esc(eu.grupo) : ''}
      <button type="button" id="sair">Sair</button></div>`;
    document.body.prepend(topo);
    document.getElementById('sair').onclick = async () => {
        await API.post('/api/logout');
        location.href = 'index.html';
    };
}

function esc(valor) {
    return String(valor ?? '').replace(/[&<>"']/g, (c) =>
        ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}

function fmtDataHora(iso) {
    if (!iso) return '';
    return new Date(iso).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });
}

function fmtData(iso) {
    if (!iso) return '';
    return new Date(iso.length === 10 ? iso + 'T00:00' : iso).toLocaleDateString('pt-BR');
}

function situacao(p) {
    const agora = new Date();
    if (agora < new Date(p.inicio)) return { texto: 'Agendada', classe: 'agendada' };
    if (agora > new Date(p.fim)) return { texto: 'Encerrada', classe: 'encerrada' };
    return { texto: 'Aberta', classe: 'aberta' };
}

const NOME_TIPO = {
    ESCALA: 'Escala 1 a 5',
    MULTIPLA_ESCOLHA: 'Múltipla escolha',
    TEXTO_LIVRE: 'Texto livre',
};

function parametro(nome) {
    return new URLSearchParams(location.search).get(nome);
}

let temporizadorAviso;
function aviso(mensagem, tipo = 'ok') {
    let caixa = document.getElementById('aviso');
    if (!caixa) {
        caixa = document.createElement('div');
        caixa.id = 'aviso';
        document.body.appendChild(caixa);
    }
    caixa.textContent = mensagem;
    caixa.className = 'visivel' + (tipo === 'erro' ? ' erro' : '');
    clearTimeout(temporizadorAviso);
    temporizadorAviso = setTimeout(() => (caixa.className = ''), tipo === 'erro' ? 6000 : 3000);
}

// Preenche o select
function preencherSelect(select, itens, { valor = 'id', texto = 'nome', vazio = null } = {}) {
    select.innerHTML = (vazio !== null ? `<option value="">${esc(vazio)}</option>` : '')
        + itens.map((i) => `<option value="${esc(i[valor])}">${esc(i[texto])}</option>`).join('');
}
