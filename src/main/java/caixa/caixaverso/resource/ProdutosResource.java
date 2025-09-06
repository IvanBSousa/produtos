package caixa.caixaverso.resource;

import caixa.caixaverso.dto.ProdutosDTO;
import caixa.caixaverso.service.ProdutosService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
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
    public Response createProduto(ProdutosDTO produtosDTO) {
        produtosService.createProduto(produtosDTO);
        return Response.status(Response.Status.CREATED).build();
    }

}
