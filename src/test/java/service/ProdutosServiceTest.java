package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dto.ProdutosDTO;
import model.Produtos;
import repository.ProdutosRepository;

public class ProdutosServiceTest {

    @Mock
    private ProdutosRepository produtosRepository;

    private ProdutosService produtosService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        produtosService = new ProdutosService(produtosRepository);
    }

    @Test
    void testCriaProduto() {
        ProdutosDTO dto = new ProdutosDTO("Produto Test", "Descrição Test", new BigDecimal("10.50"));
        produtosService.criaProduto(dto);
        verify(produtosRepository).persist(any(Produtos.class));
    }

    @Test
    void testAtualizaProdutoWhenExists() {
        Long id = 1L;
        ProdutosDTO dto = new ProdutosDTO("Produto Atualizado", "Nova Descrição", new BigDecimal("15.75"));
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
        ProdutosDTO dto = new ProdutosDTO("Produto", "Descrição", new BigDecimal("10.00"));
        
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
        
        List<ProdutosDTO> result = produtosService.findAllProdutos();
        
        assertEquals(2, result.size());
        assertEquals("Produto 1", result.get(0).nome());
        assertEquals("Produto 2", result.get(1).nome());
    }

    @Test
    void testFindProdutoByIdWhenExists() {
        Long id = 1L;
        Produtos produto = new Produtos("Produto Test", "Descrição Test", new BigDecimal("25.50"));
        
        when(produtosRepository.findById(id)).thenReturn(produto);
        
        ProdutosDTO result = produtosService.findProdutoById(id);
        
        assertNotNull(result);
        assertEquals("Produto Test", result.nome());
        assertEquals("Descrição Test", result.descricao());
        assertEquals(new BigDecimal("25.50"), result.preco());
    }

    @Test
    void testFindProdutoByIdWhenNotExists() {
        Long id = 999L;
        when(produtosRepository.findById(id)).thenReturn(null);
        ProdutosDTO result = produtosService.findProdutoById(id);
        assertNull(result);
    }

    @Test
    void testDeleteProduto() {
        Long id = 1L;
        produtosService.deleteProduto(id);
        verify(produtosRepository).deleteById(id);
    }
}