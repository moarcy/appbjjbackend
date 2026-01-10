# 🥋 BJJ App - Sistema de Gerenciamento de Academia de Jiu-Jitsu

Sistema completo para gerenciamento de academia de Jiu-Jitsu, desenvolvido com Spring Boot e arquitetura REST API.

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Funcionalidades](#funcionalidades)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Configuração](#configuração)
- [Executando o Projeto](#executando-o-projeto)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [API Endpoints](#api-endpoints)
- [Regras de Negócio](#regras-de-negócio)
- [Sistema de Permissões](#sistema-de-permissões)
- [Contribuindo](#contribuindo)

---

## 🎯 Sobre o Projeto

O **BJJ App** é uma solução completa para gerenciamento de academias de Jiu-Jitsu, permitindo controle de alunos, turmas, chamadas, progressão de faixas e graus, além de um sistema robusto de autenticação e autorização.

### Principais Características

- ✅ Gerenciamento completo de alunos (infantil e adulto)
- ✅ Sistema de progressão de faixas e graus
- ✅ Controle de chamadas e presenças
- ✅ Gerenciamento de turmas e professores
- ✅ Sistema de critérios de graduação personalizados
- ✅ Histórico detalhado de alterações
- ✅ Autenticação JWT com controle de permissões
- ✅ API RESTful documentada

---

## 🚀 Funcionalidades

### Gestão de Alunos

- Cadastro completo de alunos (adultos e crianças)
- Controle de faixas e graus (0 a 4)
- Registro de responsáveis para menores de 18 anos
- Acompanhamento de aulas acumuladas
- Aulas desde a última graduação
- Histórico completo de alterações

### Sistema de Graduação

- Faixas infantis: Branca, Cinza, Amarela, Laranja, Verde
- Faixas adultas: Branca, Azul, Roxa, Marrom, Preta
- Sistema de graus (0 a 4) por faixa
- Critérios personalizados por faixa
- Cálculo automático de requisitos
- Idade mínima por faixa

### Controle de Presenças

- Registro de chamadas por turma
- Marcação de presenças individuais ou em lote
- Estatísticas de presença por período
- Contabilização automática de aulas
- Reset ao conceder grau ou trocar faixa

### Gerenciamento de Turmas

- Criação de turmas por modalidade
- Definição de dias e horários
- Associação de alunos e professores
- Controle de turmas ativas/inativas

### Sistema de Autenticação

- Login com JWT (JSON Web Token)
- 3 níveis de acesso: Admin, Professor, Aluno
- Geração automática de credenciais
- Controle granular de permissões

---

## 🛠️ Tecnologias Utilizadas

### Backend

- **Java 21** - Linguagem de programação
- **Spring Boot 3.x** - Framework principal
- **Spring Security** - Autenticação e autorização
- **Spring Data JPA** - Persistência de dados
- **MySQL** - Banco de dados relacional
- **Lombok** - Redução de boilerplate
- **JWT (JSON Web Token)** - Autenticação stateless
- **Gradle** - Gerenciamento de dependências

### Ferramentas de Desenvolvimento

- **IntelliJ IDEA** - IDE recomendada
- **Postman** - Testes de API
- **Git** - Controle de versão

---

## 📦 Pré-requisitos

Antes de começar, você precisará ter instalado:

- **Java 21** ou superior
- **MySQL 8.0** ou superior
- **Gradle 8.x** (ou usar o wrapper incluído)
- **Git** (para clonar o repositório)

---

## 💻 Instalação

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/bjjapp.git
cd bjjapp
```

### 2. Configure o banco de dados

Crie um banco de dados MySQL:

```sql
CREATE DATABASE bjjapp;
CREATE USER 'bjjapp_user'@'localhost' IDENTIFIED BY 'sua_senha';
GRANT ALL PRIVILEGES ON bjjapp.* TO 'bjjapp_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configure as variáveis de ambiente

Edite o arquivo `src/main/resources/application.properties`:

```properties
# Configuração do Banco de Dados
spring.datasource.url=jdbc:mysql://localhost:3306/bjjapp
spring.datasource.username=bjjapp_user
spring.datasource.password=sua_senha

# Configuração JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Porta do servidor
server.port=8080
```

---

## ⚙️ Configuração

### Credenciais de Admin Padrão

Ao iniciar o sistema pela primeira vez, um usuário administrador será criado automaticamente:

- **Username:** `admin`
- **Password:** `password`

⚠️ **Importante:** Altere essas credenciais em produção!

### JWT Secret

Configure a chave secreta do JWT no `JwtUtil.java` ou use variável de ambiente:

```java
private static final String SECRET_KEY = "sua-chave-secreta-aqui";
```

---

## 🚀 Executando o Projeto

### Modo Desenvolvimento

```bash
# Usando Gradle Wrapper (recomendado)
./gradlew bootRun

# Ou usando Gradle instalado
gradle bootRun
```

### Build de Produção

```bash
# Gerar JAR
./gradlew clean build -x test

# Executar JAR
java -jar build/libs/bjjapp-0.0.1-SNAPSHOT.jar
```

O servidor estará disponível em: `http://localhost:8080`

---

## 📁 Estrutura do Projeto

```
bjjapp/
├── src/main/java/bjjapp/
│   ├── BjjappApplication.java          # Classe principal
│   ├── config/                         # Configurações
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── JwtUtil.java
│   │   └── SecurityConfig.java
│   ├── controller/                     # Controllers REST
│   │   ├── AuthController.java
│   │   ├── ChamadaController.java
│   │   ├── ProfessorController.java
│   │   ├── TurmaController.java
│   │   └── UserController.java
│   ├── entity/                         # Entidades JPA
│   │   ├── User.java
│   │   ├── Turma.java
│   │   ├── Chamada.java
│   │   ├── UserHistorico.java
│   │   └── UserPlainPassword.java
│   ├── enums/                          # Enumerações
│   │   ├── Faixa.java
│   │   ├── Role.java
│   │   ├── Modalidade.java
│   │   └── DiaSemana.java
│   ├── repository/                     # Repositórios JPA
│   └── service/                        # Serviços de negócio
│       ├── UserService.java
│       ├── TurmaService.java
│       ├── ChamadaService.java
│       ├── ProfessorService.java
│       ├── UserHistoricoService.java
│       └── RequisitosGraduacaoService.java
└── src/main/resources/
    └── application.properties          # Configurações da aplicação
```

---

## 🔌 API Endpoints

### Autenticação

```
POST   /api/auth/login              # Login do usuário
POST   /api/auth/register           # Registro de novo usuário
```

### Usuários (Alunos)

```
GET    /api/users/findAll           # Listar todos os usuários
GET    /api/users/findById/{id}     # Buscar usuário por ID
GET    /api/users/me                # Obter perfil do usuário logado
POST   /api/users/save              # Criar novo usuário
PUT    /api/users/update/{id}       # Atualizar usuário
DELETE /api/users/deleteById/{id}   # Deletar usuário
GET    /api/users/status/{id}       # Status de progressão
GET    /api/users/historico/{id}    # Histórico de alterações
POST   /api/users/conceder-grau/{id} # Conceder grau ao aluno
PUT    /api/users/trocar-faixa/{id} # Trocar faixa do aluno
GET    /api/users/graduacao/{id}    # Critérios de graduação
PUT    /api/users/graduacao/{id}    # Atualizar critérios
GET    /api/users/credenciais/{id}  # Obter credenciais de acesso
```

### Turmas

```
GET    /api/turmas/findAll          # Listar todas as turmas
GET    /api/turmas/findById/{id}    # Buscar turma por ID
POST   /api/turmas                  # Criar nova turma
PUT    /api/turmas/update/{id}      # Atualizar turma
DELETE /api/turmas/deleteById/{id}  # Deletar turma
```

### Chamadas

```
GET    /api/chamadas/findAll                    # Listar todas as chamadas
POST   /api/chamadas/iniciar                    # Iniciar nova chamada
POST   /api/chamadas/{id}/presencas             # Marcar presenças
POST   /api/chamadas/{id}/finalizar             # Finalizar chamada
GET    /api/chamadas/presencas-ausencias/{id}   # Presenças e ausências
GET    /api/chamadas/abertas                    # Chamadas não finalizadas
```

### Professores

```
GET    /api/professores/findAll     # Listar todos os professores
POST   /api/professores/save        # Criar novo professor
PUT    /api/professores/update/{id} # Atualizar professor
DELETE /api/professores/deleteById/{id} # Deletar professor
```

---

## 📜 Regras de Negócio

### Sistema de Graduação

#### Aulas Necessárias por Grau

| Grau | Aulas Necessárias |
|------|-------------------|
| 0 → 1 | 20 aulas |
| 1 → 2 | 20 aulas |
| 2 → 3 | 30 aulas |
| 3 → 4 | 40 aulas |

#### Sequência de Faixas Infantis (< 15 anos)

1. **Branca** → Cinza (7+ anos)
2. **Cinza** → Amarela
3. **Amarela** → Laranja (10+ anos)
4. **Laranja** → Verde
5. **Verde** → Azul (16+ anos)

#### Sequência de Faixas Adultas (15+ anos)

1. **Branca** → Azul
2. **Azul** → Roxa (19+ anos)
3. **Roxa** → Marrom
4. **Marrom** → Preta (19+ anos)

### Critérios de Graduação

Cada faixa possui critérios específicos que devem ser cumpridos:

- **Técnicas obrigatórias** por posição
- **Idade mínima** por faixa
- **Participação em campeonatos** (faixas coloridas)
- **Conhecimento de regras**
- **4 graus completos** para troca de faixa

### Reset de Contadores

Os contadores de aulas são zerados quando:
- ✅ Um grau é concedido
- ✅ Uma faixa é trocada

---

## 🔐 Sistema de Permissões

### Níveis de Acesso

| Funcionalidade | Admin | Professor | Aluno |
|---------------|-------|-----------|-------|
| Visualizar próprios dados | ✅ | ✅ | ✅ |
| Visualizar todos os alunos | ✅ | ✅ | ❌ |
| Criar/editar alunos | ✅ | ✅ | ❌ |
| Conceder grau/faixa | ✅ | ✅ | ❌ |
| Gerenciar turmas | ✅ | ✅ | ❌ |
| Criar chamadas | ✅ | ✅ | ❌ |
| Marcar presenças | ✅ | ✅ | ❌ |
| Ver credenciais | ✅ | ✅ | ❌ |
| Criar professores | ✅ | ❌ | ❌ |

---

## 🤝 Contribuindo

Contribuições são sempre bem-vindas!

1. Faça um Fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/NovaFuncionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/NovaFuncionalidade`)
5. Abra um Pull Request

---

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 👨‍💻 Autor

Desenvolvido com ❤️ para a comunidade de Jiu-Jitsu

---

## 📞 Suporte

Para dúvidas e suporte, entre em contato através:

- 📧 Email: suporte@bjjapp.com
- 🐛 Issues: [GitHub Issues](https://github.com/seu-usuario/bjjapp/issues)

---

## 🎯 Roadmap

- [ ] Dashboard com estatísticas
- [ ] Relatórios em PDF
- [ ] Sistema de mensagens
- [ ] Integração com calendário
- [ ] App mobile (React Native)
- [ ] Sistema de pagamentos
- [ ] Notificações push

---

**🥋 Oss! Bons treinos!**

