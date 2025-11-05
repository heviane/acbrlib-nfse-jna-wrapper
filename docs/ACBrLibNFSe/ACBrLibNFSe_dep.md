# Dependências Transitivas

Crucial sobre o *deployment* de bibliotecas nativas!
A resposta envolve o conceito de **dependências transitivas** do Windows (o seu `UnsatisfiedLinkError` inicial) e como o sistema operacional as carrega, e não o ACBrLib.

## 🎯 Regra de Ouro: Dependências na Raiz

As DLLs que ficam dentro da pasta **`dep`** do ACBrLib (como as DLLs do OpenSSL: `libeay32.dll`, `ssleay32.dll`, ou as dependências do FastReport, se aplicável) são as **dependências transitivas** da DLL principal (`ACBrLibNFSe64.dll`).

**Elas não são configuradas no `ACBrLib.ini`**, porque não são bibliotecas que o ACBrLib carrega explicitamente, mas sim bibliotecas que o próprio Windows (ou o carregador da DLL principal) precisa para que a `ACBrLibNFSe64.dll` possa ser carregada.

### O Que Acontece ao Inicializar

1. Seu código Java chama o JNA para carregar **`ACBrLibNFSe64.dll`**.
2. O Windows tenta carregar essa DLL.
3. O Windows verifica o **manifesto** da `ACBrLibNFSe64.dll` e vê que ela precisa de `libeay32.dll` e `ssleay32.dll` (e outras).
4. O Windows procura essas DLLs nos caminhos padrão do sistema, incluindo:
   * O diretório onde a `ACBrLibNFSe64.dll` foi encontrada (a raiz da sua aplicação no Tomcat: `.../nfseJavaMySQL/`).
   * As pastas listadas na variável de ambiente `PATH`.

Se as dependências estiverem em uma subpasta (`dep`), o carregador do Windows **não as encontrará**, resultando novamente no erro `UnsatisfiedLinkError` (ou em uma falha mais sutil durante a execução de uma função que precise de OpenSSL).

---

## 📋 Ação Definitiva para o Deploy

Você deve copiar **todas as DLLs necessárias** para o **mesmo diretório** onde está a DLL principal (`ACBrLibNFSe64.dll`) e o `ACBrLib.ini`.

### 1. Lista Final de Arquivos para a Pasta `Web` da KB

Copie o conteúdo da pasta `dep` (apenas as DLLs) e os arquivos essenciais para a pasta **`Web`** do seu ambiente GeneXus:

| Arquivo/Pasta | Localização na KB | Destino no Tomcat |
| :--- | :--- | :--- |
| `ACBrLibNFSe64.dll` | `[KB]\Java\Web` | `.../nfseJavaMySQL/` (Raiz) |
| **`libeay32.dll`** | `[KB]\Java\Web` | `.../nfseJavaMySQL/` (Raiz) |
| **`ssleay32.dll`** | `[KB]\Java\Web` | `.../nfseJavaMySQL/` (Raiz) |
| `ACBrLib.ini` | `[KB]\Java\Web` | `.../nfseJavaMySQL/` (Raiz) |
| `Schemas` (Pasta) | `[KB]\Java\Web\Schemas` | `.../nfseJavaMySQL/Schemas` |

**Resumindo:** Todas as DLLs de tempo de execução precisam estar no mesmo nível da `ACBrLibNFSe64.dll` para que o carregador nativo do Windows as encontre sem problemas no ambiente Tomcat.

Depois de copiar esses arquivos para a pasta `Web` e rodar o F5 no GeneXus, sua aplicação deverá ter todos os recursos necessários implantados corretamente no Tomcat.
