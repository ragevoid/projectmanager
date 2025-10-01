# Project Manager

## Descrição do Projeto
**Project Manager** é um sistema para o gerenciamento do ciclo de vida de projetos. Ele oferece ferramentas para acompanhar projetos desde a análise de viabilidade até a sua conclusão, incluindo o gerenciamento de equipe, orçamento e riscos.

O sistema se integra com uma API externa (mockada) para a gestão de membros da equipe, simulando um ambiente de microserviços.

---

## 📑 Sumário
1. [Descrição do Projeto](#descrição-do-projeto)
2. [Regras de Negócio](#regras-de-negócio)
3. [Princípios de Desenvolvimento](#princípios-de-desenvolvimento)
4. [Testando o Projeto](#testando-o-projeto)   
---

## Regras de Negócio

### Gerenciamento de Projetos
O sistema permite o **CRUD completo** (Criar, Ler, Atualizar, Deletar) de projetos com os seguintes campos:
* `Nome`
* `Data de início`
* `Previsão de término`
* `Data real de término`
* `Orçamento total`
* `Descrição`
* `Gerente responsável`
* `Status atual`

### Classificação de Risco
O risco do projeto é calculado automaticamente com base em regras predefinidas:
* **Baixo Risco:** Orçamento até R$ 100.000 e duração do projeto menor ou igual a 3 meses.
* **Médio Risco:** Orçamento entre R$ 100.001 e R$ 500.000 OU duração entre 3 e 6 meses.
* **Alto Risco:** Orçamento acima de R$ 500.000 OU duração superior a 6 meses.

### Status do Projeto
O status do projeto segue um fluxo de transição estrito:
* `Em análise` → `Análise realizada` → `Análise aprovada` → `Iniciado` → `Planejado` → `Em andamento` → `Encerrado`
* O status `Cancelado` pode ser aplicado a qualquer momento.
* A exclusão de um projeto não é permitida se ele estiver nos status `Iniciado`, `Em andamento` ou `Encerrado`.

### Gerenciamento de Membros
* O cadastro de membros é feito exclusivamente através de uma **API REST externa mockada**.
* Apenas membros com a atribuição **“funcionário”** podem ser associados a um projeto.
* Cada projeto pode ter entre **1 e 10 membros** alocados.
* Um membro não pode estar alocado em mais de **3 projetos simultaneamente** com status diferente de `Encerrado` ou `Cancelado`.

### Relatórios
Um endpoint de relatório está disponível para gerar um resumo do portfólio, incluindo:
* Quantidade de projetos por status.
* Total orçado por status.
* Média de duração dos projetos `Encerrados`.
* Total de membros únicos alocados em projetos.

---

## Princípios de Desenvolvimento
O projeto foi desenvolvido seguindo as seguintes boas práticas:
* **Princípios SOLID**
* **Clean Code**

---

## Como Rodar e Testar o Projeto

### Pré-requisitos
* Docker e Docker Compose
* Java JDK 17+

### Configuração
Antes de iniciar, você precisa criar os arquivos de configuração para as credenciais do banco de dados PostgreSQL.

* **Para uso local (sem Docker)**:
    Crie o arquivo `src/main/resources/application-local.properties` e adicione as seguintes linhas, substituindo pelas suas credenciais:
    ```properties
    spring.datasource.username=seu_usuario
    spring.datasource.password=sua_senha
    ```
    
* **Para uso com Docker**:
    Crie o arquivo `.env` na raiz do projeto e adicione suas credenciais:
    ```env
    POSTGRES_USER=seu_usuario
    POSTGRES_PASSWORD=sua_senha
    ```

### Execução
1.  Clone o repositório:
    ```bash
    git clone [URL_DO_SEU_REPOSITÓRIO]
    ```
2.  Navegue até o diretório do projeto.
3.  Execute a construção das imagens e inicie os containers:
    ```bash
    docker-compose build
    docker-compose up -d
    ```

### Testando o Projeto
Com a aplicação em execução, você pode acessar a documentação completa dos endpoints via **Swagger**.
* Abra seu navegador e navegue para: `http://localhost:3333/swagger-ui.html`

A API mockada de membros estará disponível em `http://localhost:3333/mock-api/membros`.
Existe um "Usuario" criado dinamicamente em memoria para garantir Security com Basic Authorization: 
 * username: admin,  
 * password: admin123
