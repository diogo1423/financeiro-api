package br.com.nossofinanceiro.controller;

import br.com.nossofinanceiro.Categoria;
import br.com.nossofinanceiro.CategoriaService;
import br.com.nossofinanceiro.TipoTransacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    // DTO atualizado com ícone
    public record CategoriaRequest(String nome, String tipo, String icone) {}

    @GetMapping
    public ResponseEntity<List<Categoria>> listarCategorias(@RequestParam(required = false) String tipo) {
        List<Categoria> categorias;
        if (tipo != null && !tipo.isEmpty()) {
            categorias = categoriaService.listarCategoriasPorTipo(tipo);
        } else {
            categorias = categoriaService.listarCategoriasDoUsuario();
        }
        return ResponseEntity.ok(categorias);
    }

    @PostMapping
    public ResponseEntity<Categoria> criarCategoria(@RequestBody CategoriaRequest request) {
        Categoria novaCategoria = new Categoria();
        novaCategoria.setNome(request.nome());
        novaCategoria.setTipo(TipoTransacao.valueOf(request.tipo().toUpperCase()));
        novaCategoria.setIcone(request.icone());
        Categoria categoriaSalva = categoriaService.criarCategoria(novaCategoria);
        return new ResponseEntity<>(categoriaSalva, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> atualizarCategoria(@PathVariable Long id, @RequestBody CategoriaRequest request) {
        try {
            Categoria categoriaAtualizada = categoriaService.atualizarCategoria(id, request);
            return ResponseEntity.ok(categoriaAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirCategoria(@PathVariable Long id) {
        try {
            categoriaService.excluirCategoria(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}