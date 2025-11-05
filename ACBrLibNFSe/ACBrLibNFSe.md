# ACBrLibNFSe 2.0.1.204

Biblioteca para emissão e impressão de Nota Fiscal de Serviços Eletrônica (NFSe).

- **Manual On-Line**: [https://acbr.sourceforge.io/ACBrLib/ACBrLib.html](https://acbr.sourceforge.io/ACBrLib/ACBrLib.html)
- **Download Demos de Uso em diversas linguagens pelo SVN**: [http://svn.code.sf.net/p/acbr/code/trunk2/Projetos/ACBrLib/Demos/](http://svn.code.sf.net/p/acbr/code/trunk2/Projetos/ACBrLib/Demos/)

## Demo em JAVA

- [https://svn.code.sf.net/p/acbr/code/trunk2/Projetos/ACBrLib/Demos/Java/NFSe/](https://svn.code.sf.net/p/acbr/code/trunk2/Projetos/ACBrLib/Demos/Java/NFSe/)

Essa é uma estrutura de projeto típica de bibliotecas que precisam de um "invólucro" (wrapper) para funcionar em uma linguagem diferente.

### 📂 Explicação das Pastas `Demo` e `Imports` (Pura Java)

O projeto usa a biblioteca [**JNA (Java Native Access)**](https://github.com/java-native-access/jna) para chamar a DLL nativa do Windows.
A divisão das pastas é para separar o **código de ponte JNA** do **código de aplicação de exemplo**.

| Pasta | Conteúdo Principal | Função no Projeto |
| :--- | :--- | :--- |
| **Demo** | Código da **Aplicação de Exemplo** (`ACBrLibNFSeDemo.java`, etc.). | É o programa pronto para rodar. Contém a lógica de interface (o "Formulário"), a sequência de comandos e as regras de negócio de **alto nível**. Este código **chama** os métodos definidos na pasta `Imports`. |
| **Imports** | Classes Java do **Wrapper JNA** (`ACBrNFSeWrapper.java`, `ACBrNFSeLib.java`, etc.). | É o **Adaptador (Ponte) JNA**. Este código é estritamente técnico e tem a responsabilidade de: **1.** Definir as assinaturas dos métodos da DLL nativa. **2.** Configurar e carregar a `ACBrLibNFSe64.dll` na memória (onde o seu erro de `UnsatisfiedLinkError` ocorre). **3.** Fazer a **tradução** (marshalling) de tipos de dados Java para os tipos de dados nativos (DLL) e vice-versa. |

### RESUMO

- **`Imports`** é o código que **fala com o Windows (a DLL nativa)**. É o código mais crítico onde a JNA tenta estabelecer a conexão.
- **`Demo`** é o código que **fala com o usuário** e utiliza os métodos já estabelecidos pelo código `Imports`.

O fato de você estar executando um projeto que usa esse wrapper JNA confirma que a `ACBrNFSeLib.java` (na pasta `Imports`) é o ponto exato onde o Java tenta carregar a DLL e falha por causa da falta de dependências.

> **IMPORTANTE**: A biblioteca (DLLs) devem estar na pasta `win32-x86-64`.
