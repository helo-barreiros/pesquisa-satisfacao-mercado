// "Minhas pesquisas" usuario

async function iniciar() {
    await iniciarPagina({ ativo: 'minhas.html' });
    const pesquisas = await API.get('/api/minhas-pesquisas');
    const corpo = document.getElementById('lista');
    if (pesquisas.length === 0) {
        corpo.innerHTML = '<tr><td colspan="5" class="vazio">Você ainda não foi convidado para nenhuma pesquisa.</td></tr>';
        return;
    }
    corpo.innerHTML = pesquisas.map((p) => {
        const s = situacao(p);
        let estado;
        let acao = '';
        if (p.dtResp) {
            estado = `<span class="etiqueta ok">respondida em ${fmtData(p.dtResp)}</span>`;
        } else if (s.classe === 'aberta') {
            estado = '<span class="etiqueta pendente">pendente</span>';
            acao = `<a class="botao mini" href="responder.html?id=${p.id}">Responder</a>`;
        } else {
            estado = `<span class="etiqueta ${s.classe}">${s.texto}</span>`;
        }
        return `
      <tr>
        <td><strong>${esc(p.titulo)}</strong>
          ${p.anonima ? ' <span class="etiqueta anonima">anônima</span>' : ''}
          ${p.descricao ? `<br><span class="sub">${esc(p.descricao)}</span>` : ''}</td>
        <td>${fmtDataHora(p.inicio)}<br><span class="sub">até ${fmtDataHora(p.fim)}</span></td>
        <td class="num">${p.qtdQuestoes}</td>
        <td>${estado}</td>
        <td>${acao}</td>
      </tr>`;
    }).join('');
}

iniciar();