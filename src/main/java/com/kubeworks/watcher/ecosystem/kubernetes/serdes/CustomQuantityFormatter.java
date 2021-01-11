package com.kubeworks.watcher.ecosystem.kubernetes.serdes;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.custom.SuffixFormatter;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;

public class CustomQuantityFormatter {

    public String format(final Quantity quantity) {
        switch (quantity.getFormat()) {
            case DECIMAL_SI:
            case DECIMAL_EXPONENT:
                return toBase10String(quantity);
            case BINARY_SI:
                if (isFractional(quantity)) {
                    return toBase10String(new Quantity(quantity.getNumber(), Quantity.Format.DECIMAL_SI));
                }
                return toBase1024String(quantity);
            default:
                throw new IllegalArgumentException("Can't format a " + quantity.getFormat() + " quantity");
        }
    }

    private String toBase10String(final Quantity quantity) {
        final BigDecimal amount = quantity.getNumber();
        final long value = amount.unscaledValue().longValue();
        final int exponent = -amount.scale();
        final Pair<Pair<Long, Pair<Long, Long>>, Integer> resultAndTimes = removeFactorsForBase(value, 10);
        final int postFactoringExponent = exponent + resultAndTimes.getRight();
        final Pair<Long, Integer> valueAndExponent =
            ensureExponentIsMultipleOf3(resultAndTimes.getLeft(), postFactoringExponent);
        return valueAndExponent.getLeft()
            + new SuffixFormatter().format(quantity.getFormat(), valueAndExponent.getRight());
    }

    private String toBase1024String(final Quantity quantity) {
        final BigDecimal amount = quantity.getNumber();
        final long value = amount.unscaledValue().longValue();
        final int exponent = -amount.scale();
        final Pair<Pair<Long, Pair<Long, Long>>, Integer> resultAndTimes = removeFactorsForBase(value, 1024);
        return resultAndTimes.getLeft().getLeft()
            + new SuffixFormatter()
            .format(quantity.getFormat(), exponent + resultAndTimes.getRight() * 10);
    }

    private boolean isFractional(Quantity quantity) {
        return quantity.getNumber().scale() > 0;
    }

    private Pair<Pair<Long, Pair<Long, Long>>, Integer> removeFactorsForBase(final long value, final int base) {
        int times = 0;
        long result = value;
        LinkedList<Long> remainder = new LinkedList<>(Arrays.asList(0L, 0L));
        while (result >= base) {
            times++;
            remainder.addFirst(result % base);
            result = result / base;
        }
        return Pair.of(Pair.of(result, Pair.of(remainder.getFirst(), remainder.get(1))), times);
    }

    private Pair<Long, Integer> ensureExponentIsMultipleOf3(final Pair<Long, Pair<Long, Long>> mantissa, final int exponent) {
        final long exponentRemainder = exponent % 3;
        if (exponentRemainder == 1 || exponentRemainder == -2) {
            return Pair.of(mantissa.getLeft() * 10 + mantissa.getRight().getLeft(), exponent - 1);
        } else if (exponentRemainder == -1 || exponentRemainder == 2) {
            return Pair.of(mantissa.getLeft() * 100 + mantissa.getRight().getLeft() * 10 + mantissa.getRight().getRight(), exponent - 2);
        } else {
            return Pair.of(mantissa.getLeft(), exponent);
        }
    }

}
