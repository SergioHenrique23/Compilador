package interpreter.value;

public class FloatValue extends Value<Float> {
    private Float value;

    public FloatValue(Float value) {
        this.value = value;
    }

    @Override
    public Float value() {
        return this.value;
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof FloatValue) {
            return this.value.intValue() == ((FloatValue) obj).value.intValue();
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
