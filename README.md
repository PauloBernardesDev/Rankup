# RankUp

Sistema de RankUp configurável para servidores Minecraft Paper.

O **RankUp** permite criar uma progressão de ranks baseada em dinheiro e horas jogadas, com benefícios cumulativos configurados diretamente no `config.yml`.

O projeto foi desenvolvido em Java e utiliza LuckPerms, Vault, PlaceholderAPI e SQLite.

## Recursos

* Sistema de ranks totalmente configurável.
* Progressão baseada em dinheiro e horas jogadas.
* Interface gráfica para visualização dos ranks.
* Ranks cumulativos.
* Permissões individuais por rank através do LuckPerms.
* Sistema de recompensas por comandos configuráveis.
* Sistema de horas bônus.
* Banco de dados SQLite.
* Integração com economia através do Vault.
* Integração com PlaceholderAPI.
* Lore personalizada para cada rank.
* Mensagens configuráveis pelo `config.yml`.
* Prefixo configurável.
* Anúncio global quando um jogador sobe de rank.
* Comando administrativo para definir o rank de um jogador.
* Comando de recarregamento da configuração.
* Suporte a quantidade de ranks configurável.

## Tecnologias

* Java 21
* Paper 1.21+
* LuckPerms
* Vault
* PlaceholderAPI
* SQLite

## Dependências

### Obrigatórias

* [LuckPerms](https://luckperms.net/)
* [Vault](https://www.spigotmc.org/resources/vault.34315/)

### Opcionais

* [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)

## Configuração

Os ranks são definidos no `config.yml`.

Exemplo:

```yaml
ranks:

  rank1:
    nivel: 1
    nome: "&7[01/10]"
    dinheiro: 0
    horas: 0

    permissoes: []

    comandos: []

    lore:
      - "&7Este é o seu ponto de partida."
      - ""
      - "&6Requisitos:"
      - "&7Dinheiro: &f$%dinheiro%"
      - "&7Horas: &f%horas%"

  rank2:
    nivel: 2
    nome: "&f[02/10]"
    dinheiro: 10000
    horas: 0

    permissoes:
      - simplesglobalchat.global

    comandos: []

    lore:
      - "&7Ao alcançar este rank:"
      - ""
      - "&a✓ &fAcesso ao Chat Global"
      - ""
      - "&6Requisitos:"
      - "&7Dinheiro: &f$%dinheiro%"
      - "&7Horas: &f%horas%"

  rank3:
    nivel: 3
    nome: "&a[03/10]"
    dinheiro: 10000
    horas: 0

    permissoes:
      - essentials.signs.color
      - essentials.sethome.multiple
      - essentials.sethome.multiple.rank3

    comandos: []

    lore:
      - "&7Ao alcançar este rank:"
      - ""
      - "&a✓ &fCores nas placas"
      - "&a✓ &fAté 2 homes"
      - ""
      - "&6Requisitos:"
      - "&7Dinheiro: &f$%dinheiro%"
      - "&7Horas: &f%horas%"
```

## Progressão cumulativa

Os benefícios dos ranks são cumulativos.

Por exemplo:

```text
Rank 1
└── Nenhum benefício

Rank 2
└── Chat Global

Rank 3
├── Chat Global
├── Cores nas placas
└── Até 2 homes
```

Isso permite que cada novo rank adicione benefícios sem remover os anteriores.

## Recompensas

Cada rank pode executar comandos configurados no `config.yml`.

Exemplo:

```yaml
comandos:
  - "give %jogador% diamond 5"
```

Placeholders disponíveis nas recompensas:

```text
%jogador%
%uuid%
%rank%
%nivel%
```

Os comandos são executados pelo console após a promoção do jogador.

## Sistema de mensagens

As mensagens destinadas aos jogadores são centralizadas no `configuracao.mensagens` do `config.yml`.

Exemplo:

```yaml
configuracao:

  prefixo: "&8[&6RankUp&8] "

  mensagens:

    rankup:
      sucesso: "%prefixo%&aVocê alcançou o rank &f%rank%&a!"
      anuncio-global: "%prefixo%&f%jogador% &aalcançou o rank &f%rank%&a!"
```

Isso permite alterar ou traduzir as mensagens sem modificar o código-fonte.

## Comandos

### Jogadores

```text
/rankup
```

Abre o menu de RankUp.

### Administração

```text
/rankup set <jogador> <rank>
```

Define o rank de um jogador.

```text
/horas <jogador>
```

Mostra as horas de jogo.

```text
/addhoras <jogador> <horas>
```

Adiciona horas bônus.

```text
/removehoras <jogador> <horas>
```

Remove horas bônus.

```text
/rankup reload
```

Recarrega o `config.yml` e os ranks sem precisar reiniciar o servidor.

O `reload` é destinado a operadores e ao console.

## Sistema de horas

O RankUp utiliza o tempo de jogo do Minecraft através da estatística `PLAY_ONE_MINUTE`.

Além das horas normais, o sistema possui horas bônus, armazenadas no SQLite.

O total utilizado para promoção é:

```text
Horas Minecraft + Horas Bônus
```

## Sistema de economia

Os requisitos monetários utilizam o Vault.

Exemplo:

```yaml
dinheiro: 10000
```

Ao realizar a promoção, o valor definido para o próximo rank é retirado do saldo do jogador.

##
