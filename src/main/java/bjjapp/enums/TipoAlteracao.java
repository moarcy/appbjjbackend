package bjjapp.enums;

/**
 * Tipos de alteração para histórico
 */
public enum TipoAlteracao {
    CADASTRO("Cadastro"),
    ATUALIZACAO("Atualização de dados"),
    PRESENCA("Presença registrada"),
    GRAU("Alteração de grau"),
    FAIXA("Troca de faixa"),
    TURMA("Alteração de turma");

    private final String descricao;

    TipoAlteracao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

