package caixa.caixaverso.repository;

import java.util.Optional;

import caixa.caixaverso.model.Produtos;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProdutosRepository implements PanacheRepository<Produtos> {

    public Optional<Produtos> findByName(String nome) {
        return find("nome", nome).firstResultOptional();
    }

}
