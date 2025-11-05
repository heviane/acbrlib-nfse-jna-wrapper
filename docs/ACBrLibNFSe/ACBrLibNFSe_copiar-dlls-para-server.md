# Copiar as bibliotecas nativas (DLLs) para o servidor

A etapa de copiar as bibliotecas nativas (DLLs) para o servidor é crucial e, se faltar apenas uma dependência, o erro `UnsatisfiedLinkError` persiste.

No caso de bibliotecas baseadas em C/C++ que interagem com o sistema operacional (como a `ACBrLibNFSe64.dll`), não basta apenas a DLL principal; você também precisa garantir que todas as suas **dependências transitivas** (outras DLLs que ela usa) estejam presentes.

-----

## 📋 Arquivos Essenciais para Copiar (Checklist)

Para que a `ACBrLibNFSe64.dll` funcione em um servidor Java, você precisa copiar **três conjuntos de arquivos** para a pasta raiz da sua aplicação web no Tomcat:

### 1. A DLL Principal

Esta é a biblioteca do ACBr que você compilou ou baixou:

* **`ACBrLibNFSe64.dll`**

### 2. Dependências Comuns (OpenSSL)

A maioria dos módulos do ACBr que lidam com comunicação segura (como NFSe, que lida com comunicação web/certificados) depende das bibliotecas **OpenSSL**.

* **`libeay32.dll`** (ou `libcrypto-1_1-x64.dll`, dependendo da versão do ACBr)
* **`ssleay32.dll`** (ou `libssl-1_1-x64.dll`, dependendo da versão do ACBr)

> **Importante:** Se você usa uma versão mais moderna do ACBr Lib, os nomes das DLLs do OpenSSL podem ser `libcrypto-x64.dll` e `libssl-x64.dll` (ou similar). Consulte a documentação do instalador do ACBr Lib para confirmar quais DLLs de terceiros são usadas.

### 3. O Arquivo de Configuração

Embora não cause um `UnsatisfiedLinkError`, a biblioteca precisará de seu arquivo de configuração na hora de inicializar.

* **`ACBrLib.ini`** (ou similar, se você o renomeou)

-----

## 🗺️ Onde Copiar

Copie **todos** os arquivos listados acima para a **raiz** da sua aplicação no Tomcat:

* **Caminho Alvo:** `C:\Program Files\Apache Software Foundation\Tomcat 11.0\webapps\nfseJavaMySQL\`

### Resumo da Pasta Alvo

```text
C:\Program Files\...\nfseJavaMySQL\
  |-- ACBrLibNFSe64.dll  <-- ESSENCIAL
  |-- libeay32.dll       <-- Geralmente necessário
  |-- ssleay32.dll       <-- Geralmente necessário
  |-- ACBrLib.ini        <-- Para configuração
  |-- Schemas            <-- Schemas (configurados no arquivo .ini)
  |-- WEB-INF/
      |-- lib/
          |-- acbrnfse_wrapper-1.0.jar (Seu código Java)
          |-- jna-5.14.0.jar (A dependência JNA)
          |-- jna-platform-5.14.0.jar
          |-- ... outros JARs do Genexus
```

### Próximo Passo

1. **Reúna** a DLL principal (`ACBrLibNFSe64.dll`) e as duas dependências do OpenSSL (`libeay32.dll` e `ssleay32.dll`).
2. **Copie** todos para a pasta **raiz** do `nfseJavaMySQL`.
3. **Reinicie o Tomcat** (Parar e Iniciar o serviço).

Se o erro persistir após a cópia correta das dependências e o reinício, será necessário verificar se existe alguma dependência adicional do Visual C++ Redistributable, mas geralmente a presença das DLLs do OpenSSL resolve a maioria dos casos.
