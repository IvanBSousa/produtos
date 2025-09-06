package caixa.caixaverso.service;

import caixa.caixaverso.dto.ProdutosDTO;
import caixa.caixaverso.model.Produtos;
import caixa.caixaverso.repository.ProdutosRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ProdutosService {

    private final ProdutosRepository produtosRepository;

    public ProdutosService(ProdutosRepository produtosRepository) {
        this.produtosRepository = produtosRepository;
    }

    @Transactional
    public void createProduto(ProdutosDTO produtosDTO) {
        var produto = new Produtos(produtosDTO.nome(), produtosDTO.descricao(), produtosDTO.preco());
        produtosRepository.persist(produto);
    }

}
