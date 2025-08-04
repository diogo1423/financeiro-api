package br.com.nossofinanceiro;

import br.com.nossofinanceiro.security.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    // --- NOVO CAMPO PARA ÍCONES ---
    @Column
    private String icone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // Construtores, Getters e Setters
    public Categoria() {}

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public TipoTransacao getTipo() { return tipo; }
    public void setTipo(TipoTransacao tipo) { this.tipo = tipo; }
    public String getIcone() { return icone; }
    public void setIcone(String icone) { this.icone = icone; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}