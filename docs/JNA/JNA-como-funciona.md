# JNA (Java Native Access)

## O que é o JNA?

O JNA (Java Native Access) é uma biblioteca que permite que o código Java interaja com bibliotecas nativas (escritas em C/C++) sem a necessidade de escrever código JNI (Java Native Interface) complexo. Ele fornece uma maneira simples de chamar funções nativas diretamente do Java, facilitando a integração com bibliotecas externas.

## Como Funciona neste Projeto?

JNA é a biblioteca que faz a interface entre o Java e a DLL nativa.

Como a JNA realiza o mapeamento da DLL neste projeto.
O "coração" do mapeamento está no arquivo `ACBrNFSeLib.java`.

Vamos analisar como ele funciona:

### 1. Como a JNA Mapeia a DLL?

A JNA (Java Native Access) utiliza uma abordagem de proxy dinâmico para se conectar à DLL.

Isso acontece em duas etapas principais no seu código:

- **Etapa 1**: A Interface de Mapeamento:

Primeiro, você define uma interface Java que espelha as funções que deseja usar da DLL. No seu projeto, é a `ACBrNFSeLib`:

```java
// file: ACBrNFSeLib.java
public interface ACBrNFSeLib extends Library {
    // ...
}
```

Ao estender `com.sun.jna.Library`, você está dizendo à JNA: "Esta interface não é uma classe comum; ela é um blueprint para uma biblioteca nativa".

- **Etapa 2**: O Carregamento da Biblioteca:

A "mágica" acontece nesta linha dentro da interface:

```java
// file: ACBrNFSeLib.java
ACBrNFSeLib INSTANCE = Native.load("ACBrLibNFSe64", ACBrNFSeLib.class);
```

O método `Native.load()` faz o seguinte, Procura e carrega a biblioteca nativa especificada.

Ele cria um objeto em tempo de execução que implementa a sua interface `ACBrNFSeLib`.
Quando você chama um método nesse objeto (ex: `INSTANCE.NFSE_Inicializar(...)`), a JNA intercepta a chamada, converte os parâmetros Java (String, int) para seus equivalentes nativos e invoca a função com o mesmo nome (NFSE_Inicializar) dentro da DLL carregada.

Ela então pega o valor de retorno da função da DLL, converte-o de volta para um tipo Java e o retorna para o seu código.

### 2. Por Qual Nome a JNA Busca o Arquivo?

A JNA busca pelo nome que você passa como primeiro argumento para o método `Native.load()`.

No seu caso, o nome é `ACBrLibNFSe64`.

É importante notar que você não inclui a extensão do arquivo (como .dll ou .so). A JNA adiciona automaticamente o prefixo e o sufixo apropriados para o sistema operacional em que o código está rodando:

- **Windows**: Procurará por `ACBrLibNFSe64.dll`.
- **Linux**: Procurará por lib `ACBrLibNFSe64.so`.
- **macOS**: Procurará por lib `ACBrLibNFSe64.dylib`.

Isso torna o seu código Java portável entre diferentes sistemas operacionais sem precisar de alterações.

### 3. Onde a JNA Busca o Arquivo `.dll`?

A JNA segue uma ordem de busca padrão para encontrar a biblioteca nativa. Os locais mais relevantes para o seu projeto são:

- **Diretórios específicos da JNA**: Você pode especificar um caminho com a propriedade de sistema `jna.library.path`.
- **O diretório raiz da sua aplicação**: No contexto de um servidor de aplicação como o **Tomcat**, este é o local mais comum e recomendado. O arquivo `ACBrLibNFSe64.dll` deve ser colocado na raiz da aplicação web (ex: `C:\...\Tomcat\webapps\nfseJavaMySQL\`). O JNA encontrará o arquivo ali.
- **Diretórios no PATH do sistema (Windows) ou LD_LIBRARY_PATH (Linux)**: A JNA também procura nos diretórios que estão configurados na variável de ambiente de bibliotecas do sistema operacional. Esta é uma abordagem menos comum para aplicações web, pois dificulta o deploy.

Resumindo, o seu projeto está configurado para que a JNA procure por um arquivo chamado `ACBrLibNFSe64.dll` (em Windows) diretamente na pasta raiz da aplicação onde ela está sendo executada.
