# Especificar o Local das Libs com `jna.library.path`

Especificar o `jna.library.path` é uma excelente prática para organizar suas DLLs, especialmente em um ambiente de servidor como o Tomcat. Isso centraliza as bibliotecas nativas em um único local, em vez de misturá-las com sua aplicação web.

> Aqui está um passo a passo detalhado de como configurar o `jna.library.path` no **Windows** para o seu projeto.

## O que é o jna.library.path?

É uma "propriedade de sistema" Java que diz à JNA: "Antes de procurar nos locais padrão, por favor, procure primeiro neste diretório específico que estou te informando".

Vamos usar o cenário mais comum e recomendado para um servidor: configurar a propriedade na inicialização do Tomcat.

## Passo a Passo para Configurar o `jna.library.path` no Tomcat (Windows)

Este método é o ideal porque a configuração fica atrelada ao servidor, e não à sua aplicação. Isso significa que, mesmo que você atualize sua aplicação (.war), as DLLs permanecem no lugar e a configuração é mantida.

### Passo 1: Criar um Diretório Central para as DLLs

Primeiro, crie uma pasta em um local fixo e de fácil acesso no seu servidor. Este diretório irá conter todas as DLLs necessárias.

1. Crie uma pasta, por exemplo: `C:\acbr_libs\`
2. Copie todas as DLLs necessárias para dentro desta nova pasta. E quaisquer outras dependências que a lib possa ter.

Agora, a pasta `C:\acbr_libs\` deve conter esses arquivos.

### Passo 2: Configurar a Variável de Ambiente do Tomcat

O Tomcat utiliza uma variável de ambiente chamada `CATALINA_OPTS` para receber opções de inicialização da JVM (Máquina Virtual Java). É aqui que vamos "injetar" a propriedade `jna.library.path`.

Existem duas maneiras de fazer isso:

#### Maneira 1: Temporária (para testes)

Configuração Programática para testes.

Definir essa propriedade diretamente no seu código Java. Isso não é recomendado para produção em um servidor, pois é menos flexível, mas é útil para testes rápidos.

Para fazer isso, você precisaria adicionar a seguinte linha no seu código antes de qualquer chamada à JNA (ou seja, antes da linha `ACBrNFSeLib INSTANCE = ...)`.

```java
// Exemplo de como seria em um bloco estático na sua classe Wrapper
public class ACBrNFSeWrapper {

    static {
        // Define o caminho ANTES de a JNA tentar carregar a biblioteca
        System.setProperty("jna.library.path", "C:\\acbr_libs\\");
    }

    // ... resto do seu código
}
```

> **IMPORTANTE**: Note que em uma String Java, a barra invertida `\` é um caractere de escape, então você precisa usar duas: `\\`.

Esta abordagem é menos ideal porque "chumba" o caminho no código e a configuração precisa ser executada muito cedo no ciclo de vida da aplicação, o que pode ser difícil de garantir. A configuração via `CATALINA_OPTS` é muito mais robusta.

#### Maneira 2: Permanente (recomendado para produção)

Esta é a melhor abordagem para um servidor, pois a configuração sobrevive a reinicializações do sistema.

1. Pressione a tecla Windows + R, digite `sysdm.cpl` e pressione Enter. Isso abrirá as "Propriedades do Sistema".
2. Vá para a aba Avançado.
3. Clique no botão Variáveis de Ambiente....
4. Na seção "Variáveis do sistema", clique em Novo....

   - Nome da variável: `CATALINA_OPTS`
   - Valor da variável: `-Djna.library.path=C:\acbr_libs`

5. Clique OK em todas as janelas para salvar.

> **IMPORTANTE**: Se a variável `CATALINA_OPTS` já existir, em vez de criar uma nova, edite a existente e adicione `-Djna.library.path=C:\acbr_libs` ao final do valor, separado por um espaço.

### Passo 3: Reiniciar o Tomcat e Verificar

1. Reinicie o serviço do Tomcat:
   - Se você estiver rodando o Tomcat pelo `startup.bat`, feche a janela do console e abra uma nova.
   - Se for um serviço do Windows, reinicie-o pelo `services.msc`.
2. Quando o Tomcat iniciar, ele passará a opção `-Djna.library.path=C:\acbr_libs` para a JVM.
3. A JNA, ao ser chamada pelo seu código com `Native.load("ACBrLibNFSe64", ...)` irá primeiro procurar por `ACBrLibNFSe64.dll` dentro do diretório `C:\acbr_libs\`.
4. O Windows, ao carregar a `ACBrLibNFSe64.dll`, também encontrará suas dependências (libeay32.dll, etc.) no mesmo diretório `C:\acbr_libs\`.

Com isso, você não precisa mais copiar as DLLs para a pasta raiz da sua aplicação web (webapps\nfseJavaMySQL). Sua estrutura fica muito mais limpa e organizada.
