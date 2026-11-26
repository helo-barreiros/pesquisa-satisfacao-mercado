// PostgreSQL local (PGlite) para desenvolvimento e para o pitch.
//
//   node servidor.mjs            liga o banco (carrega schema + carga só na primeira vez)
//   node servidor.mjs --resetar  apaga os dados e carrega schema + carga de novo
//
// Conexão JDBC:
//   jdbc:postgresql://127.0.0.1:5433/postgres?sslmode=disable&prepareThreshold=0
//   usuário: postgres   senha: postgres
//
// Observações:
//   * prepareThreshold=0 é obrigatório: todas as conexões compartilham um único
//     processo do PostgreSQL, e os nomes dos comandos preparados colidiriam;
//   * use pool com 1 conexão (spring.datasource.hikari.maximum-pool-size=1).

import { readFileSync, rmSync, existsSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';
import { PGlite } from '@electric-sql/pglite';
import { PGLiteSocketServer } from '@electric-sql/pglite-socket';

const AQUI = dirname(fileURLToPath(import.meta.url));
const DADOS = join(AQUI, 'dados');
const SQL = join(AQUI, '..', 'database');
const HOST = '127.0.0.1';
const PORTA = Number(process.env.PORTA ?? 5433);

const resetar = process.argv.includes('--resetar');
if (resetar && existsSync(DADOS)) {
  rmSync(DADOS, { recursive: true, force: true });
  console.log('Dados apagados.');
}

const db = await PGlite.create(DADOS);

const jaCarregado = (await db.query(`select to_regclass('public.usuario') is not null as ok`)).rows[0].ok;
if (!jaCarregado) {
  console.log('Banco vazio: rodando database/schema.sql e database/carga.sql...');
  await db.exec(readFileSync(join(SQL, 'schema.sql'), 'utf8'));
  await db.exec(readFileSync(join(SQL, 'carga.sql'), 'utf8'));
  console.log('Banco carregado.');
} else {
  console.log('Usando os dados já existentes em db-local/dados (para recomeçar: resetar.cmd).');
}

const server = new PGLiteSocketServer({ db, host: HOST, port: PORTA, maxConnections: 10 });
try {
  await server.start();
} catch (e) {
  console.error(`\nNão foi possível abrir a porta ${PORTA}: ${e.message}`);
  console.error('Talvez o banco já esteja ligado em outra janela.');
  await db.close();
  process.exit(1);
}

console.log(`
PostgreSQL local ligado em ${HOST}:${PORTA}
  JDBC:    jdbc:postgresql://${HOST}:${PORTA}/postgres?sslmode=disable&prepareThreshold=0
  usuário: postgres   senha: postgres

Deixe esta janela aberta enquanto usa o sistema. Para desligar: Ctrl+C.
`);

let saindo = false;
async function desligar() {
  if (saindo) return;
  saindo = true;
  console.log('\nDesligando e gravando os dados...');
  try { await server.stop(); } catch {}
  try { await db.close(); } catch {}
  console.log('Banco desligado.');
  process.exit(0);
}
process.on('SIGINT', desligar);
process.on('SIGTERM', desligar);
process.on('SIGBREAK', desligar);
