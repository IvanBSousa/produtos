package service;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dto.RequestDTO;
import dto.ResponseDTO;
import io.quarkus.arc.profile.UnlessBuildProfile;
import io.quarkus.logging.Log;
import io.quarkus.narayana.jta.QuarkusTransaction;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import model.Produtos;
import repository.ProdutosRepository;

@ApplicationScoped
public class ProdutosService {

    private final ProdutosRepository produtosRepository;

    public ProdutosService(ProdutosRepository produtosRepository) {
        this.produtosRepository = produtosRepository;
    }

    @UnlessBuildProfile("test")
    @Inject
    @Channel("produto-topic-out")
    Emitter<String> emissor;

    //@Outgoing("produto-criado")
    public void criaProduto(RequestDTO produtosDTO) {
        var produto = new Produtos(produtosDTO.nome(), produtosDTO.descricao(), produtosDTO.preco());
        QuarkusTransaction.requiringNew().run(() -> {
            produtosRepository.persist(produto);
        });
        try {
            var resposta = new ResponseDTO(produto.getId(), produto.getNome(), produto.getDescricao(), produto.getPreco(), produto.getDataCriacao());
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(resposta);
            emissor.send(json);
            Log.info("Mensagem enviada para o Kafka: " + json);
        } catch (JsonProcessingException e) {
            Log.error("Erro ao serializar produto para Kafka", e);
        }
    }

    @Transactional
    public void atualizaProduto(Long id, RequestDTO produtosDTO) {
        var produto = produtosRepository.findById(id);
        if (produto != null) {
            produto.setNome(produtosDTO.nome());
            produto.setDescricao(produtosDTO.descricao());
            produto.setPreco(produtosDTO.preco());
            produtosRepository.persist(produto);
        }
    }

    public List<ResponseDTO> findAllProdutos() {
        var produtos = produtosRepository.listAll();
        return produtos.stream()
                .map(p -> new ResponseDTO(p.getId(), p.getNome(), p.getDescricao(), p.getPreco(), p.getDataCriacao()))
                .collect(Collectors.toList());
    }

    public ResponseDTO findProdutoById(Long id) {
        var produto = produtosRepository.findById(id);
        if (produto != null) {
            Log.info("Produto encontrado no Banco de Dados");
            return new ResponseDTO(produto.getId(),produto.getNome(), produto.getDescricao(), produto.getPreco(), produto.getDataCriacao());
        }
        return null;
    }

    @Transactional
    public void deleteProduto(Long id) {
        produtosRepository.deleteById(id);
    }

}
