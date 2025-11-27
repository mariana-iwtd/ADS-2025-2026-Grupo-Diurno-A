package Grupo_Diurno_A;

/**
 * A classe {@code AtributeInfo} representa a informacao basica de um atributo
 * de uma classe, incluindo seu nome e tipo.
 *
 * <p>Utilizada para armazenar detalhes dos atributos durante a analise
 * de metricas de codigo fonte.</p>
 *
 */
public class AtributeInfo {
    /**
     * Nome do atributo.
     */
    private String name;

    /**
     * Tipo do atributo.
     */
    private String type;

    //construtor

    /**
     * Construtor da classe AtributeInfo.
     * @param name nome do atributo
     * @param type tipo do atributo
     */
    public AtributeInfo(String name, String type) {
        this.name = name;
        this.type = type;
    }

    //getters
    /**
     * Retorna o nome do atributo.
     * @return nome do atributo
     */
    public String getName() {
        return name;
    }

    /**
     * Retorna o tipo do atributo.
     * @return tipo do atributo
     */
    public String getType() {
        return type;
    }

}
