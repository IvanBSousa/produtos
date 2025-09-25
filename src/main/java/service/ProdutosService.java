package service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import com.fasterxml.jackson.databind.ObjectMapper;

import dto.ProdutosDTO;
import io.quarkus.logging.Log;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.smallrye.reactive.messaging.annotations.Channel;
import io.smallrye.reactive.messaging.annotations.Emitter;
import model.Produtos;
import repository.ProdutosRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ProdutosService {

    private final ProdutosRepository produtosRepository;

    public ProdutosService(ProdutosRepository produtosRepository) {
        this.produtosRepository = produtosRepository;
    }

    @Inject
    @Channel("produto-topic-out")
    Emitter<String> emissor;

    //@Outgoing("produto-criado")
    public void criaProduto(ProdutosDTO produtosDTO) {
        var produto = new Produtos(produtosDTO.nome(), produtosDTO.descricao(), produtosDTO.preco());
        QuarkusTransaction.requiringNew().run(() -> {
            produtosRepository.persist(produto);
        });
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(produtosDTO);
            emissor.send(json);
            Log.info("Mensagem enviada para o Kafka: " + json);
        } catch (Exception e) {
            Log.error("Erro ao serializar produto para Kafka", e);
    }
    }

    @Transactional
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
            Log.info("Produto encontrado no Banco de Dados");
            return new ProdutosDTO(produto.getNome(), produto.getDescricao(), produto.getPreco());
        }
        return null;
    }

    @Transactional
    public void deleteProduto(Long id) {
        produtosRepository.deleteById(id);
    }

}
