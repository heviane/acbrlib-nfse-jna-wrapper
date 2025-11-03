# 🛠️ Guia Passo a Passo: Projeto JNA Bridge no VS Code

**Pré-requisitos:**

1. VS Code instalado.
2. Java Development Kit (JDK 11 ou superior) instalado.
3. Extensões **"Extension Pack for Java"** e **"Maven for Java"** instaladas no VS Code.

## Fase I: Configuração do Projeto no VS Code

### 1. Clonar o Repositório

1. No VS Code, abra a **Paleta de Comandos** (`Ctrl+Shift+P` ou `Cmd+Shift+P`).
2. Digite: `Git: Clone`.
3. Cole a URL do seu novo repositório GitHub.
4. Selecione a pasta local onde deseja clonar o projeto.
5. Quando perguntado, selecione **"Abrir Repositório"** (Open Repository).

### 2. Criar a Estrutura Maven

Como o repositório está vazio, vamos criar a estrutura do projeto Maven nele:

1. **Abra a Paleta de Comandos** (`Ctrl+Shift+P`).
2. Digite: `Java: Create Java Project`.
3. Selecione: **`Maven`**.
4. Selecione o arquétipo (template): **`maven-archetype-quickstart`** (o mais simples).
5. **Versão:** Escolha a versão mais recente.
6. **Defina o *Group ID* (ID do Grupo):** Digite `com.seuprojeto.acbr` (substitua `seuprojeto`).
7. **Defina o *Artifact ID* (ID do Artefato/Projeto):** Digite `acbrnfse_wrapper`.
8. **Confirme o Caminho:** O VS Code criará a estrutura dentro do seu repositório clonado.

### Fase II: Configuração do Maven (pom.xml)

Agora você precisa informar ao Maven que o projeto depende da JNA.

- No VS Code Explorer, abra o arquivo **`pom.xml`**.
- Localize a tag `<dependencies>...</dependencies>`.
- **Adicione as dependências JNA** dentro dessa tag:

```xml
<dependencies>
    <dependency>
        <groupId>net.java.dev.jna</groupId>
        <artifactId>jna</artifactId>
        <version>5.14.0</version>
    </dependency>
    <dependency>
        <groupId>net.java.dev.jna</groupId>
        <artifactId>jna-platform</artifactId>
        <version>5.14.0</version>
    </dependency>
    
    </dependencies>
```

> **Nota:** Use `5.14.0` ou verifique a versão estável mais recente.

- **Salve o `pom.xml`**. O VS Code/Maven deve baixar automaticamente essas bibliotecas.

### Fase III: Implementação da Interface JNA

Agora vamos mapear as funções da `ACBrLibNFSe64.dll` para o Java.

1. No Explorer, navegue até `src/main/java/com/seuprojeto/acbr`.

2. Renomeie ou exclua o arquivo `App.java` que o Maven criou.

3. Crie um novo arquivo chamado **`ACBrNFSeLib.java`**.

4. Cole o seguinte código (que define o mapeamento):

```java
package com.seuprojeto.acbr; // Use o nome do seu pacote

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
```

### Fase IV: Implementação da Classe Wrapper (Consumo no Genexus)

Crie a classe de Alto Nível que o Genexus consumirá.

1. Crie um novo arquivo chamado **`ACBrNFSeWrapper.java`**.

2. Cole o seguinte código:

```java
package com.seuprojeto.acbr; // Use o nome do seu pacote

// Esta é a classe que o Genexus irá referenciar (o External Object)
public class ACBrNFSeWrapper {

    private int hLib = 0; // Armazena o Handle (hLib) da instância

    // Construtor: Chamado por 'New ACBrNFSeWrapper.New(...)' no Genexus
    public ACBrNFSeWrapper(String arqConfig, String chaveCrypt) throws RuntimeException {
        // Chama a função nativa via JNA
        this.hLib = ACBrNFSeLib.INSTANCE.NFSE_Inicializar(arqConfig, chaveCrypt);

        if (this.hLib <= 0) {
            // Lança exceção para avisar ao Genexus em caso de falha
            throw new RuntimeException("Erro ao inicializar ACBrLibNFSe. Código: " + this.hLib);
        }
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
```

### Fase V: Compilação

- **Abra o Terminal** integrado do VS Code (` Ctrl+`` ou `Cmd+\`\`).

- **Execute o Maven para gerar o JAR:**

O comando deve ser executado dentro do diretório do módulo Maven (`acbrnfse_wrapper`), onde o arquivo `pom.xml` está localizado.

```bash
cd acbrnfse_wrapper
mvn clean package
```

- **Resultado:** Se a compilação for bem-sucedida (Status `BUILD SUCCESS`), o arquivo **`acbrnfse_wrapper-1.0.jar`** estará dentro da pasta **`target`** do seu projeto.

Este JAR é o artefato que você usará no Genexus, e ele já contém a JNA e o código para falar com a DLL.

> **PRONTO**: Agora que o projeto Java está pronto, o próximo passo seria mover os artefatos (JAR e DLL) para o Genexus.
