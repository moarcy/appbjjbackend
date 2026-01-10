package bjjapp.controller;

import bjjapp.entity.User;
import bjjapp.entity.UserHistorico;
import bjjapp.entity.Turma;
import bjjapp.enums.Faixa;
import bjjapp.enums.Role;
import bjjapp.service.UserService;
import bjjapp.service.RequisitosGraduacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:5173", "http://localhost:3000"})
public class UserController {

    private final UserService userService;
    private final RequisitosGraduacaoService requisitosService;
    private final BCryptPasswordEncoder passwordEncoder; // ✅ Injetar BCryptPasswordEncoder

    public record UserRequest(
        String nome,
        Integer idade,
        String faixa,
        Integer grau,  // ✅ Adicionado campo grau
        String dataNascimento,
        Set<Long> turmasIds,
        // Campos para responsáveis (menores de 18 anos)
        String nomeResponsavel,
        String whatsappResponsavel,
        // Campo para contato (maiores de 18 anos)
        String telefoneContato,
        // Campos adicionais para todos os usuários
        String dataInicioPratica, // Formato "YYYY-MM"
        String dataUltimaGraduacao // Formato "YYYY-MM"
    ) {}

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody UserRequest request) {
        try {
            if (request.nome() == null || request.nome().isBlank()) {
                return ResponseEntity.badRequest().body("Nome é obrigatório");
            }

            User user = User.builder()
                .nome(request.nome())
                .idade(request.idade())
                .grau(request.grau() != null ? request.grau() : 0)  // ✅ Setando grau
                .nomeResponsavel(request.nomeResponsavel())
                .whatsappResponsavel(request.whatsappResponsavel())
                .telefoneContato(request.telefoneContato())
                .dataInicioPratica(request.dataInicioPratica())
                .dataUltimaGraduacao(request.dataUltimaGraduacao())
                .build();

            if (request.faixa() != null) {
                try {
                    user.setFaixa(Faixa.valueOf(request.faixa().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body("Faixa inválida: " + request.faixa());
                }
            }

            if (request.dataNascimento() != null) {
                user.setDataNascimento(LocalDate.parse(request.dataNascimento()));
            }

            // Gerar credenciais automaticamente
            String username = generateUsername(request.nome());
            String password = generatePassword();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));

            User salvo = userService.save(user, request.turmasIds());
            UserCreationResponse response = new UserCreationResponse(salvo, username, password);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao salvar: " + e.getMessage());
        }
    }

    @PostMapping("/saveProfessor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> saveProfessor(@RequestBody UserRequest request) {
        try {
            if (request.nome() == null || request.nome().isBlank()) {
                return ResponseEntity.badRequest().body("Nome é obrigatório");
            }

            User professor = User.builder()
                .nome(request.nome())
                .role(Role.PROFESSOR)
                .build();

            // Gerar credenciais automaticamente
            String username = generateUsername(request.nome());
            String password = generatePassword();
            professor.setUsername(username);
            professor.setPassword(passwordEncoder.encode(password));

            User salvo = userService.save(professor);
            return ResponseEntity.ok(salvo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao salvar professor: " + e.getMessage());
        }
    }

    @GetMapping("/findAll")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/findById/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR') or @userSecurity.isSelf(#id)")
    public ResponseEntity<?> findById(@PathVariable Long id, HttpServletRequest request) {
        try {
            User user = userService.findById(id);
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("id", user.getId());
            response.put("nome", user.getNome());
            response.put("idade", user.getIdade());
            response.put("faixa", user.getFaixa().name());
            response.put("grau", user.getGrau());
            response.put("dataNascimento", user.getDataNascimento() != null ? user.getDataNascimento().toString() : null);
            response.put("turmasIds", user.getTurmas() != null ? user.getTurmas().stream().map(Turma::getId).toList() : java.util.Collections.emptyList());
            response.put("aulasAcumuladas", user.getAulasAcumuladas());
            response.put("aulasDesdeUltimaGraduacao", user.getAulasDesdeUltimaGraduacao());
            response.put("ultimaGraduacao", user.getUltimaGraduacao() != null ? user.getUltimaGraduacao().toString() : null);
            response.put("criteriosConcluidos", user.getCriteriosConcluidos() != null ? user.getCriteriosConcluidos() : new java.util.HashSet<>());
            // Novos campos para responsáveis e contato
            response.put("nomeResponsavel", user.getNomeResponsavel());
            response.put("whatsappResponsavel", user.getWhatsappResponsavel());
            response.put("telefoneContato", user.getTelefoneContato());
            response.put("menorDeIdade", user.isMenorDeIdade());
            // Novos campos adicionais
            response.put("dataInicioPratica", user.getDataInicioPratica());
            response.put("dataUltimaGraduacao", user.getDataUltimaGraduacao());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/findByFaixa")
    public ResponseEntity<List<User>> findByFaixa(@RequestParam Faixa faixa) {
        return ResponseEntity.ok(userService.findByFaixa(faixa));
    }

    @GetMapping("/findByTurma/{turmaId}")
    public ResponseEntity<List<User>> findByTurma(@PathVariable Long turmaId) {
        return ResponseEntity.ok(userService.findByTurmaId(turmaId));
    }

    @GetMapping("/findByNome")
    public ResponseEntity<List<User>> findByNome(@RequestParam String nome) {
        return ResponseEntity.ok(userService.findByNome(nome));
    }

    @GetMapping("/aptos-graduacao")
    public ResponseEntity<List<User>> findAptosParaGraduacao() {
        return ResponseEntity.ok(userService.findAptosParaGraduacao());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> dados) {
        try {
            User user = userService.findById(id);
            if (dados.containsKey("nome")) user.setNome((String) dados.get("nome"));
            if (dados.containsKey("idade")) user.setIdade((Integer) dados.get("idade"));
            if (dados.containsKey("grau")) {
                Integer grau = dados.get("grau") instanceof Integer
                    ? (Integer) dados.get("grau")
                    : Integer.parseInt(dados.get("grau").toString());
                user.setGrau(grau);
            }
            if (dados.containsKey("faixa")) {
                String faixaStr = (String) dados.get("faixa");
                user.setFaixa(Faixa.valueOf(faixaStr.toUpperCase()));
            }
            // Novos campos para responsáveis e contato
            if (dados.containsKey("nomeResponsavel")) user.setNomeResponsavel((String) dados.get("nomeResponsavel"));
            if (dados.containsKey("whatsappResponsavel")) user.setWhatsappResponsavel((String) dados.get("whatsappResponsavel"));
            if (dados.containsKey("telefoneContato")) user.setTelefoneContato((String) dados.get("telefoneContato"));
            // Novos campos adicionais
            if (dados.containsKey("dataInicioPratica")) user.setDataInicioPratica((String) dados.get("dataInicioPratica"));
            if (dados.containsKey("dataUltimaGraduacao")) user.setDataUltimaGraduacao((String) dados.get("dataUltimaGraduacao"));
            // Atualizar turmas se turmasIds for fornecido
            if (dados.containsKey("turmasIds")) {
                @SuppressWarnings("unchecked")
                List<?> rawList = (List<?>) dados.get("turmasIds");
                Set<Long> turmasIds = rawList.stream()
                    .map(obj -> ((Number) obj).longValue())
                    .collect(Collectors.toSet());
                userService.updateTurmas(id, turmasIds);
            }
            return ResponseEntity.ok(userService.update(id, user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/turmas/{id}")
    public ResponseEntity<?> updateTurmas(@PathVariable Long id, @RequestBody Set<Long> turmasIds) {
        try {
            userService.updateTurmas(id, turmasIds);
            return ResponseEntity.ok("Turmas atualizadas com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/status/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR') or @userSecurity.isSelf(#id)")
    public ResponseEntity<?> getStatus(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getStatus(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/progressao/{id}/percentual")
    public ResponseEntity<?> getPercentualProgressao(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getPercentualProgressao(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/conceder-grau/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<?> concederGrau(@PathVariable Long id) {
        try {
            User userAntes = userService.findById(id);
            int grauAntes = userAntes.getGrau();

            User user = userService.concederGrau(id);
            int grauAtual = user.getGrau();

            String mensagem;
            if (grauAtual > grauAntes) {
                mensagem = "Grau concedido com sucesso";
            } else {
                mensagem = "Aluno já está no grau máximo (4). Nenhum grau concedido.";
            }

            return ResponseEntity.ok(Map.of(
                "mensagem", mensagem,
                "grauAtual", user.getGrau(),
                "grauAnterior", grauAntes,
                "faixa", user.getFaixa().name(),
                "aulasDesdeUltimaGraduacao", user.getAulasDesdeUltimaGraduacao(),
                "aulasAcumuladas", user.getAulasAcumuladas(),
                "ultimaGraduacao", user.getUltimaGraduacao() != null ? user.getUltimaGraduacao().toString() : null
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/trocar-faixa/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<?> trocarFaixa(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String novaFaixa = request.get("novaFaixa");
            if (novaFaixa == null) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Campo 'novaFaixa' é obrigatório"));
            }

            // Guardar faixa anterior antes de trocar
            User userAntes = userService.findById(id);
            String faixaAnterior = userAntes.getFaixa().name();

            User user = userService.trocarFaixa(id, novaFaixa);
            return ResponseEntity.ok(Map.of(
                "mensagem", "Faixa trocada com sucesso",
                "faixa", user.getFaixa().name(),
                "faixaAnterior", faixaAnterior,
                "grau", user.getGrau(),
                "aulasDesdeUltimaGraduacao", user.getAulasDesdeUltimaGraduacao(),
                "aulasAcumuladas", user.getAulasAcumuladas(),
                "ultimaGraduacao", user.getUltimaGraduacao() != null ? user.getUltimaGraduacao().toString() : null
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // =============================================
    // ENDPOINTS DE CRITÉRIOS DE GRADUAÇÃO
    // =============================================

    @GetMapping("/graduacao/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'ALUNO')")
    public ResponseEntity<?> getCriteriosGraduacao(
            @PathVariable Long id,
            @RequestParam(required = false) String faixa) {
        try {
            User user = userService.findById(id);

            // Usar método que retorna requisitos da PRÓXIMA faixa considerando idade
            List<String> requisitos;
            Faixa faixaRequisitos;
            if (faixa != null) {
                faixaRequisitos = Faixa.valueOf(faixa.toUpperCase());
                requisitos = requisitosService.getRequisitosPorFaixa(faixaRequisitos);
            } else {
                faixaRequisitos = requisitosService.getProximaFaixa(user.getFaixa(), user.getIdade());
                requisitos = requisitosService.getRequisitosParaProximaFaixa(user.getFaixa(), user.getIdade());
            }

            Set<Integer> concluidos = user.getCriteriosConcluidos() != null
                ? user.getCriteriosConcluidos()
                : new java.util.HashSet<>();

            boolean[] criteriosMarcados = new boolean[requisitos.size()];
            int totalConcluidos = 0;
            for (int i = 0; i < requisitos.size(); i++) {
                criteriosMarcados[i] = concluidos.contains(i);
                if (criteriosMarcados[i]) totalConcluidos++;
            }

            boolean prontoParaProximaFaixa = requisitosService.isProntoParaProximaFaixa(
                user.getFaixa(),
                user.getGrau(),
                totalConcluidos,
                requisitos.size(),
                user.getIdade()
            );

            Integer idadeMinimaProximaFaixa = faixaRequisitos != null
                ? requisitosService.getIdadeMinima(faixaRequisitos) : null;

            Map<String, Object> response = new java.util.HashMap<>();
            response.put("requisitos", requisitos);
            response.put("criteriosMarcados", criteriosMarcados);
            response.put("faixaAtual", user.getFaixa().name());
            response.put("faixaRequisitos", faixaRequisitos != null ? faixaRequisitos.name() : null);
            response.put("prontoParaProximaFaixa", prontoParaProximaFaixa);
            response.put("idadeMinimaProximaFaixa", idadeMinimaProximaFaixa);
            response.put("grauAtual", user.getGrau());
            response.put("totalCriterios", requisitos.size());
            response.put("totalConcluidos", totalConcluidos);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/{faixa}/requisitos/{id}")
    public ResponseEntity<?> getRequisitosPorFaixa(
            @PathVariable String faixa,
            @PathVariable Long id) {
        try {
            // Verificar se o usuário existe
            userService.findById(id);

            List<String> requisitos = requisitosService.getRequisitosPorFaixa(faixa);
            if (requisitos.isEmpty()) {
                return ResponseEntity.ok(List.of());
            }
            return ResponseEntity.ok(requisitos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/graduacao/{id}")
    public ResponseEntity<?> atualizarCriterios(
            @PathVariable Long id,
            @RequestParam(required = false) String faixa,
            @RequestBody boolean[] criteriosMarcados) {
        try {
            // Converter array de boolean para Set de índices
            Set<Integer> novosIndices = new java.util.HashSet<>();
            for (int i = 0; i < criteriosMarcados.length; i++) {
                if (criteriosMarcados[i]) {
                    novosIndices.add(i);
                }
            }

            // Usar método específico que salva os critérios
            User user = userService.updateCriterios(id, novosIndices);

            return ResponseEntity.ok(Map.of(
                "mensagem", "Critérios atualizados com sucesso",
                "totalConcluidos", novosIndices.size(),
                "criteriosConcluidos", novosIndices
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/marcar-criterios/{id}")
    public ResponseEntity<?> marcarCriterios(
            @PathVariable Long id,
            @RequestBody boolean[] criteriosMarcados) {
        return atualizarCriterios(id, null, criteriosMarcados);
    }

    @GetMapping("/historico/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'ALUNO')")
    public ResponseEntity<?> getHistorico(@PathVariable Long id) {
        try {
            List<UserHistorico> historico = userService.getHistorico(id);
            return ResponseEntity.ok(historico.stream().map(h -> Map.of(
                "id", h.getId(),
                "userId", h.getUser().getId(),
                "descricao", h.getDescricao(),
                "dataHoraAlteracao", h.getDataHora().toString(), // ISO 8601: 2026-01-04T15:30:00
                "dataHora", h.getDataHora().toString(),
                "tipo", h.getTipoAlteracao().name()
            )).toList());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/estatisticas/faixas")
    public ResponseEntity<?> getEstatisticasFaixas() {
        return ResponseEntity.ok(userService.getEstatisticasFaixas());
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.ok("Usuário deletado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/campos-cadastro")
    public ResponseEntity<?> getCamposCadastro(@RequestParam Integer idade) {
        boolean menorDeIdade = idade != null && idade < 18;
        Map<String, Object> campos = new java.util.HashMap<>();
        campos.put("menorDeIdade", menorDeIdade);
        campos.put("camposObrigatorios", menorDeIdade ?
            Arrays.asList("nome", "dataNascimento", "nomeResponsavel", "whatsappResponsavel", "dataInicioPratica") :
            Arrays.asList("nome", "dataNascimento", "telefoneContato", "dataInicioPratica"));
        campos.put("camposOpcionais", menorDeIdade ?
            Arrays.asList("telefoneContato", "dataUltimaGraduacao") :
            Arrays.asList("nomeResponsavel", "whatsappResponsavel", "dataUltimaGraduacao"));
        return ResponseEntity.ok(campos);
    }

    private String generateUsername(String nome) {
        String baseUsername = nome.toLowerCase().replace(" ", ".");
        String username = baseUsername;
        int counter = 1;
        while (userService.findByUsername(username).isPresent()) {
            username = baseUsername + counter;
            counter++;
        }
        return username;
    }

    private String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @GetMapping("/credenciais/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<?> getCredenciais(@PathVariable Long userId) {
        try {
            bjjapp.entity.UserPlainPassword credenciais = userService.getCredenciais(userId);
            if (credenciais == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Credenciais não encontradas para o usuário: " + userId);
            }
            return ResponseEntity.ok(Map.of(
                "username", credenciais.getUsername(),
                "password", credenciais.getPlainPassword()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao buscar credenciais: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'ALUNO')")
    public ResponseEntity<?> getMyProfile(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
        }
        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("id", user.getId());
        response.put("nome", user.getNome());
        response.put("idade", user.getIdade());
        response.put("faixa", user.getFaixa().name());
        response.put("grau", user.getGrau());
        response.put("dataNascimento", user.getDataNascimento() != null ? user.getDataNascimento().toString() : null);
        response.put("turmasIds", user.getTurmas() != null ? user.getTurmas().stream().map(Turma::getId).toList() : java.util.Collections.emptyList());
        response.put("aulasAcumuladas", user.getAulasAcumuladas());
        response.put("aulasDesdeUltimaGraduacao", user.getAulasDesdeUltimaGraduacao());
        response.put("ultimaGraduacao", user.getUltimaGraduacao() != null ? user.getUltimaGraduacao().toString() : null);
        response.put("criteriosConcluidos", user.getCriteriosConcluidos() != null ? user.getCriteriosConcluidos() : new java.util.HashSet<>());
        response.put("nomeResponsavel", user.getNomeResponsavel());
        response.put("whatsappResponsavel", user.getWhatsappResponsavel());
        response.put("telefoneContato", user.getTelefoneContato());
        response.put("menorDeIdade", user.isMenorDeIdade());
        response.put("dataInicioPratica", user.getDataInicioPratica());
        response.put("dataUltimaGraduacao", user.getDataUltimaGraduacao());
        return ResponseEntity.ok(response);
    }
}
