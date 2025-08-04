package br.com.nossofinanceiro;

import br.com.nossofinanceiro.security.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double valor;
    private String descricao;
    private LocalDate data;
    private String categoria;

    @Enumerated(EnumType.STRING)
    private TipoTransacao tipo;

    @Column(nullable = false)
    private boolean pago = false;

    private Integer parcelaAtual;
    private Integer totalParcelas;

    // --- NOVO CAMPO PARA TAGS ---
    @Column
    private String tags;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public Transacao() {}

    // Getters
    public Long getId() { return id; }
    public double getValor() { return valor; }
    public String getDescricao() { return descricao; }
    public LocalDate getData() { return data; }
    public String getCategoria() { return categoria; }
    public TipoTransacao getTipo() { return tipo; }
    public boolean isPago() { return pago; }
    public Integer getParcelaAtual() { return parcelaAtual; }
    public Integer getTotalParcelas() { return totalParcelas; }
    public String getTags() { return tags; }
    public User getUser() { return user; }

    // Setters
    public void setValor(double valor) { this.valor = valor; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setData(LocalDate data) { this.data = data; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setTipo(TipoTransacao tipo) { this.tipo = tipo; }
    public void setPago(boolean pago) { this.pago = pago; }
    public void setParcelaAtual(Integer parcelaAtual) { this.parcelaAtual = parcelaAtual; }
    public void setTotalParcelas(Integer totalParcelas) { this.totalParcelas = totalParcelas; }
    public void setTags(String tags) { this.tags = tags; }
    public void setUser(User user) { this.user = user; }
}