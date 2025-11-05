# Projetos Mavem

Projetos Maven são projetados para serem autocontidos e portáteis.

O arquivo `pom.xml` é o "cérebro" do projeto, e ele não contém caminhos absolutos para sua própria localização.
Todas as referências a arquivos de código-fonte (src/main/java), recursos e a pasta de saída (target) são relativas à localização do próprio pom.xml.

## Como Compilar?

```bash
# Dentro do diretório do projeto
mvn clean package
```

O resultado da compilação (o arquivo `.jar`) será gerado dentro da pasta `target`.
