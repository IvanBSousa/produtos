package caixa.caixaverso.repository;

import caixa.caixaverso.model.Produtos;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProdutosRepository implements PanacheRepository<Produtos>{

}
