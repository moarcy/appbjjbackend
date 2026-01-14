package bjjapp.enums;

/**
 * Roles de usuário no sistema
 */
public enum Role {
    SUPER_ADMIN("Super Administrador"),
    SCHOOL_ADMIN("Administrador da Escola"),
    TEACHER("Professor"),
    STUDENT("Aluno");

    private final String descricao;

    Role(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
