## Sobre

Neste arquivo de README estão contidas as observações feitas acerca da refatoração da classe em questão. Na branch `main` está a classe original Java, denominada "TransacaoValidator" e na branch `refactor` está a classe refatorada, denominada "TransactionValidator".

Na branch `refactor` é possível acompanhar os commits e suas mudanças de acordo com as 10 observações feitas abaixo.

## Observações sobre as mudanças realizadas

1. O nome da classe está misturando português com inglês. O correto é manter o idioma. Para o exemplo, seguirei no padrão “Inglês”.

```jsx
public class TransacaoValidator {
...
}
```

```jsx
public class TransactionValidator {
...
}
```

2. Não faz sentido ter uma classe diferente da atual referenciada no log. O correto seria referenciar a classe que este atributo de Log está inserido.

```jsx
private static final Logger LOGGER = LoggerFactory.getLogger(CapturaTransacaoService.class); 

```

```jsx
private static final Logger LOGGER = LoggerFactory.getLogger(TransactionValidator.class);

```

3. A variável BIT_02 não está sendo utilizada, sendo que poderia até ser referenciada em outros lugares do código em que é utilizado uma String de forma “chumbada”. Sendo assim, ao analisar, por exemplo, a linha 19 do código, gerei variáveis Default com nomes mais significativos para o contexto da solução desenvolvida. Além disso, na linha 19 havia outro problema relacionado ao nome não representativo dado. Sendo assim, foi mudado o nome de “valor” para “bit02Value”. Além disso, retirada linha em branco extra no código.

```jsx
private static final String BIT_02 = "02"; 
{...}
//Linha 19:
String valor = isNotPreenchido ? "01" : "02";
```

```jsx
private static final String DEFAULT_VALUE = "01";
private static final String NON_DEFAULT_VALUE = "02";

//Linha 19:
String bit02Value = isBit02NotSet ? DEFAULT_VALUE : NON_DEFAULT_VALUE;
```

4. O nome da lista deveria ser mais significativo, além disso, deveria seguir o padrão de caixa alta em sua nomenclatura, uma vez que é uma variável do tipo static e final.

```jsx
private static final List<String> lista = List.of("02", "03", "04", "05", "12");
```

```jsx
private static final List<String> LIST_OF_BITS = List.of("02", "03", "04", "05", "12");
```

5. O parâmetro recebido no método “validate” está com nome pouco significativo também (todos os lugares que referenciaram o model foram modificados, bem como outros métodos que seguiam a mesma dinâmica de nomenclatura também).

```jsx
public void validate(ISOModel m) {
```

```jsx
public void validate(ISOModel model) {
```

6. Adicionados comentários mais claros nos logs, também.

```jsx
LOGGER.info("Início");
```

```jsx
LOGGER.info("Iniciando validação da transação.");
```

7. Na sequência de nomenclatura de variáveis iniciada na linha 16, observei alguns pontos importantes de melhoria: nomenclatura significativa, seguir o padrão do idioma, utilizar as variáveis globais para deixar mais legível e significativo o código.

```jsx
boolean isNotPreenchido = m.getBit02() == null;
boolean validateAux = m.getBit02() != null && m.getBit02().getValue().isEmpty();
boolean auxValidacao = m.getBit02() != null && m.getBit02().getValue().isEmpty() && m.getBit03() == null;
String valor = isNotPreenchido ? "01" : "02";
```

```jsx
boolean isBit02NotSet = model.getBit02() == null;
boolean isBit02Empty = !isBit02NotSet && model.getBit02().getValue().isEmpty();
boolean isBit02EmptyAndBit03Null = isBit02Empty && model.getBit03() == null;

String bit02Value = isBit02NotSet ? DEFAULT_VALUE : NON_DEFAULT_VALUE;
```

8. Foi realizada uma mudança dos ifs aninhados, pois essa dificulta a compreensão do código, manutenção e legibilidade. Outro ponto importante a ressaltar, é que o método estava sendo responsável por muitas coisas, sendo assim, foi criado um método suporte chamado “isTransactionValid(ISOModel model)” retornando um boolean. Além disso, o catch vazio sem captura de nenhum erro não faz sentido existir. 

```jsx
try{
  if(!isNotValid(isNotPreenchido, validateAux, auxValidacao, valor)) {
      if(m.getBit03() != null) {
          if(m.getBit04() != null && lista.contains("10")) {
              if(m.getBit05() != null) {
                  if(m.getBit12() != null) {
                      salvar(m, auxValidacao);
                  }
              }
          }
      }
  }
} catch (Exception e) {
}

if(isNotValid(isNotPreenchido, validateAux, auxValidacao, valor)) {
  throw new IllegalArgumentException("Valores não preenchidos");
}
```

```jsx
if (isNotValid(isBit02NotSet, isBit02Empty, isBit02EmptyAndBit03Null , bit02Value)) {
    throw new IllegalStateException("Valores não preenchidos corretamente");
}

if (isTransactionValid(model)) {
    saveTransaction(model, isBit02EmptyAndBit03Null);
}
```

Dependendo da regra de negócio, seria possível receber a variável local bit10 por parâmetro. Porém, decidi não inferir regras de negócio e seguir da forma descrita no código.

```jsx
private boolean isTransactionValid(ISOModel model) {
String bit10 = "10";
return model.getBit03() != null &&
        model.getBit04() != null && LIST_OF_BITS.contains(bit10) &&
        model.getBit05() != null &&
        model.getBit12() != null;
}
```

9. Mudança de nome dos parâmetros para o inglês e adicionando maior significado. Utilização de atributo global, em vez de “chumbar” o código.

```jsx
private boolean isNotValid(boolean validaPreenchido, boolean validaVazio, boolean validaAux, String str) {
	  return validaPreenchido || validaVazio && !validaAux && str.equals("01");
}
```

```jsx
private boolean isNotValid(boolean isBit02NotSet, boolean isBit02Empty, boolean isAuxValidationFailed, String bit02Value) {
    return isBit02NotSet || isBit02Empty && !isAuxValidationFailed && DEFAULT_VALUE.equals(bit02Value);
}
```

10. Mudança de nomenclatura do método. Em vez de retornar uma mensagem para o usuário via terminal, adição de um Log. Além disso, o ideal seria salvar a transação em um banco, porém, hoje a classe apenas retorna um log informando que a transação foi salva.

```jsx
private void salvar(ISOModel m, boolean auxValidacao) {
    if(auxValidacao) {
        throw new IllegalArgumentException("Validacao falhou");
    }

    System.out.println("Salvando transacao " + m.getBit02().getValue());
}
```

```jsx
private void saveTransaction(ISOModel model, boolean isBit02EmptyAndBit03Null) {
  if (isBit02EmptyAndBit03Null) {
      throw new IllegalArgumentException("Validação falhou");
  }
  
  LOGGER.info("Salvando transação com Bit02: {}", model.getBit02().getValue());

}
```
