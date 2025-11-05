# Projeto JNA

Projeto Maven de uma solução JNA Wrapper.
Genexus não importa `DLLs` que não possuem código gerenciado em .NET, como a biblioteca **ACBrLibNFSe**.

## Build (Compilação)

Para compilar, execute este código no terminal:

```bash
cd ACBrLibNFSe\ACBrLibNFSeMT\Genexus\Import\acbrnfse_wrapper
mvn clean package
```

O resultado da compilação (o arquivo `.jar`) será gerado dentro da pasta `target`.

## Uso no Genexus

Instruções claras sobre como:

1. Onde colocar o JAR gerado.
2. Como configurar o External Object no Genexus para usar o JAR e a classe ACBrNFSeWrapper.
3. Onde colocar a ACBrLibNFSe64.dll e suas dependências
