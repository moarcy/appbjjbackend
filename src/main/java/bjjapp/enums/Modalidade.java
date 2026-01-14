package bjjapp.enums;

/**
 * Modalidades de treino
 */
public enum Modalidade {
    GI("Gi (Kimono)"),
    NO_GI("No-Gi (Sem Kimono)"),
    KIDS("Jiu-Jitsu Kids"),
    COMPETICAO("Competição");

    private final String descricao;

    Modalidade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static Modalidade fromDescricao(String descricao) {
        for (Modalidade m : values()) {
            if (m.descricao.equals(descricao)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Modalidade desconhecida: " + descricao);
    }
}
