package service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dto.RequestDTO;
import dto.ResponseDTO;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import model.Produtos;
import repository.ProdutosRepository;

@QuarkusTest
public class ProdutosServiceTest {

    @Inject
    ProdutosService produtosService;

    @InjectMock
    ProdutosRepository produtosRepository;

    @InjectMock
    @Channel("produto-topic-out")
    Emitter<String> emissor;


    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }



    @Test
    void testCriaProduto() throws Exception {
        // Arrange
        RequestDTO dto = new RequestDTO("Café", "Café preto forte", new BigDecimal("9.90"));

        // Mock persist -> simula que o banco atribuiu um ID ao produto
        doAnswer(invocation -> {
            Produtos p = invocation.getArgument(0);
            p.setId(1L); // simula ID do banco
            return null;
        }).when(produtosRepository).persist(any(Produtos.class));

        // Act
        produtosService.criaProduto(dto);

        // Assert: repositorio persistiu
        verify(produtosRepository).persist(any(Produtos.class));

        // Captura o JSON enviado
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(emissor, times(1)).send(captor.capture());

        String jsonEnviado = captor.getValue();
        System.out.println("JSON enviado: " + jsonEnviado);

        // Verifica se o JSON contem os campos esperados
        assertTrue(jsonEnviado.contains("Café"));
        assertTrue(jsonEnviado.contains("Café preto forte"));
        assertTrue(jsonEnviado.contains("9.90"));
        assertTrue(jsonEnviado.contains("1")); // ID simulado
    }


    @Test
    void testAtualizaProdutoWhenExists() {
        Long id = 1L;
        RequestDTO dto = new RequestDTO("Produto Atualizado", "Nova Descrição", new BigDecimal("15.75"));
        Produtos existingProduto = new Produtos("Produto Antigo", "Descrição Antiga", new BigDecimal("10.00"));
        
        when(produtosRepository.findById(id)).thenReturn(existingProduto);
    
        produtosService.atualizaProduto(id, dto);
        
        assertEquals("Produto Atualizado", existingProduto.getNome());
        assertEquals("Nova Descrição", existingProduto.getDescricao());
        assertEquals(new BigDecimal("15.75"), existingProduto.getPreco());
        verify(produtosRepository).persist(existingProduto);
    }

    @Test
    void testAtualizaProdutoWhenNotExists() {
        Long id = 999L;
        RequestDTO dto = new RequestDTO("Produto", "Descrição", new BigDecimal("10.00"));
        
        when(produtosRepository.findById(id)).thenReturn(null);
        
        produtosService.atualizaProduto(id, dto);
        
        verify(produtosRepository, never()).persist(any(Produtos.class));
    }

    @Test
    void testFindAllProdutos() {
        Produtos produto1 = new Produtos("Produto 1", "Desc 1", new BigDecimal("10.00"));
        Produtos produto2 = new Produtos("Produto 2", "Desc 2", new BigDecimal("20.00"));
        List<Produtos> produtos = Arrays.asList(produto1, produto2);
        
        when(produtosRepository.listAll()).thenReturn(produtos);
        
        List<ResponseDTO> result = produtosService.findAllProdutos();
        
        assertEquals(2, result.size());
        assertEquals("Produto 1", result.get(0).nome());
        assertEquals("Produto 2", result.get(1).nome());
    }

    @Test
    void testFindProdutoByIdWhenExists() {
        Long id = 1L;
        Produtos produto = new Produtos("Produto Test", "Descrição Test", new BigDecimal("25.50"));
        
        when(produtosRepository.findById(id)).thenReturn(produto);
        
        ResponseDTO result = produtosService.findProdutoById(id);
        
        assertNotNull(result);
        assertEquals("Produto Test", result.nome());
        assertEquals("Descrição Test", result.descricao());
        assertEquals(new BigDecimal("25.50"), result.preco());
    }

    @Test
    void testFindProdutoByIdWhenNotExists() {
        Long id = 999L;
        when(produtosRepository.findById(id)).thenReturn(null);
        ResponseDTO result = produtosService.findProdutoById(id);
        assertNull(result);
    }

    @Test
    void testDeleteProduto() {
        Long id = 1L;
        produtosService.deleteProduto(id);
        verify(produtosRepository).deleteById(id);
    }
}