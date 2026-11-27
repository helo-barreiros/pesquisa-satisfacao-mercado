// Tela de pesquisas lista e criação.

// converte uma data do JavaScript para o formato do campo datetime-local
function paraInputLocal(data) {
    const p = (n) => String(n).padStart(2, '0');
    return `${data.getFullYear()}-${p(data.getMonth() + 1)}-${p(data.getDate())}T${p(data.getHours())}:${p(data.getMinutes())}`;
}

async function iniciar() {
    await iniciarPagina({ admin: true, ativo: 'pesquisas.html' });

    // sugestão: começa agora e dura 15 dias
    const agora = new Date();
    agora.setSeconds(0, 0);
    const fim = new Date(agora.getTime() + 15 * 24 * 3600 * 1000);
    document.getElementById('inicio').value = paraInputLocal(agora);
    document.getElementById('fim').value = paraInputLocal(fim);

    document.getElementById('form-pesquisa').onsubmit = criar;
    carregarLista();
}

async function carregarLista() {
    const pesquisas = await API.get('/api/admin/pesquisas');
    const corpo = document.getElementById('lista');
    if (pesquisas.length === 0) {
        corpo.innerHTML = '<tr><td colspan="7" class="vazio">Nenhuma pesquisa cadastrada.</td></tr>';
        return;
    }
    corpo.innerHTML = pesquisas.map((p) => {
        const s = situacao(p);
        const taxa = p.convidados ? Math.round((100 * p.responderam) / p.convidados) : 0;
        return `
      <tr>
        <td><strong>${esc(p.titulo)}</strong>
          ${p.anonima ? ' <span class="etiqueta anonima">anônima</span>' : ''}</td>
        <td>${fmtDataHora(p.inicio)}<br><span class="sub">até ${fmtDataHora(p.fim)}</span></td>
        <td><span class="etiqueta ${s.classe}">${s.texto}</span></td>
        <td class="num">${p.qtdQuestoes}</td>
        <td class="num">${p.convidados}</td>
        <td class="num">${p.responderam} <span class="sub">(${taxa}%)</span></td>
        <td><a class="botao secundario mini" href="pesquisa.html?id=${p.id}">Gerenciar</a></td>
      </tr>`;
    }).join('');
}

async function criar(ev) {
    ev.preventDefault();
    const corpo = {
        titulo: document.getElementById('titulo').value,
        descricao: document.getElementById('descricao').value,
        inicio: document.getElementById('inicio').value,
        fim: document.getElementById('fim').value,
        anonima: document.getElementById('anonima').checked,
    };
    try {
        const { id } = await API.post('/api/admin/pesquisas', corpo);
        location.href = `pesquisa.html?id=${id}`;
    } catch (e) {
        aviso(e.message, 'erro');
    }
}

iniciar();