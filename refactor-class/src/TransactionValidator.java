import java.util.List;
import java.util.logging.Logger;


public class TransactionValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionValidator.class);
    private static final String DEFAULT_VALUE = "01";
    private static final String NON_DEFAULT_VALUE = "02";
    private static final List<String> LIST_OF_BITS = List.of("02", "03", "04", "05", "12");

    public void validate(ISOModel model) {
        LOGGER.info("Starting transaction validation.");

        boolean isBit02NotSet = model.getBit02() == null;
        boolean isBit02Empty = !isBit02NotSet && model.getBit02().getValue().isEmpty();
        boolean isBit02EmptyAndBit03Null  = isBit02Empty && model.getBit03() == null;

        String bit02Value = isBit02NotSet ? DEFAULT_VALUE : NON_DEFAULT_VALUE;

        try{
            if(!isNotValid(isNotPreenchido, validateAux, auxValidacao, valor)) {
                if(model.getBit03() != null) {
                    if(model.getBit04() != null && LIST_OF_BITS.contains("10")) {
                        if(model.getBit05() != null) {
                            if(model.getBit12() != null) {
                                salvar(model, auxValidacao);
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

    private void salvar(ISOModel model, boolean auxValidacao) {
        if(auxValidacao) {
            throw new IllegalArgumentException("Validacao falhou");
        }

        System.out.println("Salvando transacao " + model.getBit02().getValue());
    }

}
