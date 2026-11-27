package br.uel.pesquisa.controller;

import br.uel.pesquisa.dao.RelatorioDao;
import br.uel.pesquisa.service.RegraNegocio;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

//Relatórios (RF7). Fica em /api/admin, então só a administração acessa.
@RestController
@RequestMapping("/api/admin/relatorios")
public class RelatorioController {

    private final RelatorioDao relatorioDao;

    public RelatorioController(RelatorioDao relatorioDao) {
        this.relatorioDao = relatorioDao;
    }

    // ex.: /api/admin/relatorios/temas?inicio=2026-01-01&fim=2026-12-31&grupos=1,2,3
    @GetMapping("/temas")
    public List<Map<String, Object>> mediaPorTema(
            @RequestParam(name = "inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(name = "fim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(name = "grupos", required = false) List<Integer> grupos) {
        if (grupos == null || grupos.isEmpty()) {
            throw new RegraNegocio("Escolha pelo menos um grupo.");
        }
        if (fim.isBefore(inicio)) {
            throw new RegraNegocio("A data final precisa ser depois da inicial.");
        }
        return relatorioDao.mediaPorTema(inicio, fim, grupos);
    }

    @GetMapping("/ranking")
    public List<Map<String, Object>> rankingPiores() {
        return relatorioDao.rankingPiores();
    }

    // ex.: /api/admin/relatorios/participacao?busca=satisfação
    @GetMapping("/participacao")
    public List<Map<String, Object>> participacao(
            @RequestParam(name = "busca", required = false, defaultValue = "") String busca) {
        return relatorioDao.participacao(busca.trim());
    }
}
