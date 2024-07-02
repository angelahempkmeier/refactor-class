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

        if (isNotValid(isBit02NotSet, isBit02Empty, isBit02EmptyAndBit03Null , bit02Value)) {
            throw new IllegalStateException("Values not correctly filled.");
        }

        if (isTransactionValid(model)) {
            saveTransaction(model, isBit02EmptyAndBit03Null);
        }
    }

    private boolean isNotValid(boolean isBit02NotSet, boolean isBit02Empty, boolean isBit02EmptyAndBit03Null, String bit02Value) {
        return isBit02NotSet || isBit02Empty && !isBit02EmptyAndBit03Null && DEFAULT_VALUE.equals(bit02Value);
    }

    private boolean isTransactionValid(ISOModel model) {
        String bit10 = "10";
        return model.getBit03() != null &&
                model.getBit04() != null && LIST_OF_BITS.contains(bit10) &&
                model.getBit05() != null &&
                model.getBit12() != null;
    }

    private void saveTransaction(ISOModel model, boolean isBit02EmptyAndBit03Null) {
        if (isBit02EmptyAndBit03Null) {
            throw new IllegalArgumentException("Validation failed: Bit02 is empty or Bit03 is null or both.");
        }

        LOGGER.info("Saving transaction with Bit02: {}", model.getBit02().getValue());
    }
}