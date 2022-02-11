package net.dzikoysk.funnyguilds.config.range;

public interface AbstractRange<T extends Number> {

    T getMinRange();

    T getMaxRange();

    default NumberRange toNumberRange() {
        return new NumberRange(getMinRange(), getMaxRange());
    }

    default IntegerRange toIntegerRange() {
        return new IntegerRange(getMinRange().intValue(), getMaxRange().intValue());
    }

    default DoubleRange toDoubleRange() {
        return new DoubleRange(getMinRange().doubleValue(), getMaxRange().doubleValue());
    }

}
