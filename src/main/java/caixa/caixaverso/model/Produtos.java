package caixa.caixaverso.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Produtos extends PanacheEntity {
    private String nome;
    private String descricao;
    private Double preco;
    
    public String getNome() {
        return nome;
    }
    public String getDescricao() {
        return descricao;
    }
    public Double getPreco() {
        return preco;
    }
    public Produtos(String nome, String descricao, Double preco) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
    }
    public Produtos() {
    }

}
