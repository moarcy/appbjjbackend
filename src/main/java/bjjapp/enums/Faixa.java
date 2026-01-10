package bjjapp.enums;

/**
 * Faixas do Jiu-Jitsu Brasileiro
 */
public enum Faixa {
    // Faixas Adulto
    BRANCA("Branca", "#FFFFFF"),
    AZUL("Azul", "#0066CC"),
    ROXA("Roxa", "#800080"),
    MARROM("Marrom", "#8B4513"),
    PRETA("Preta", "#000000"),
    
    // Faixas Infantil
    BRANCA_CINZA("Branca/Cinza", "#E0E0E0"),
    CINZA("Cinza", "#808080"),
    AMARELA("Amarela", "#FFD700"),
    LARANJA("Laranja", "#FFA500"),
    VERDE("Verde", "#228B22");
    
    private final String nome;
    private final String cor;
    
    Faixa(String nome, String cor) {
        this.nome = nome;
        this.cor = cor;
    }
    
    public String getNome() {
        return nome;
    }
    
    public String getCor() {
        return cor;
    }
}

