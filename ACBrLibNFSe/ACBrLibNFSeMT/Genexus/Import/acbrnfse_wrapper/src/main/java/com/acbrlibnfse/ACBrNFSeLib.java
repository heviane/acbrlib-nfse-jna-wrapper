package com.acbrlibnfse;

import com.sun.jna.Library;
import com.sun.jna.Native;

// A interface que mapeia a DLL
public interface ACBrNFSeLib extends Library {

    // Instância estática para carregar a DLL
    // O nome deve ser EXATAMENTE o nome do arquivo da DLL SEM a extensão (.dll/.so)
    ACBrNFSeLib INSTANCE = Native.load("ACBrLibNFSe64", ACBrNFSeLib.class);

    // Funções de API da ACBrLib:

    // 1. Inicializar (Retorna o Handle (hLib) ou código de erro)
    // O Genexus estava com problemas aqui porque o retorno não é String, e sim um Integer/Pointer.
    int NFSE_Inicializar(
        String eArqConfig,  // Caminho do arquivo INI
        String eChaveCrypt // Chave de criptografia
    );

    // 2. ConfigGravar (Retorna o código de erro. Requer o Handle hLib)
    int NFSE_ConfigGravar(
        int hLib,           // Handle (hLib) da instância
        String eSessao,     // Ex: "DFe", "NFSe"
        String eChave,      // Ex: "CaminhoCertificado"
        String eValor       // O valor a ser gravado
    );

    // 3. Finalizar (Retorna o código de erro)
    int NFSE_Finalizar(int hLib);
}
