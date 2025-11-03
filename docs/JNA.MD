# JNA (Java Native Access)

É a biblioteca responsável por carregar a DLL.

ERROR: **`java.lang.NoClassDefFoundError: com/sun/jna/Library`**, é um **erro de *runtime***, não de compilação.
Isso indica que a aplicação conseguiu carregar sua classe `ACBrNFSeWrapper` (conforme o log), mas falhou ao encontrar uma das suas dependências, que é o **JNA (Java Native Access)**.

---

## 🛑 Análise e Solução do `NoClassDefFoundError` (JNA)

### Causa

O `NoClassDefFoundError` ocorre porque a **biblioteca JNA** (`jna.jar` e `jna-platform.jar`) não está sendo carregada pelo *Class Loader* do seu servidor de aplicação (Tomcat ou similar), no momento em que seu código Java tenta usá-la.

* Quando você compilou o projeto no VS Code com Maven (`mvn clean package`), o Maven **incluiu** suas classes (`ACBrNFSeWrapper`, `ACBrNFSeLib`) e as classes JNA dentro do **seu JAR** (`acbrnfse_wrapper-1.0.jar`).
* No entanto, o JNA (que é uma dependência) pode não estar sendo *desempacotado* ou *exposto* corretamente pelo Genexus/Gradle no diretório de classes do servidor.

### 🛠️ Solução: Fat JAR (Uber JAR) ou Inclusão Explícita

Temos duas opções para garantir que o JNA esteja disponível no *runtime* do Tomcat/servidor.

#### Opção 1 (Recomendada): Gerar um Fat JAR (Incluindo JNA no JAR Final)

A maneira mais robusta é refazer o *build* do seu *wrapper* JNA para que ele inclua **todas** as dependências (JNA) dentro do seu JAR final. Isso elimina a necessidade de copiar arquivos JNA separados.

Para fazer isso no seu projeto Maven (VS Code), você precisa usar o plugin `maven-assembly-plugin` ou `maven-shade-plugin`.

**Passos no VS Code (Projeto Maven):**

1. **Abra o `pom.xml`** no seu projeto JNA.

2. **Adicione o Plugin de Shading** (Sombra/Montagem) dentro da tag `<build>` e `<plugins>`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.5.3</version> <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <artifactSet>
                            <excludes>
                                </excludes>
                        </artifactSet>
                        <finalName>${project.artifactId}-shaded</finalName> 
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

3. **Compile Novamente:** Abra o terminal no VS Code e execute:

```bash
mvn clean package
```

4. **Resultado:** Procure na pasta `target` por um arquivo chamado, por exemplo, **`acbrnfse_wrapper-shaded.jar`**. Este novo JAR contém o JNA e todas as suas classes.

5. **Substitua no Genexus:** Copie este novo `*-shaded.jar` para a pasta `[Caminho do Projeto Genexus]\web\lib\`, **substituindo** o JAR antigo.

#### Opção 2 (Alternativa Rápida): Copiar JNA Separadamente

Se você quiser uma correção imediata e não puder recompilar o JAR (ou se o Genexus não gostar do JAR sombreado):

1. **Localize os JARs do JNA:**
    * No seu projeto Maven, navegue até a pasta de *cache* local do Maven (geralmente em `C:\Users\[SeuUsuario]\.m2\repository\net\java\dev\jna\jna\` e `...\jna-platform\`).
    * Encontre os arquivos **`jna-[versao].jar`** e **`jna-platform-[versao].jar`**.
2. **Copie para o Genexus:**
    * Copie estes dois JARs diretamente para a pasta `[Caminho do Projeto Genexus]\web\lib\` (a mesma pasta onde está seu `acbrnfse_wrapper-1.0.jar`).
3. **Reinicie o Servidor:** Pare e inicie seu servidor de aplicação (Tomcat, etc.) para forçar o carregamento dos novos JARs.

```bash
# Opção 2 (Alternativa Rápida): Copiar JNA Separadamente

## JARs do JNA (Pasta de cache do Maven, geralmente C:\Users\[SeuUsuario]\.m2\repository\ )
## Se esses dois arquivos estiverem no CLASSPATH do seu servidor web (o que a pasta lib garante), o erro desaparecerá.
## Reinicie o Servidor: Pare e inicie seu servidor de aplicação (Tomcat, etc.) para forçar o carregamento dos novos JARs.
cp /Users/hevianebastos/.m2/repository/net/java/dev/jna/jna/5.14.0/jna-5.14.0.jar /Users/hevianebastos/Downloads/
cp /Users/hevianebastos/.m2/repository/net/java/dev/jna/jna-platform/5.14.0/jna-platform-5.14.0.jar /Users/hevianebastos/Downloads/
```

> **NOTA:** Recomendo tentar a **Opção 1** por ser mais profissional e garantir a integridade do seu *wrapper* em um único arquivo.
