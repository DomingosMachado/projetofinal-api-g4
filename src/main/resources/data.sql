-- 1. CATEGORIAS
INSERT INTO categoria (nome, descricao) VALUES 
('Eletrônicos', 'Produtos eletrônicos'),
('Livros', 'Livros de diversos gêneros'),
('Roupas', 'Vestuário masculino e feminino');

-- 2. FORNECEDORES
INSERT INTO fornecedor (nome, cnpj) VALUES
('Distribuidora Alfa LTDA', '12345678000190'),
('Comercial Beta S.A.', '98765432000101'),
('Fornecedor Gama EIRELI', '56789012000155'),
('Suprimentos Delta ME', '34567890000177'),
('Importadora Ômega Ltda', '22334455000188');

-- 3. CLIENTES
INSERT INTO cliente (
    nome, email, telefone, cpf, senha,
    cep, logradouro, complemento, bairro, numero, uf, cidade, ibge
) VALUES 
(
    'Letícia', 'lelezinhateles@gmail.com', '(21) 98765-4321', '123.456.789-00', '$2a$12$40RyJjy2W8hxvkSo2fsawuE3VYpiYQaMfOAxNfav6/1RZ2ytzJfR.',
    '22000-000', 'Rua das Flores', 'Apto 202', 'Jardim América', '123', 'RJ', 'Rio de Janeiro', 3304557
),
(
    'Maria Oliveira', 'maria.oliveira@email.com', '(11) 99876-5432', '987.654.321-00', 'maria2024',
    '01000-000', 'Av. Paulista', 'Bloco B', 'Bela Vista', '456', 'SP', 'São Paulo', 3550308
);

-- 4. FUNCIONÁRIOS
INSERT INTO funcionario (nome, email, senha, tipo_funcionario) VALUES
('João Silva', 'joao.silva@empresa.com', 'senha123', 'ADMIN'),
('Maria Oliveira', 'maria.oliveira@empresa.com', 'senha456', 'VENDEDOR'),
('Carlos Souza', 'carlos.souza@empresa.com', 'senha789', 'ESTOQUISTA');

-- 5. PRODUTOS
INSERT INTO produto (
    nome, descricao, preco, preco_atual, quantidade, estoque, categoria_id, fornecedor_id
) VALUES 
('Smartphone', 'Celular Android', 2000.00, 2000.00, 10, 10, 1, 1),
('Notebook', 'Notebook i5', 3500.00, 3500.00, 5, 5, 1, 1),
('Fone Bluetooth', 'Fone sem fio', 200.00, 200.00, 30, 30, 1, 1),
('Livro Java', 'Aprenda Java', 80.00, 80.00, 15, 15, 2, 2),
('Livro Spring', 'Spring Boot para Iniciantes', 90.00, 90.00, 10, 10, 2, 2),
('Livro Front-end', 'HTML, CSS e JS', 70.00, 70.00, 20, 20, 2, 2),
('Camiseta', 'Camiseta Algodão', 50.00, 50.00, 40, 40, 3, 3),
('Calça Jeans', 'Calça Masculina', 120.00, 120.00, 25, 25, 3, 3),
('Vestido', 'Vestido Feminino', 150.00, 150.00, 12, 12, 3, 3),
('Notebook Dell Inspiron 15', 'Notebook com processador Intel i7, 16GB RAM e SSD 512GB', 4500.00, 4300.00, 25, 25, 1, 1),
('Mouse Gamer Logitech G203', 'Mouse RGB com 8000 DPI', 200.00, 180.00, 100, 100, 1, 2);

-- 6. CARRINHOS
INSERT INTO carrinho (cliente_id, total) VALUES
(1, 0.00),
(2, 150.75);



-- 8. COMPRAS DE FORNECEDORES
INSERT INTO compra_fornecedor (data_compra, fornecedor_id) VALUES 
('2025-06-01', 1),
('2025-06-02', 2),
('2025-06-03', 1),
('2025-06-03', 3);

-- 9. COMPRA_PRODUTO

INSERT INTO carrinho_produto (carrinho_id, produto_id, quantidade, preco_unitario, subtotal) VALUES
(1, 1, 5, 1900.00, 9500.00),
(1, 2, 3, 3500.00, 10500.00),
(2, 3, 10, 200.00, 2000.00),
(2, 4, 20, 80.00, 1600.00),
(1, 5, 15, 90.00, 1350.00);

-- 10. PEDIDOS
INSERT INTO pedidos (
    cliente_id, valor_total, data_pedido, data_atualizacao, status, tipo_pedido
) VALUES 
(1, 250.00, '2025-06-03T10:30:00', '2025-06-03T11:00:00', 'PENDENTE', 'CLIENTE'),
(1, 430.50, '2025-06-02T15:15:00', NULL, 'CONFIRMADO', 'CLIENTE');

-- 11. PEDIDO_PRODUTO
INSERT INTO pedido_produto (
    pedido_id, produto_id, quantidade, preco_unitario, desconto, subtotal, compra_fornecedor_id
) VALUES 
(1, 2, 3, 50.00, 10.00, 140.00, 1),
(1, 3, 2, 100.00, 0.00, 200.00, 2),
(2, 5, 1, 90.00, 0.00, 90.00, 1);

-- 12. AVALIAÇÕES
INSERT INTO avaliacao (nota, comentario, data_avaliacao, produto_id, cliente_id) VALUES
(5, 'Produto excelente, superou minhas expectativas!', '2025-06-03 14:00:00', 1, 1),
(3, 'Produto bom, mas poderia melhorar a qualidade.', '2025-06-01 10:30:00', 2, 1),
(4, 'Gostei do produto, entrega rápida.', '2025-05-30 09:20:00', 1, 2);