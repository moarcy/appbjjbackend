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
}
