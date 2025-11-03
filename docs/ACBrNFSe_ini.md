# Arquivo INI

O arquivo INI é o ponto de partida, mas os métodos são a forma de interagir com a biblioteca durante a execução.

Baseado na arquitetura do ACBrLib, a finalidade de cada método é clara:

## 🔑 Funções dos Métodos do ACBrLibNFSe

| Método | Finalidade | Status |
| :--- | :--- | :--- |
| **`Inicializar()`** | É o método **obrigatório** de *setup*. Ele faz o *boot* da biblioteca, carrega as DLLs dependentes, e lê o arquivo de configuração inicial (`ACBrLib.ini`). | **Obrigatório** (Executado apenas uma vez ao iniciar a instância). |
| **`SalvarConfiguracao()`** | Usado para **alterar parâmetros de configuração em *runtime*** (durante a execução da aplicação) e salvá-los no arquivo INI. | **Opcional** (Apenas se precisar mudar configurações, como certificados, dinamicamente). |
| **`Finalizar()`** | É o método **obrigatório** de *shutdown*. Ele libera a memória e os recursos alocados pela biblioteca nativa, prevenindo *memory leaks* ou *locks* de arquivos. | **Obrigatório** (Executado apenas uma vez ao finalizar a instância ou a sessão). |

---

### 1. `Inicializar()`: O Bootstrap

> **Você realmente precisa executar este método?** **Sim, é essencial.**

A DLL do ACBrLib é um código nativo (C++ ou Delphi) que precisa ser carregado na memória do processo Java (Tomcat). O `Inicializar()` faz exatamente isso, além de:

* **Carregar Configuração Inicial:** Ele lê o `ACBrLib.ini` que você passou como parâmetro.
* **Preparar a Biblioteca:** Inicializa componentes internos, abre loggers, e prepara variáveis de estado.
* **Retornar Handle/Erro:** Se a inicialização falhar (por não encontrar o INI, DLL, ou dependências), ele retorna um código de erro (no seu *wrapper* Java, se ele seguir o padrão) em vez de uma exceção.

Sem o `Inicializar()`, a biblioteca não está pronta para usar nenhuma outra função (como a emissão de NFSe), e você receberá erros.

### 2. `SalvarConfiguracao()`: Ajuste Dinâmico

> **O que é e quando usar?**

Serve para mudar uma ou mais configurações **depois** que a biblioteca foi inicializada, sem a necessidade de reiniciar o Tomcat.

**Exemplo de Uso:**

Se o seu sistema permite que o usuário troque o certificado digital em *runtime* sem parar o servidor, você usaria o `SalvarConfiguracao()` para atualizar o caminho do novo certificado no arquivo INI e na memória da biblioteca.

### 3. `Finalizar()`: Limpeza e Liberação

> **Você realmente precisa executar este método?** **Sim, é essencial.**

No ambiente Java/Tomcat, que tem um ciclo de vida longo, o **`Finalizar()` é CRÍTICO.**

* **Liberação de Recursos:** Ele garante que todos os objetos nativos (do C++), conexões e alocações de memória da DLL sejam liberados.
* **Evitar Bloqueios:** Impede que a DLL mantenha bloqueios em arquivos (como o log ou o próprio INI) ou portas de rede.

O Genexus costuma gerenciar a finalização automaticamente para External Objects (EOs) criados como SDTs, mas o método `Finalizar()` precisa estar exposto no seu EO (`ACBrNFSe_JNA`) e chamar o método correspondente no seu *wrapper* Java para garantir que a DLL nativa seja descarregada corretamente.

---

## ✅ Resposta Direta no seu Cenário Genexus

**Você deve ter certeza de que está chamando:**

1. **`Inicializar("Caminho.ini", "CaminhoLog")`** (Uma vez ao instanciar o objeto).
2. ... (Usa outros métodos como `EnviarNFSe()`, `ConsultarSitucao()`, etc.).
3. **`Finalizar()`** (Uma vez ao fim do uso do objeto, ou quando a sessão do usuário terminar, se o objeto tiver escopo de sessão).

**Próximo Passo:** Confirme se o seu código Genexus está chamando o método `Inicializar()` e se o `arqConfig` (primeiro parâmetro) está sendo passado como o nome do arquivo ou o caminho absoluto (conforme discutido anteriormente).

Com a confirmação de que os JARs estão no `lib` e as DLLs estão na raiz da aplicação, este é o ponto onde a aplicação deve funcionar se o caminho for resolvido corretamente.
