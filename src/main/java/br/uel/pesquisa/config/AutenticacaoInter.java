package br.uel.pesquisa.config;

import br.uel.pesquisa.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * Protege a API: exige login em /api/** (menos /api/login) e papel ADMIN em /api/admin/**.
 */
public class AutenticacaoInter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws IOException {
        Usuario usuario = Sessao.usuario(req.getSession(false));
        if (usuario == null) {
            return negar(resp, HttpServletResponse.SC_UNAUTHORIZED, "Faça login para continuar.");
        }
        String caminho = req.getRequestURI().substring(req.getContextPath().length());
        if (caminho.startsWith("/api/admin/") && !usuario.ehAdmin()) {
            return negar(resp, HttpServletResponse.SC_FORBIDDEN, "Acesso permitido só para a administração.");
        }
        return true;
    }

    private boolean negar(HttpServletResponse resp, int status, String mensagem) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write("{\"erro\":\"" + mensagem + "\"}");
        return false;
    }
}
