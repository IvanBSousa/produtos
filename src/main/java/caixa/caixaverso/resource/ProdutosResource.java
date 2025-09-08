package caixa.caixaverso.resource;

import caixa.caixaverso.dto.ProdutosDTO;
import caixa.caixaverso.service.ProdutosService;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/produtos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProdutosResource {

    private final ProdutosService produtosService;

    public ProdutosResource(ProdutosService produtosService) {
        this.produtosService = produtosService;
    }

    @POST
    @Path("/produtos")
    @Transactional
    public Response createProduto(ProdutosDTO produtosDTO) {

        produtosService.criaProduto(produtosDTO);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response consultaProdutos() {
        var todosProdutos = produtosService.findAllProdutos();
        return Response.status(Response.Status.OK).entity(todosProdutos).build();
    }

    @GET
    @Path("/produtos/{id}")
    public Response consultaProdutoPorId(@PathParam(value = "id") Long id) {
        var produto = produtosService.findProdutoById(id);
        return Response.status(Response.Status.OK).entity(produto).build();
    }

    @PUT
    @Path("/produtos/{id}")
    @Transactional
    public Response updateProduto(@PathParam(value = "id") Long id, ProdutosDTO produtosDTO) {
        produtosService.atualizaProduto(id, produtosDTO);
        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("/produtos/{id}")
    @Transactional
    public Response deleteProduto(@PathParam(value = "id") Long id) {
        produtosService.deleteProduto(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
