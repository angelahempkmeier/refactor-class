import java.util.List;
import java.util.logging.Logger;


public class TransactionValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(CapturaTransacaoService.class);
    private static final String BIT_02 = "02";

    private static final List<String> lista = List.of("02", "03", "04", "05", "12");

    public void validate(ISOModel m) {
        LOGGER.info("Início");

        boolean isNotPreenchido = m.getBit02() == null;
        boolean validateAux = m.getBit02() != null && m.getBit02().getValue().isEmpty();
        boolean auxValidacao = m.getBit02() != null && m.getBit02().getValue().isEmpty() && m.getBit03() == null;
        String valor = isNotPreenchido ? "01" : "02";

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

    }

    private boolean isNotValid(boolean validaPreenchido, boolean validaVazio, boolean validaAux, String str) {
        return validaPreenchido || validaVazio && !validaAux && str.equals("01");
    }

    private void salvar(ISOModel m, boolean auxValidacao) {
        if(auxValidacao) {
            throw new IllegalArgumentException("Validacao falhou");
        }

        System.out.println("Salvando transacao " + m.getBit02().getValue());
    }

}
