package br.com.nossofinanceiro;

import br.com.nossofinanceiro.security.User;
import br.com.nossofinanceiro.security.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private UserRepository userRepository;

    private User getUsuarioLogado() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    }

    public List<Categoria> listarCategoriasDoUsuario() {
        return categoriaRepository.findAllByUser(getUsuarioLogado());
    }

    // --- NOVO MÉTODO ---
    public List<Categoria> listarCategoriasPorTipo(String tipo) {
        User usuarioLogado = getUsuarioLogado();
        TipoTransacao tipoTransacao = TipoTransacao.valueOf(tipo.toUpperCase());
        return categoriaRepository.findAllByUserAndTipo(usuarioLogado, tipoTransacao);
    }

    public Categoria criarCategoria(Categoria categoria) {
        User usuarioLogado = getUsuarioLogado();
        categoria.setUser(usuarioLogado);
        // O tipo já virá do controller
        return categoriaRepository.save(categoria);
    }
}
