package bjjapp.enums;

/**
 * Modalidades de treino
 */
public enum Modalidade {
    GI("Gi (Kimono)"),
    NO_GI("No-Gi (Sem Kimono)"),
    JIU_JITSU_KIDS("Jiu-Jitsu Kids"),
    JIU_JITSU_COMPETICAO("Competição");

    private final String descricao;

    Modalidade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
