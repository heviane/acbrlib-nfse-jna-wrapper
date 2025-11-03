
# Visão Geral

## O problema

(Dificuldade de PInvoke direto em Java/Genexus) e a solução (JNA Wrapper).

## Tecnologias

- Java
- Maven
- JNA
- ACBrLibNFSe

## Configuração

Os blocos de código do pom.xml para as dependências JNA.

## Uso no Genexus

Instruções claras sobre como: 1. Compilar o projeto (mvn clean package). 2. Onde encontrar o JAR gerado. 3. Como configurar o External Object no Genexus para usar o JAR e a classe ACBrNFSeWrapper.

## Artefatos Nativos

Notas sobre a necessidade de colocar a ACBrLibNFSe64.dll e suas dependências (que estão na pasta native/win64) no PATH ou no diretório de runtime do Genexus/Java.
