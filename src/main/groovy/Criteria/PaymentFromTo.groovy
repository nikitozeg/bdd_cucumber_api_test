package Criteria
/**
 * Created by ryabnat on 2/17/2016.
 */
class PaymentFromTo {

    String provider
    String amount
    String currency
    String identity


    private PaymentFromTo(Builder builder) {
        this.provider = builder.provider
        this.amount = builder.amount
        this.currency = builder.currency
        this.identity = builder.identity

    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        String provider = ""
        String amount = ""
        String currency = ""
        String identity = ""


        PaymentFromTo build() {
            new PaymentFromTo(this)
        }

        PaymentFromTo.Builder setProvider(String value) {
            if (value) {
                provider = value
            }
            this
        }

        PaymentFromTo.Builder setAmount(String value) {
            if (value) {
                if (value.equalsIgnoreCase("null"))
                    amount = null
                else
                    amount = value
            }
            this
        }

        PaymentFromTo.Builder setCurrency(String value) {
            if (value) {
                currency = value
            }
            this
        }

        PaymentFromTo.Builder setIdentity(String value) {
            if (value) {
                identity = value
            }
            this
        }

    }
}