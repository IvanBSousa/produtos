

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.eclipse.microprofile.reactive.messaging.Emitter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dto.RequestDTO;
import dto.ResponseDTO;
import io.quarkus.test.junit.QuarkusTest;
import model.Produtos;
import repository.ProdutosRepository;
import service.ProdutosService;

@QuarkusTest
class ProdutosServiceTest {

    private ProdutosRepository produtosRepository;
    private Emitter<String> emissor;
    private ProdutosService produtosService;

    @BeforeEach
    void setup() {
        produtosRepository = mock(ProdutosRepository.class);
        emissor = mock(Emitter.class);
        produtosService = new ProdutosService(produtosRepository);
        produtosService.emissor = emissor; // Injeta o mock manualmente
    }

    @Test
    void testCriaProduto() throws Exception {
        RequestDTO dto = new RequestDTO("Café", "Café preto forte", new BigDecimal("9.90"));

        doNothing().when(produtosRepository).persist(any(Produtos.class));

        produtosService.criaProduto(dto);

        ArgumentCaptor<Produtos> captor = ArgumentCaptor.forClass(Produtos.class);
        verify(produtosRepository).persist(captor.capture());
        Produtos produtoPersistido = captor.getValue();

        assertEquals("Café", produtoPersistido.getNome());
        assertEquals("Café preto forte", produtoPersistido.getDescricao());
        assertEquals(new BigDecimal("9.90"), produtoPersistido.getPreco());

        ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
        verify(emissor).send(jsonCaptor.capture());
        String jsonEnviado = jsonCaptor.getValue();
        assertTrue(jsonEnviado.contains("Café"));
        assertTrue(jsonEnviado.contains("Café preto forte"));
        assertTrue(jsonEnviado.contains("9.90"));
    }

    @Test
    void testAtualizaProduto() {
        Produtos produto = new Produtos("Café", "Descrição antiga", new BigDecimal("8.50"));
        produto.setId(1L);

        when(produtosRepository.findById(1L)).thenReturn(produto);
        doNothing().when(produtosRepository).persist(produto);

        RequestDTO dto = new RequestDTO("Café Atualizado", "Descrição nova", new BigDecimal("9.90"));
        produtosService.atualizaProduto(1L, dto);

        assertEquals("Café Atualizado", produto.getNome());
        assertEquals("Descrição nova", produto.getDescricao());
        assertEquals(new BigDecimal("9.90"), produto.getPreco());

        verify(produtosRepository).persist(produto);
    }

    @Test
    void testFindAllProdutos() {
        Produtos p1 = new Produtos("Café", "Café preto", new BigDecimal("9.90"));
        p1.setId(1L);
        p1.setDataCriacao(Instant.now());

        Produtos p2 = new Produtos("Chá", "Chá verde", new BigDecimal("7.50"));
        p2.setId(2L);
        p2.setDataCriacao(Instant.now());

        when(produtosRepository.listAll()).thenReturn(Arrays.asList(p1, p2));

        List<ResponseDTO> produtos = produtosService.findAllProdutos();
        assertEquals(2, produtos.size());
        assertEquals("Café", produtos.get(0).nome());
        assertEquals("Chá", produtos.get(1).nome());
    }

    @Test
    void testFindProdutoByIdExists() {
        Produtos produto = new Produtos("Café", "Café preto", new BigDecimal("9.90"));
        produto.setId(1L);
        produto.setDataCriacao(Instant.now());

        when(produtosRepository.findById(1L)).thenReturn(produto);

        ResponseDTO dto = produtosService.findProdutoById(1L);
        assertNotNull(dto);
        assertEquals("Café", dto.nome());
    }

    @Test
    void testFindProdutoByIdNotExists() {
        when(produtosRepository.findById(99L)).thenReturn(null);

        ResponseDTO dto = produtosService.findProdutoById(99L);
        assertNull(dto);
    }
    
    @Test
    void testDeleteProduto() {
        Long id = 1L;
        produtosService.deleteProduto(id);
        verify(produtosRepository).deleteById(id);
    }
}
