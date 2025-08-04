package br.com.nossofinanceiro;

import br.com.nossofinanceiro.security.User;
import br.com.nossofinanceiro.security.UserRepository; // Importação necessária
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
    private UserRepository userRepository; // Esta linha depende da importação acima

    /**
     * Obtém o usuário atualmente autenticado no sistema.
     * @return O objeto User do usuário logado.
     */
    private User getUsuarioLogado() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    /**
     * Lista todas as categorias (receitas e despesas) do usuário logado.
     * @return Uma lista de categorias.
     */
    public List<Categoria> listarCategoriasDoUsuario() {
        return categoriaRepository.findAllByUser(getUsuarioLogado());
    }

    /**
     * Lista as categorias do usuário logado filtrando por tipo (RECEITA ou DESPESA).
     * @param tipo O tipo da transação ("RECEITA" ou "DESPESA").
     * @return Uma lista de categorias do tipo especificado.
     */
    public List<Categoria> listarCategoriasPorTipo(String tipo) {
        User usuarioLogado = getUsuarioLogado();
        TipoTransacao tipoTransacao = TipoTransacao.valueOf(tipo.toUpperCase());
        return categoriaRepository.findAllByUserAndTipo(usuarioLogado, tipoTransacao);
    }

    /**
     * Cria uma nova categoria para o usuário logado.
     * @param categoria O objeto Categoria a ser salvo.
     * @return A categoria salva com o ID gerado.
     */
    public Categoria criarCategoria(Categoria categoria) {
        User usuarioLogado = getUsuarioLogado();
        categoria.setUser(usuarioLogado);
        // O tipo e o nome já devem ter sido definidos no Controller
        return categoriaRepository.save(categoria);
    }
}
