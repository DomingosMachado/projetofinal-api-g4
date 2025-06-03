package org.serratec.projetofinal_api_g4.service;

import org.serratec.projetofinal_api_g4.domain.Carrinho;
import org.serratec.projetofinal_api_g4.domain.CarrinhoProduto;
import org.serratec.projetofinal_api_g4.domain.Cliente;
import org.serratec.projetofinal_api_g4.domain.Pedido;
import org.serratec.projetofinal_api_g4.domain.Produto;
import org.serratec.projetofinal_api_g4.dto.CarrinhoRequestDTO;
import org.serratec.projetofinal_api_g4.dto.CarrinhoResponseDTO;
import org.serratec.projetofinal_api_g4.dto.PedidoDTO;
import org.serratec.projetofinal_api_g4.enums.PedidoStatus;
import org.serratec.projetofinal_api_g4.repository.CarrinhoProdutoRepository;
import org.serratec.projetofinal_api_g4.repository.CarrinhoRepository;
import org.serratec.projetofinal_api_g4.repository.ClienteRepository;
import org.serratec.projetofinal_api_g4.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarrinhoService {

        @Autowired
        private CarrinhoRepository carrinhoRepository;

        @Autowired
        private CarrinhoProdutoRepository carrinhoProdutoRepository;

        @Autowired
        private ClienteRepository clienteRepository;

        @Autowired
        private ProdutoRepository produtoRepository;


        @Autowired
        private PedidoService pedidoService;

        @Transactional
        public CarrinhoResponseDTO criarOuAtualizarCarrinho(Long clienteId, CarrinhoRequestDTO dto) {
                Cliente cliente = clienteRepository.findById(clienteId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Cliente não encontrado. Id: " + clienteId));

                Carrinho carrinho = carrinhoRepository.findByClienteId(clienteId)
                                .orElseGet(() -> {
                                        Carrinho novo = new Carrinho();
                                        novo.setCliente(cliente);
                                        return novo;
                                });

                // Limpa itens antigos para atualização
                carrinhoProdutoRepository.deleteAll(carrinho.getItens());
                carrinho.getItens().clear();

                // Adiciona novos itens
                List<CarrinhoProduto> itens = dto.getItens().stream().map(itemDto -> {
                        Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                        "Produto não encontrado. Id: " + itemDto.getProdutoId()));

                        CarrinhoProduto item = new CarrinhoProduto();
                        item.setCarrinho(carrinho);
                        item.setProduto(produto);
                        item.setQuantidade(itemDto.getQuantidade());
                        item.setPrecoUnitario(produto.getPreco());
                        item.calcularSubtotal();
                        return item;
                }).collect(Collectors.toList());

                carrinho.getItens().addAll(itens);

                BigDecimal total = carrinho.getItens().stream()
                                .map(CarrinhoProduto::getSubtotal)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                carrinho.setTotal(total);

                carrinhoRepository.save(carrinho);
                carrinhoProdutoRepository.saveAll(itens);

                return new CarrinhoResponseDTO(carrinho);
        }

        public CarrinhoResponseDTO buscarPorCliente(Long clienteId) {
                Carrinho carrinho = carrinhoRepository.findByClienteId(clienteId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Carrinho não encontrado para o cliente. Id: " + clienteId));
                return new CarrinhoResponseDTO(carrinho);
        }

        @Transactional
        public void removerItem(Long carrinhoId, Long itemId) {
                Carrinho carrinho = carrinhoRepository.findById(carrinhoId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Carrinho não encontrado. Id: " + carrinhoId));

                CarrinhoProduto item = carrinhoProdutoRepository.findById(itemId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Item do carrinho não encontrado. Id: " + itemId));

                if (!item.getCarrinho().getId().equals(carrinhoId)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Item não pertence ao carrinho informado");
                }

                carrinho.getItens().remove(item);
                carrinhoProdutoRepository.delete(item);

                BigDecimal total = carrinho.getItens().stream()
                                .map(CarrinhoProduto::getSubtotal)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                carrinho.setTotal(total);

                carrinhoRepository.save(carrinho);
        }

      @Transactional
public PedidoDTO finalizarCarrinho(Long clienteId) {
    Carrinho carrinho = carrinhoRepository.findByClienteId(clienteId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Carrinho não encontrado para o cliente. Id: " + clienteId));

    if (carrinho.getItens().isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Carrinho está vazio.");
    }

    // Chama método único do PedidoService para criar pedido
    Pedido pedido = pedidoService.criarPedido(carrinho.getCliente(), carrinho.getItens(), carrinho.getTotal(), PedidoStatus.PENDENTE);

    // Limpa o carrinho após finalizar o pedido
    carrinhoProdutoRepository.deleteAll(carrinho.getItens());
    carrinho.getItens().clear();
    carrinho.setTotal(BigDecimal.ZERO);
    carrinhoRepository.save(carrinho);

    return new PedidoDTO(pedido);
}
}
