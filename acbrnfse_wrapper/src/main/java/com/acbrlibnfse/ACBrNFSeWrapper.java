package com.acbrlibnfse;

// Esta é a classe que o Genexus irá referenciar (o External Object)
public class ACBrNFSeWrapper {

    private int hLib = 0; // Armazena o Handle (hLib) da instância

    public ACBrNFSeWrapper(){
        // Construtor vazio para permitir que o Genexus instancie o objeto primeiro.
    }

    // ACBrNFSeWrapper.java (Adicionar/Substituir o construtor antigo):
    public int inicializar(String arqConfig, String chaveCrypt) throws RuntimeException {
        // ... (Mesma lógica de antes)
        this.hLib = ACBrNFSeLib.INSTANCE.NFSE_Inicializar(arqConfig, chaveCrypt);

        if (this.hLib <= 0) {
            return this.hLib; // Retorna o código de erro em vez de lançar exceção
        }
        return 0; // Sucesso
    }

    // Método que mapeia NFSE_ConfigGravar
    public int salvarConfiguracao(String sessao, String chave, String valor) {
        if (this.hLib <= 0) {
            // Retorna erro se não estiver inicializado
            return -99;
        }

        // Chama a função nativa
        return ACBrNFSeLib.INSTANCE.NFSE_ConfigGravar(this.hLib, sessao, chave, valor);
    }

    // Método que mapeia NFSE_Finalizar
    public int finalizar() {
        if (this.hLib <= 0) return 0;

        int result = ACBrNFSeLib.INSTANCE.NFSE_Finalizar(this.hLib);
        this.hLib = 0;
        return result;
    }
}