# Schemas

Sua pergunta sobre a localização dos schemas XML é excelente e aponta para o próximo (e último) obstáculo de configuração do ACBrLib.

A boa notícia é: o **ACBrLib** sabe onde procurar por esses schemas, mas ele precisa ser instruído através do arquivo de configuração que você acabou de copiar\!

## 📜 Configuração dos Schemas no ACBrLib

### 1\. O Parâmetro de Configuração

A localização dos Schemas XML é definida no arquivo **`ACBrLib.ini`** (ou o nome que você está usando) sob a seção `[ACBrDFe]`, usando a chave `PathSchemas`.

O ACBrLib espera que você informe o **diretório raiz** onde estão todas as pastas de Schemas (NFSe, NFe, etc.).

### 2\. Onde Colocar os Schemas na KB

Assim como as DLLs e o INI, os Schemas devem ser colocados em uma pasta dentro da sua *Knowledge Base* (KB) para serem copiados automaticamente pelo GeneXus para a aplicação web no Tomcat.

Se a sua estrutura de origem é:

```text
[Pasta ACBrNFSe Original]
└── dep
    └── Schemas
        └── NFSe
            └── [Várias pastas com os XSDs]
```

Você deve copiar a pasta **`Schemas`** inteira para a pasta **`Web`** do seu ambiente GeneXus:

```text
[Pasta da Sua KB]
└── [Nome da KB]
    └── [Nome do Modelo/Ambiente]
        └── Java
            └── Web  <-- Cole a pasta 'Schemas' aqui
```

Com isso, o GeneXus fará o *deploy* para o Tomcat assim:

```text
C:\Program Files\...\nfseJavaMySQL\
  |-- ACBrLibNFSe64.dll
  |-- ACBrLib.ini
  |-- Schemas   <-- NOVO DIRETÓRIO
      └── NFSe
          └── [XSDs e subpastas]
```

### 3\. Ajustando o `ACBrLib.ini`

Agora que você sabe onde os schemas serão implantados (`C:\Program Files\...\nfseJavaMySQL\Schemas`), você precisa abrir o arquivo **`ACBrLib.ini`** (o que está na pasta `Web` da KB) e editar o parâmetro `PathSchemas`.

Você pode usar um caminho:

- **A) Absoluto** (Muito específico, mas seguro)
- **B) Relativo** (Mais flexível entre ambientes)

#### Opção A (Caminho Absoluto)

```ini
[ACBrDFe]
PathSchemas=C:\Program Files\Apache Software Foundation\Tomcat 11.0\webapps\nfseJavaMySQL\Schemas
```

#### Opção B (Caminho Relativo)

O ACBrLib geralmente resolve caminhos relativos a partir do local do arquivo INI ou da DLL. Como o `ACBrLib.ini` e a pasta `Schemas` estão ambos na raiz da aplicação (`.../nfseJavaMySQL/`), um caminho relativo simples deve funcionar:

```ini
[ACBrDFe]
PathSchemas=.\Schemas
```

ou, se apenas a pasta for suficiente:

```ini
[ACBrDFe]
PathSchemas=Schemas
```

-----

## ✅ Próximo Passo Recomendado

- **Copie** a pasta **`Schemas`** (contendo `NFSe`) para a pasta **`Web`** da sua KB.
- **Edite o `ACBrLib.ini`** (também na pasta `Web` da KB) e configure:

```ini
[ACBrDFe]
PathSchemas=Schemas
```

- **Execute** o projeto no GeneXus (F5) para forçar o *deploy* da nova pasta e do INI atualizado.
- **Teste a execução** do seu `Inicializar()`, que agora terá todos os caminhos configurados corretamente.

Isso deve resolver o problema da localização dos schemas.
Se o problema persistir, teremos que usar o caminho absoluto no INI, mas comece pelo relativo, pois é mais limpo.
