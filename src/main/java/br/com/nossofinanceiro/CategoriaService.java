package br.com.nossofinanceiro;

import br.com.nossofinanceiro.controller.CategoriaController.CategoriaRequest;
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

    public List<Categoria> listarCategoriasPorTipo(String tipo) {
        User usuarioLogado = getUsuarioLogado();
        TipoTransacao tipoTransacao = TipoTransacao.valueOf(tipo.toUpperCase());
        return categoriaRepository.findAllByUserAndTipo(usuarioLogado, tipoTransacao);
    }

    public Categoria criarCategoria(Categoria categoria) {
        User usuarioLogado = getUsuarioLogado();
        categoria.setUser(usuarioLogado);
        return categoriaRepository.save(categoria);
    }

    public Categoria atualizarCategoria(Long id, CategoriaRequest request) {
        User usuarioLogado = getUsuarioLogado();

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        // Verifica se a categoria pertence ao usuário
        if (!categoria.getUser().getId().equals(usuarioLogado.getId())) {
            throw new RuntimeException("Categoria não pertence ao usuário");
        }

        categoria.setNome(request.nome());
        categoria.setTipo(TipoTransacao.valueOf(request.tipo().toUpperCase()));
        categoria.setIcone(request.icone());

        return categoriaRepository.save(categoria);
    }

    public void excluirCategoria(Long id) {
        User usuarioLogado = getUsuarioLogado();

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        // Verifica se a categoria pertence ao usuário
        if (!categoria.getUser().getId().equals(usuarioLogado.getId())) {
            throw new RuntimeException("Categoria não pertence ao usuário");
        }

        // TODO: Verificar se existem transações usando esta categoria
        // Se existirem, você pode optar por:
        // 1. Impedir a exclusão
        // 2. Definir as transações para uma categoria padrão
        // 3. Deixar as transações órfãs

        categoriaRepository.delete(categoria);
    }
}