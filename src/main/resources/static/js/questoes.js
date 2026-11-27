
let tipos = [];

async function iniciar() {
    await iniciarPagina({ admin: true, ativo: 'questoes.html' });
    const [listaTipos, temas] = await Promise.all([API.get('/api/admin/tipos'), API.get('/api/admin/temas')]);
    tipos = listaTipos;
    preencherSelect(document.getElementById('tipo'), tipos.map((t) => ({ id: t.id, nome: NOME_TIPO[t.codigo] || t.codigo })));
    preencherSelect(document.getElementById('tema'), temas, { vazio: 'Escolha...' });
    preencherSelect(document.getElementById('filtro-tema'), temas, { vazio: 'Todos os temas' });

    document.getElementById('tipo').onchange = atualizarAlternativas;
    document.getElementById('mais-alternativa').onclick = () => adicionarAlternativa('');
    document.getElementById('form-questao').onsubmit = salvar;
    document.getElementById('filtro-tema').onchange = carregarLista;
    let espera;
    document.getElementById('filtro-busca').oninput = () => {
        clearTimeout(espera);
        espera = setTimeout(carregarLista, 300);
    };

    atualizarAlternativas();
    carregarLista();
}

function tipoEscolhido() {
    const id = Number(document.getElementById('tipo').value);
    return tipos.find((t) => t.id === id);
}

// mostra o bloco de alternativas só para múltipla escolha
function atualizarAlternativas() {
    const ehMultipla = tipoEscolhido()?.codigo === 'MULTIPLA_ESCOLHA';
    document.getElementById('bloco-alternativas').hidden = !ehMultipla;
    const caixa = document.getElementById('alternativas');
    if (ehMultipla && caixa.children.length === 0) {
        adicionarAlternativa('');
        adicionarAlternativa('');
    }
}

function adicionarAlternativa(texto) {
    const caixa = document.getElementById('alternativas');
    const linha = document.createElement('div');
    linha.className = 'linha';
    linha.style.marginBottom = '6px';
    linha.innerHTML = `<input type="text" class="alternativa" placeholder="Texto da alternativa">
                     <button type="button" class="perigo mini fixo" title="Remover">✕</button>`;
    linha.querySelector('input').value = texto;
    linha.querySelector('button').onclick = () => linha.remove();
    caixa.appendChild(linha);
}

async function salvar(ev) {
    ev.preventDefault();
    const corpo = {
        idTipo: Number(document.getElementById('tipo').value),
        idTema: Number(document.getElementById('tema').value) || null,
        enunciado: document.getElementById('enunciado').value,
        alternativas: [...document.querySelectorAll('.alternativa')].map((i) => i.value),
    };
    try {
        const { id } = await API.post('/api/admin/questoes', corpo);
        aviso(`Questão ${id} cadastrada.`);
        document.getElementById('enunciado').value = '';
        document.getElementById('alternativas').innerHTML = '';
        atualizarAlternativas();
        carregarLista();
    } catch (e) {
        aviso(e.message, 'erro');
    }
}

async function carregarLista() {
    const tema = document.getElementById('filtro-tema').value;
    const busca = document.getElementById('filtro-busca').value;
    const params = new URLSearchParams();
    if (tema) params.set('tema', tema);
    if (busca) params.set('busca', busca);
    const questoes = await API.get('/api/admin/questoes?' + params);

    document.getElementById('contagem').textContent = `${questoes.length} questão(ões)`;
    const corpo = document.getElementById('lista');
    if (questoes.length === 0) {
        corpo.innerHTML = '<tr><td colspan="5" class="vazio">Nenhuma questão encontrada.</td></tr>';
        return;
    }
    corpo.innerHTML = questoes.map((q) => `
    <tr>
      <td class="num">${q.id}</td>
      <td>${esc(q.enunciado)}
        ${q.alternativas.length ? `<ol class="alternativas">${q.alternativas.map((a) => `<li>${esc(a.texto)}</li>`).join('')}</ol>` : ''}
      </td>
      <td><span class="etiqueta">${NOME_TIPO[q.tipo] || q.tipo}</span></td>
      <td>${esc(q.tema)}</td>
      <td class="num">${q.usos === 0 ? '<span class="etiqueta pendente">nunca usada</span>' : q.usos + ' pesquisa(s)'}</td>
    </tr>`).join('');
}

iniciar();