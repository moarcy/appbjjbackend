package bjjapp.service;

import bjjapp.enums.Faixa;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Serviço para gerenciar requisitos de graduação por faixa
 */
@Service
public class RequisitosGraduacaoService {

    // Requisitos técnicos por faixa
    private static final Map<Faixa, List<String>> REQUISITOS_POR_FAIXA = new HashMap<>();

    // Idade mínima por faixa
    private static final Map<Faixa, Integer> IDADE_MINIMA_POR_FAIXA = new HashMap<>();

    static {
        // =============================================
        // FAIXAS INFANTIS
        // =============================================

        // Faixa Cinza (IDADE MÍNIMA: 7 ANOS)
        REQUISITOS_POR_FAIXA.put(Faixa.CINZA, Arrays.asList(
            "1 Amortecimento",
            "1 Rolamento",
            "1 Estabilização",
            "1 Queda",
            "1 Passagem",
            "1 Raspagem",
            "1 Finalização da guarda",
            "1 Finalização da montada",
            "Participar de pelo menos 1 campeonato"
        ));
        IDADE_MINIMA_POR_FAIXA.put(Faixa.CINZA, 7);

        // Faixa Amarela
        REQUISITOS_POR_FAIXA.put(Faixa.AMARELA, Arrays.asList(
            "Transição das guardas",
            "3 Quedas",
            "3 Raspagens",
            "3 Passagens",
            "3 Finalizações da guarda",
            "2 Finalizações da montada",
            "1 Finalização das costas",
            "Participar de pelo menos 1 campeonato"
        ));

        // Faixa Laranja (IDADE MÍNIMA: 10 ANOS)
        REQUISITOS_POR_FAIXA.put(Faixa.LARANJA, Arrays.asList(
            "5 Quedas",
            "5 Raspagens (1 meia guarda, 1 aranha, 3 livres)",
            "5 Passagens (1 meia guarda, 1 aranha, 3 livres)",
            "4 Finalizações da guarda",
            "2 Finalizações da montada",
            "1 Finalização das costas",
            "1 Finalização dos 100kg",
            "Noções de regra (pontuação)",
            "Participar de pelo menos 2 campeonatos"
        ));
        IDADE_MINIMA_POR_FAIXA.put(Faixa.LARANJA, 10);

        // Faixa Verde
        REQUISITOS_POR_FAIXA.put(Faixa.VERDE, Arrays.asList(
            "5 Quedas",
            "5 Raspagens (1 meia guarda, 1 aranha, 1 de la riva, 1 laço)",
            "5 Passagens (1 meia guarda, 1 aranha, 1 de la riva, 1 laço)",
            "4 Finalizações da guarda",
            "2 Finalizações da montada",
            "2 Finalizações das costas",
            "2 Finalizações dos 100kg",
            "Noções de regra (pontuação e golpes proibidos)",
            "Fazer defesa de golpe",
            "Fazer pelo menos 1 drill",
            "Participar de pelo menos 2 campeonatos"
        ));

        // =============================================
        // FAIXAS ADULTO
        // =============================================

        // Faixa Branca
        REQUISITOS_POR_FAIXA.put(Faixa.BRANCA, Arrays.asList(
            "Postura base e movimentação básica",
            "Queda segura (rolamentos)",
            "Guarda fechada básica",
            "Passagem de guarda básica",
            "Montada e saídas",
            "Raspagem tesoura",
            "Armlock da guarda",
            "Triângulo básico",
            "Kimura",
            "Americana"
        ));

        // Faixa Azul
        REQUISITOS_POR_FAIXA.put(Faixa.AZUL, Arrays.asList(
            "Guarda aberta e variações",
            "Passagens de guarda variadas",
            "Controle lateral (side control)",
            "North-south position",
            "Raspagens de meia-guarda",
            "Ataques das costas",
            "Defesas de finalizações",
            "Transições fluidas",
            "Estrangulamentos básicos",
            "Leg locks básicos (reta de pé)"
        ));

        // Faixa Roxa
        REQUISITOS_POR_FAIXA.put(Faixa.ROXA, Arrays.asList(
            "Guarda de la Riva",
            "Guarda aranha",
            "Berimbolo básico",
            "Leg drag",
            "Knee slice pass",
            "Ataques da montada",
            "Back takes avançados",
            "Chokes variados",
            "Leg locks intermediários",
            "Defesas avançadas"
        ));

        // Faixa Marrom
        REQUISITOS_POR_FAIXA.put(Faixa.MARROM, Arrays.asList(
            "Jogo completo em pé",
            "Guard retention avançada",
            "Pressure passing",
            "Wrestling integrado",
            "Leg locks avançados",
            "Worm guard / Lapel guards",
            "Inversões",
            "Controle de ritmo",
            "Estratégia de luta",
            "Ensino de técnicas básicas"
        ));

        // Faixa Preta
        REQUISITOS_POR_FAIXA.put(Faixa.PRETA, Arrays.asList(
            "Domínio completo de todas as posições",
            "Estilo próprio desenvolvido",
            "Capacidade de adaptar jogo ao oponente",
            "Liderança e exemplo",
            "Contribuição para a arte",
            "Ensino avançado",
            "Competição em alto nível",
            "Conhecimento histórico do BJJ",
            "Filosofia marcial",
            "Desenvolvimento contínuo"
        ));

        // Faixa Branca/Cinza (transição) usa requisitos da Cinza
        REQUISITOS_POR_FAIXA.put(Faixa.BRANCA_CINZA, REQUISITOS_POR_FAIXA.get(Faixa.CINZA));
    }

    // Mapa de progressão de faixas infantis
    private static final Map<Faixa, Faixa> PROGRESSAO_FAIXA_INFANTIL = Map.of(
        Faixa.BRANCA, Faixa.CINZA,
        Faixa.CINZA, Faixa.AMARELA,
        Faixa.AMARELA, Faixa.LARANJA,
        Faixa.LARANJA, Faixa.VERDE
    );

    // Mapa de progressão de faixas adulto
    private static final Map<Faixa, Faixa> PROGRESSAO_FAIXA_ADULTO = Map.of(
        Faixa.BRANCA, Faixa.AZUL,
        Faixa.AZUL, Faixa.ROXA,
        Faixa.ROXA, Faixa.MARROM,
        Faixa.MARROM, Faixa.PRETA
    );

    public List<String> getRequisitosPorFaixa(Faixa faixa) {
        return REQUISITOS_POR_FAIXA.getOrDefault(faixa, Collections.emptyList());
    }

    public List<String> getRequisitosPorFaixa(String faixaStr) {
        try {
            Faixa faixa = Faixa.valueOf(faixaStr.toUpperCase());
            return getRequisitosPorFaixa(faixa);
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }
    }

    public Map<Faixa, List<String>> getTodosRequisitos() {
        return Collections.unmodifiableMap(REQUISITOS_POR_FAIXA);
    }

    public Integer getIdadeMinima(Faixa faixa) {
        return IDADE_MINIMA_POR_FAIXA.get(faixa);
    }

    public Faixa getProximaFaixaInfantil(Faixa faixaAtual) {
        return PROGRESSAO_FAIXA_INFANTIL.get(faixaAtual);
    }

    public Faixa getProximaFaixaAdulto(Faixa faixaAtual) {
        return PROGRESSAO_FAIXA_ADULTO.get(faixaAtual);
    }

    /**
     * Verifica se o aluno é infantil baseado na idade
     * Alunos com menos de 15 anos são considerados infantis
     */
    public boolean isInfantil(Integer idade) {
        return idade != null && idade < 15;
    }

    /**
     * Verifica se a faixa é uma faixa infantil
     */
    public boolean isFaixaInfantil(Faixa faixa) {
        return faixa == Faixa.BRANCA_CINZA || faixa == Faixa.CINZA ||
               faixa == Faixa.AMARELA || faixa == Faixa.LARANJA || faixa == Faixa.VERDE;
    }

    /**
     * Obtém a próxima faixa baseado na IDADE do aluno (não na faixa atual)
     * Se idade < 15 anos: usa progressão infantil
     * Se idade >= 15 anos: usa progressão adulto
     */
    public Faixa getProximaFaixa(Faixa faixaAtual, Integer idade) {
        if (isInfantil(idade)) {
            // Aluno infantil - usa progressão infantil
            return PROGRESSAO_FAIXA_INFANTIL.get(faixaAtual);
        } else {
            // Aluno adulto - usa progressão adulto
            return PROGRESSAO_FAIXA_ADULTO.get(faixaAtual);
        }
    }

    /**
     * Verifica se o aluno está pronto para a próxima faixa
     * @param faixaAtual Faixa atual do aluno
     * @param grauAtual Grau atual (0-4)
     * @param criteriosConcluidos Quantidade de critérios concluídos
     * @param totalCriterios Total de critérios da faixa
     * @param idade Idade do aluno (pode ser null)
     * @return true se está pronto para próxima faixa
     */
    public boolean isProntoParaProximaFaixa(Faixa faixaAtual, int grauAtual,
            int criteriosConcluidos, int totalCriterios, Integer idade) {

        // Precisa ter 4 graus
        if (grauAtual < 4) {
            return false;
        }

        // Precisa ter completado todos os critérios
        if (criteriosConcluidos < totalCriterios) {
            return false;
        }

        // Verificar idade mínima para próxima faixa (se aplicável)
        Faixa proximaFaixa = getProximaFaixaInfantil(faixaAtual);
        if (proximaFaixa != null && idade != null) {
            Integer idadeMinima = getIdadeMinima(proximaFaixa);
            if (idadeMinima != null && idade < idadeMinima) {
                return false;
            }
        }

        return true;
    }

    /**
     * Retorna os requisitos para a PRÓXIMA faixa do aluno, considerando idade
     * Se infantil, retorna requisitos da próxima faixa infantil
     * Se adulto, retorna requisitos da próxima faixa adulta
     */
    public List<String> getRequisitosParaProximaFaixa(Faixa faixaAtual, Integer idade) {
        Faixa proximaFaixa = getProximaFaixa(faixaAtual, idade);
        if (proximaFaixa != null) {
            return getRequisitosPorFaixa(proximaFaixa);
        }
        return Collections.emptyList();
    }
}
