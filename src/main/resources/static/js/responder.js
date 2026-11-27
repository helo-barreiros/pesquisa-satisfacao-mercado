// Tela de responder uma pesquisa (logado e anonimo)

const idPesquisa = Number(parametro('id'));
let questoes = [];
let alternativas = {};   // { idQuestao: [alternativas] }

async function iniciar() {
  await iniciarPagina({ ativo: 'minhas.html' });
  let form;
  try {
    form = await API.get(`/api/minhas-pesquisas/${idPesquisa}`);
  } catch (e) {
    // não convidado, já respondeu ou pesquisa fechada
    document.getElementById('titulo').textContent = 'Não é possível responder';
    document.getElementById('form').innerHTML = `<p class="nota">${esc(e.message)}</p>`;
    return;
  }
  const p = form.pesquisa;
  questoes = form.questoes;
  alternativas = form.alternativas;
  document.title = `${p.titulo} · Pesquisa de Satisfação`;
  document.getElementById('titulo').textContent = p.titulo;
  document.getElementById('descricao').textContent = p.descricao || '';
  document.getElementById('aviso-anonima').hidden = !p.anonima;
  document.getElementById('questoes').innerHTML = questoes.map(desenharQuestao).join('');
  document.getElementById('form').onsubmit = enviar;
}

// Monta o campo certo para cada tipo de questão
function desenharQuestao(q) {
  const nome = `q${q.idQuestao}`;
  let campo;
  if (q.tipo === 'ESCALA') {
    const notas = [1, 2, 3, 4, 5].map((v) =>
      `<label><input type="radio" name="${nome}" value="${v}">${v}</label>`).join('');
    campo = `<div class="escala"><span class="extremo">Péssimo</span>${notas}<span class="extremo">Ótimo</span></div>`;
  } else if (q.tipo === 'MULTIPLA_ESCOLHA') {
    const opcoes = (alternativas[q.idQuestao] || []).map((a) =>
      `<label><input type="radio" name="${nome}" value="${a.id}">${esc(a.texto)}</label>`).join('');
    campo = `<div class="opcoes">${opcoes}</div>`;
  } else {
    campo = `<textarea name="${nome}" maxlength="1000" placeholder="Escreva aqui"></textarea>`;
  }
  return `
    <div class="questao">
      <div class="enunciado">${q.ordem}. ${esc(q.enunciado)} ${q.obrigatoria ? '<span class="obrig">*</span>' : ''}</div>
      <div class="meta">${esc(q.tema)}</div>
      ${campo}
    </div>`;
}

// Junta as respostas no formato [{idQuestao, valorEscala | idAlternativa | texto}]
function coletar() {
  const respostas = [];
  for (const q of questoes) {
    const nome = `q${q.idQuestao}`;
    if (q.tipo === 'TEXTO_LIVRE') {
      const texto = document.querySelector(`[name="${nome}"]`).value.trim();
      if (texto) respostas.push({ idQuestao: q.idQuestao, texto: texto });
    } else {
      const marcado = document.querySelector(`[name="${nome}"]:checked`);
      if (!marcado) continue;
      const valor = Number(marcado.value);
      if (q.tipo === 'ESCALA') {
        respostas.push({ idQuestao: q.idQuestao, valorEscala: valor });
      } else {
        respostas.push({ idQuestao: q.idQuestao, idAlternativa: valor });
      }
    }
  }
  return respostas;
}

async function enviar(ev) {
  ev.preventDefault();
  const respostas = coletar();
  const respondidas = new Set(respostas.map((r) => r.idQuestao));
  const faltando = questoes.find((q) => q.obrigatoria && !respondidas.has(q.idQuestao));
  if (faltando) {
    aviso(`Responda a questão ${faltando.ordem}.`, 'erro');
    return;
  }
  const botao = document.getElementById('enviar');
  botao.disabled = true;
  try {
    await API.post(`/api/minhas-pesquisas/${idPesquisa}/respostas`, respostas);
    aviso('Respostas enviadas. Obrigado!');
    setTimeout(() => (location.href = 'minhas.html'), 1500);
  } catch (e) {
    aviso(e.message, 'erro');
    botao.disabled = false;
  }
}

iniciar();
