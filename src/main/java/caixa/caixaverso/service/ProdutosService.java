package caixa.caixaverso.service;

import java.util.List;
import java.util.stream.Collectors;

import caixa.caixaverso.dto.ProdutosDTO;
import caixa.caixaverso.model.Produtos;
import caixa.caixaverso.repository.ProdutosRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProdutosService {

    private final ProdutosRepository produtosRepository;

    public ProdutosService(ProdutosRepository produtosRepository) {
        this.produtosRepository = produtosRepository;
    }

    public void criaProduto(ProdutosDTO produtosDTO) {
        var produto = new Produtos(produtosDTO.nome(), produtosDTO.descricao(), produtosDTO.preco());
        produtosRepository.persist(produto);
    }

    public void atualizaProduto(Long id, ProdutosDTO produtosDTO) {
        var produto = produtosRepository.findById(id);
        if (produto != null) {
            produto.setNome(produtosDTO.nome());
            produto.setDescricao(produtosDTO.descricao());
            produto.setPreco(produtosDTO.preco());
            produtosRepository.persist(produto);
        }
    }

    public List<ProdutosDTO> findAllProdutos() {
        var produtos = produtosRepository.listAll();
        return produtos.stream()
                .map(p -> new ProdutosDTO(p.getNome(), p.getDescricao(), p.getPreco()))
                .collect(Collectors.toList());
    }

    public ProdutosDTO findProdutoById(Long id) {
        var produto = produtosRepository.findById(id);
        if (produto != null) {
            return new ProdutosDTO(produto.getNome(), produto.getDescricao(), produto.getPreco());
        }
        return null;
    }

    public void deleteProduto(Long id) {
        produtosRepository.deleteById(id);
    }

}
